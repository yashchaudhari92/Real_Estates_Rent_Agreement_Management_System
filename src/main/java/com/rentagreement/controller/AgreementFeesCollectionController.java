package com.rentagreement.controller;

import com.rentagreement.dto.fees.AgreementFeesCollectionResponseDTO;
import com.rentagreement.response.ApiResponse;
import com.rentagreement.service.AgreementFeesCollectionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/broker/fees-collection")
@CrossOrigin(origins = "http://localhost:5173")
public class AgreementFeesCollectionController {

    private final AgreementFeesCollectionService feesCollectionService;


    public AgreementFeesCollectionController(
            AgreementFeesCollectionService feesCollectionService
    ) {

        this.feesCollectionService =
                feesCollectionService;

    }


    @GetMapping
    public ResponseEntity<
            ApiResponse<AgreementFeesCollectionResponseDTO>
            > getFeesCollection(

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate toDate

    ) {

        return ResponseEntity.ok(

                new ApiResponse<>(

                        true,

                        "Agreement Fees Collection Fetched Successfully",

                        feesCollectionService
                                .getFeesCollection(
                                        fromDate,
                                        toDate
                                )

                )

        );

    }

}