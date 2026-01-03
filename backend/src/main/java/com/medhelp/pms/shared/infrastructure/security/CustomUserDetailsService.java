package com.medhelp.pms.shared.infrastructure.security;

import com.medhelp.pms.modules.auth_module.domain.repositories.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final AuthRepository authRepository;


    /**
     * @param usernameOrEmail
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        return authRepository.findByUsernameOrEmail(usernameOrEmail).orElseThrow(() -> new UsernameNotFoundException("Invalid username or password"));
    }
}
