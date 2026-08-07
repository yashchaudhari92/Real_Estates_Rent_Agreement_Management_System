package com.rentagreement.repository;

import com.rentagreement.entity.Broker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BrokerRepository extends JpaRepository<Broker, Long> {

    Optional<Broker> findByUsernameAndDeletedFalse(String username);

    Optional<Broker> findByEmailAndDeletedFalse(String email);

    List<Broker> findAllByDeletedFalse();

    Page<Broker> findByDeletedFalse(Pageable pageable);

    Page<Broker> findByDeletedFalseAndBrokerNameContainingIgnoreCaseOrDeletedFalseAndCompanyNameContainingIgnoreCaseOrDeletedFalseAndEmailContainingIgnoreCase(
            String brokerName,
            String companyName,
            String email,
            Pageable pageable
    );

    List<Broker> findAllByDeletedFalseOrderByIdDesc();

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

}