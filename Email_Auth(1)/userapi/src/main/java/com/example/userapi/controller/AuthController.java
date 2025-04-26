package com.example.userapi.controller;

import com.example.userapi.dto.AuthenticationRequest;
import com.example.userapi.dto.AuthenticationResponse;
import com.example.userapi.model.User;
import com.example.userapi.security.JwtTokenUtil;
import com.example.userapi.security.UserDetailsServiceImpl;
import com.example.userapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, 
                        UserDetailsServiceImpl userDetailsService,
                        JwtTokenUtil jwtTokenUtil,
                        UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) 
            throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getEmpId(), 
                            authenticationRequest.getPassword())
            );
        } 
        catch (BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect empId or password");
        }
        
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmpId());
        final String jwt = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }
}