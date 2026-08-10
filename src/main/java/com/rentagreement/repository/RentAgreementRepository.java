package com.rentagreement.repository;

import com.rentagreement.entity.RentAgreement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RentAgreementRepository
        extends JpaRepository<RentAgreement, Long> {

    Page<RentAgreement>
    findByBuildingBrokerIdAndDeletedFalse(
            Long brokerId,
            Pageable pageable
    );

    Page<RentAgreement>
    findByBuildingBrokerIdAndOwnerNameContainingIgnoreCaseAndDeletedFalse(
            Long brokerId,
            String ownerName,
            Pageable pageable
    );

    Optional<RentAgreement>
    findByIdAndBuildingBrokerIdAndDeletedFalse(
            Long id,
            Long brokerId
    );

}

//package com.rentagreement.repository;
//
//import com.rentagreement.entity.RentAgreement;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//public interface RentAgreementRepository
//        extends JpaRepository<RentAgreement, Long> {
//
//    // View All Agreements (Pagination)
//    Page<RentAgreement> findAllByDeletedFalse(Pageable pageable);
//
//    // Search Agreement by Owner Name
//    Page<RentAgreement> findByOwnerNameContainingIgnoreCaseAndDeletedFalse(
//            String ownerName,
//            Pageable pageable
//    );
//
//}