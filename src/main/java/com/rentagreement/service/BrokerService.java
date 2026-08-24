package com.rentagreement.service;

import com.rentagreement.dto.auth.BrokerRegisterRequestDTO;
import com.rentagreement.dto.auth.BrokerResponseDTO;
import com.rentagreement.dto.broker.BrokerListResponseDTO;
import com.rentagreement.dto.broker.BrokerUpdateRequestDTO;
import com.rentagreement.dto.broker.BrokerUserCreateRequestDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BrokerService {

    // ==========================================================
    // Existing Admin Broker Management
    // ==========================================================

    BrokerResponseDTO addBroker(
            BrokerRegisterRequestDTO request
    );

    Page<BrokerListResponseDTO> getAllBrokers(
            int page,
            int size,
            String keyword,
            String sortBy,
            String direction
    );

    BrokerListResponseDTO getBrokerById(
            Long id
    );

    BrokerListResponseDTO updateBroker(
            Long id,
            BrokerUpdateRequestDTO request
    );

    void activateBroker(
            Long id
    );

    void deactivateBroker(
            Long id
    );

    void deleteBroker(
            Long id
    );

    // ==========================================================
    // Main Broker -> Sub User Management
    // ==========================================================

    BrokerResponseDTO createUser(
            BrokerUserCreateRequestDTO request
    );

    List<BrokerListResponseDTO> getMyUsers();

    void activateUser(
            Long userId
    );

    void deactivateUser(
            Long userId
    );

    void deleteUser(
            Long userId
    );

    boolean isMainBroker();
}


//package com.rentagreement.service;
//
//import com.rentagreement.dto.auth.BrokerRegisterRequestDTO;
//import com.rentagreement.dto.auth.BrokerResponseDTO;
//import com.rentagreement.dto.broker.BrokerListResponseDTO;
//import com.rentagreement.dto.broker.BrokerUpdateRequestDTO;
//import org.springframework.data.domain.Page;
//
//import java.util.List;
//
//public interface BrokerService {
//
//    // NEW
//    BrokerResponseDTO addBroker(BrokerRegisterRequestDTO request);
//
//    Page<BrokerListResponseDTO> getAllBrokers(
//            int page,
//            int size,
//            String keyword,
//            String sortBy,
//            String direction
//    );
//
//    BrokerListResponseDTO getBrokerById(Long id);
//
//    BrokerListResponseDTO updateBroker(
//            Long id,
//            BrokerUpdateRequestDTO request
//    );
//
//    void activateBroker(Long id);
//
//    void deactivateBroker(Long id);
//
//    void deleteBroker(Long id);
//
//}