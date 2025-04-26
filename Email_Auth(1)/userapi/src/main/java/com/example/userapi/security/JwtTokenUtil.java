package com.example.userapi.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);
    
    @Value("${jwt.secret}")
    private String secretKey;
    
    @Value("${jwt.expiration}")
    private long jwtTokenValidity;
    
    @PostConstruct
    public void init() {
        if (secretKey == null || secretKey.isEmpty()) {
            SecureRandom random = new SecureRandom();
            byte[] bytes = new byte[64]; // 512 bits
            random.nextBytes(bytes);
            secretKey = Base64.getEncoder().encodeToString(bytes);
            logger.info("Generated new JWT secret key");
        }
    }
    
    public String getUsernameFromToken(String token) throws JwtException {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) throws JwtException {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) throws JwtException {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) throws JwtException {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException ex) {
            logger.warn("JWT Token expired: {}", ex.getMessage());
            throw ex;
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT Token: {}", ex.getMessage());
            throw new JwtException("Unsupported JWT token");
        } catch (MalformedJwtException ex) {
            logger.error("Malformed JWT Token: {}", ex.getMessage());
            throw new JwtException("Malformed JWT token");
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature: {}", ex.getMessage());
            throw new JwtException("Invalid JWT signature");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty: {}", ex.getMessage());
            throw new JwtException("JWT claims string is empty");
        }
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Boolean isTokenExpired(String token) throws JwtException {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userDetails.getUsername());
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtTokenValidity * 1000))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) throws JwtException {
        try {
            final String username = getUsernameFromToken(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (JwtException ex) {
            logger.error("JWT validation error: {}", ex.getMessage());
            throw ex;
        }
    }
}