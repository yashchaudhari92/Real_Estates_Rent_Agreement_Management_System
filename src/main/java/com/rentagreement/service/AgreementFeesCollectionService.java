package com.rentagreement.service;

import com.rentagreement.dto.fees.AgreementFeesCollectionResponseDTO;

import java.time.LocalDate;

public interface AgreementFeesCollectionService {

    AgreementFeesCollectionResponseDTO getFeesCollection(
            LocalDate fromDate,
            LocalDate toDate
    );

}