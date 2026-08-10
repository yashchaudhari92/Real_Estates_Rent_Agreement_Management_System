package com.rentagreement.service;

import com.rentagreement.dto.document.AgreementDocumentResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface AgreementDocumentService {

    AgreementDocumentResponseDTO uploadDocument(
            Long agreementId,
            MultipartFile file
    );

    AgreementDocumentResponseDTO getDocument(
            Long agreementId
    );

    AgreementDocumentResponseDTO replaceDocument(
            Long agreementId,
            MultipartFile file
    );

    void deleteDocument(
            Long agreementId
    );

}