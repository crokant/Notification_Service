package com.icispp.notificationservice.services;


import com.icispp.notificationservice.models.CustomUserDetails;
import com.icispp.notificationservice.models.User;

import com.icispp.notificationservice.repositories.SqlUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final SqlUserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(SqlUserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new CustomUserDetails(user);
    }
}
