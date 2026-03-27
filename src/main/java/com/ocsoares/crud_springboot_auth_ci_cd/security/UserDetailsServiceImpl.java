package com.ocsoares.crud_springboot_auth_ci_cd.security;

import com.ocsoares.crud_springboot_auth_ci_cd.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        return this.userRepository.findByEmail(email)
                                  .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}