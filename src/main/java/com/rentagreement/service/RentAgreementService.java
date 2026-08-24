package com.rentagreement.service;

import com.rentagreement.dto.agreement.AgreementRequestDTO;
import com.rentagreement.dto.agreement.AgreementResponseDTO;
import com.rentagreement.dto.agreement.AgreementUpdateRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.rentagreement.dto.agreement.AgreementRenewalRequestDTO;

import java.util.List;

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

    List<AgreementResponseDTO> getAgreementHistory(
            Long id
    );

    AgreementResponseDTO renewAgreement(
            Long id,
            AgreementRenewalRequestDTO request
    );

    AgreementResponseDTO updateAgreement(
            Long id,
            AgreementUpdateRequestDTO request
    );

    List<AgreementResponseDTO> getAgreementsByUser(
            Long userId
    );

    void deleteAgreement(
            Long id,
            String deletePassword
    );

}