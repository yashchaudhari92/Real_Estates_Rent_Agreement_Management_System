package com.rentagreement.service;

import com.rentagreement.dto.agreement.AgreementRequestDTO;
import com.rentagreement.dto.agreement.AgreementResponseDTO;
import com.rentagreement.dto.agreement.AgreementUpdateRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RentAgreementService {

    AgreementResponseDTO createAgreement(
            AgreementRequestDTO request
    );

    Page<AgreementResponseDTO> getAllAgreements(
            String keyword,
            Pageable pageable
    );

    AgreementResponseDTO getAgreementById(
            Long id
    );

    AgreementResponseDTO updateAgreement(
            Long id,
            AgreementUpdateRequestDTO request
    );

    void deleteAgreement(
            Long id
    );

}