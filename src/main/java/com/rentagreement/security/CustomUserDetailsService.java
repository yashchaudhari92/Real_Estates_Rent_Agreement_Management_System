package com.rentagreement.security;

import com.rentagreement.entity.Broker;
import com.rentagreement.repository.BrokerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final BrokerRepository brokerRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    public CustomUserDetailsService(BrokerRepository brokerRepository,
                                    PasswordEncoder passwordEncoder) {
        this.brokerRepository = brokerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        if (username.equals(adminUsername)) {

            return User.builder()
                    .username(adminUsername)
                    .password(passwordEncoder.encode(adminPassword))
                    .authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                    .build();
        }

        Broker broker = brokerRepository
                .findByUsernameAndDeletedFalse(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User Not Found"));

        return User.builder()
                .username(broker.getUsername())
                .password(broker.getPassword())
                .authorities(
                        List.of(new SimpleGrantedAuthority(
                                broker.getRole().name()
                        ))
                )
                .build();
    }
}