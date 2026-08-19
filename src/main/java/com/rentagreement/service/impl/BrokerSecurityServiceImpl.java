package com.rentagreement.service.impl;

import com.rentagreement.dto.broker.DeletePasswordRequestDTO;
import com.rentagreement.entity.Broker;
import com.rentagreement.exception.ResourceNotFoundException;
import com.rentagreement.repository.BrokerRepository;
import com.rentagreement.service.BrokerSecurityService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class BrokerSecurityServiceImpl
        implements BrokerSecurityService {

    private final BrokerRepository brokerRepository;
    private final PasswordEncoder passwordEncoder;

    public BrokerSecurityServiceImpl(
            BrokerRepository brokerRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.brokerRepository = brokerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private Broker getCurrentBroker() {

        String username =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        return brokerRepository
                .findByUsernameAndDeletedFalse(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Logged-in broker not found"
                        )
                );
    }

    @Override
    public void setOrChangeDeletePassword(
            DeletePasswordRequestDTO request
    ) {

        Broker broker = getCurrentBroker();

        /*
         * Confirm new password
         */
        if (!request.getNewDeletePassword()
                .equals(request.getConfirmDeletePassword())) {

            throw new IllegalArgumentException(
                    "New delete password and confirm password do not match"
            );
        }

        /*
         * Delete password must be different
         * from login password.
         */
        if (passwordEncoder.matches(
                request.getNewDeletePassword(),
                broker.getPassword()
        )) {

            throw new IllegalArgumentException(
                    "Delete password must be different from login password"
            );
        }

        /*
         * If a delete password already exists,
         * current delete password is required.
         */
        if (broker.getDeletePassword() != null
                && !broker.getDeletePassword().isBlank()) {

            if (request.getCurrentDeletePassword() == null
                    || request.getCurrentDeletePassword().isBlank()) {

                throw new IllegalArgumentException(
                        "Current delete password is required"
                );
            }

            if (!passwordEncoder.matches(
                    request.getCurrentDeletePassword(),
                    broker.getDeletePassword()
            )) {

                throw new IllegalArgumentException(
                        "Current delete password is incorrect"
                );
            }
        }

        /*
         * Store only the BCrypt hash.
         */
        broker.setDeletePassword(
                passwordEncoder.encode(
                        request.getNewDeletePassword()
                )
        );

        brokerRepository.save(broker);
    }

    @Override
    public boolean hasDeletePassword() {

        Broker broker = getCurrentBroker();

        return broker.getDeletePassword() != null
                && !broker.getDeletePassword().isBlank();
    }

}