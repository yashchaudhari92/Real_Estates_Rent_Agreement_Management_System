package com.rentagreement.repository;

import com.rentagreement.entity.AgreementDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgreementDocumentRepository
        extends JpaRepository<AgreementDocument, Long> {

    Optional<AgreementDocument> findByAgreementId(Long agreementId);

}