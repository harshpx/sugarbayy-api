package com.store.sugarbayy.configs.security;

import com.store.sugarbayy.repos.CustomerRepo;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerUserDetailsService implements UserDetailsService {
    private final CustomerRepo customerRepo;
    @Override
    @NonNull
    public UserDetails loadUserByUsername(@NonNull String identifier) throws UsernameNotFoundException {
        return customerRepo.findByIdentifier(identifier)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials / user not found"));
    }
}
