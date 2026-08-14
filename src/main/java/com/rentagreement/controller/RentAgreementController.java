package com.rentagreement.controller;

import com.rentagreement.dto.agreement.AgreementRequestDTO;
import com.rentagreement.dto.agreement.AgreementResponseDTO;
import com.rentagreement.dto.agreement.AgreementUpdateRequestDTO;
import com.rentagreement.dto.agreement.AgreementRenewalRequestDTO;
import com.rentagreement.response.ApiResponse;
import com.rentagreement.service.RentAgreementService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/broker/agreements")
@CrossOrigin(origins = "http://localhost:5173")
public class RentAgreementController {

    private final RentAgreementService agreementService;

    public RentAgreementController(
            RentAgreementService agreementService
    ) {
        this.agreementService = agreementService;
    }

    // Create Agreement
    @PostMapping
    public ResponseEntity<ApiResponse<AgreementResponseDTO>> createAgreement(
            @Valid @RequestBody AgreementRequestDTO request
    ) {

        AgreementResponseDTO agreement =
                agreementService.createAgreement(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Agreement Created Successfully",
                        agreement
                )
        );

    }

    // View All Agreements
    @GetMapping
    public ResponseEntity<ApiResponse<Page<AgreementResponseDTO>>> getAllAgreements(

            @RequestParam(defaultValue = "") String keyword,

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "10") int size

    ) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Agreement List Fetched Successfully",
                        agreementService.getAllAgreements(
                                keyword,
                                pageable
                        )
                )
        );

    }

    // View Agreement By Id
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AgreementResponseDTO>> getAgreementById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Agreement Details",
                        agreementService.getAgreementById(id)
                )
        );

    }

    // View Agreement History
    @GetMapping("/{id}/history")
    public ResponseEntity<ApiResponse<List<AgreementResponseDTO>>> getAgreementHistory(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Agreement History Fetched Successfully",
                        agreementService.getAgreementHistory(id)
                )
        );

    }

    // Renew Agreement
    @PostMapping("/{id}/renew")
    public ResponseEntity<ApiResponse<AgreementResponseDTO>> renewAgreement(
            @PathVariable Long id,
            @Valid @RequestBody AgreementRenewalRequestDTO request
    ) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Agreement Renewed Successfully",
                        agreementService.renewAgreement(
                                id,
                                request
                        )
                )
        );

    }

    // Update Agreement
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AgreementResponseDTO>> updateAgreement(
            @PathVariable Long id,
            @Valid @RequestBody AgreementUpdateRequestDTO request
    ) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Agreement Updated Successfully",
                        agreementService.updateAgreement(id, request)
                )
        );

    }

    // Soft Delete Agreement
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteAgreement(
            @PathVariable Long id
    ) {

        agreementService.deleteAgreement(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Agreement Deleted Successfully",
                        null
                )
        );

    }

}