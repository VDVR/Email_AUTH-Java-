package com.example.userapi.security;

import com.example.userapi.model.User;
import com.example.userapi.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String empId) throws UsernameNotFoundException {
        User user = userRepository.findByEmpId(empId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("User not found with empId: " + empId));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmpId())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }
}