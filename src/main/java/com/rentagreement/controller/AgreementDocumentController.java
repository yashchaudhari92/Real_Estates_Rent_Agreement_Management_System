package com.rentagreement.controller;

import com.rentagreement.dto.document.AgreementDocumentResponseDTO;
import com.rentagreement.service.AgreementDocumentService;
import com.rentagreement.response.ApiResponse;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/broker/agreements")
@CrossOrigin(origins = "http://localhost:5173")
public class AgreementDocumentController {

    private final AgreementDocumentService documentService;

    public AgreementDocumentController(
            AgreementDocumentService documentService
    ) {
        this.documentService = documentService;
    }

    // Upload Document
    @PostMapping(
            value = "/{agreementId}/document",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<AgreementDocumentResponseDTO>>
    uploadDocument(
            @PathVariable Long agreementId,
            @RequestParam("file") MultipartFile file
    ) {

        AgreementDocumentResponseDTO document =
                documentService.uploadDocument(
                        agreementId,
                        file
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Agreement Document Uploaded Successfully",
                        document
                )
        );
    }

    // Download Document
    @GetMapping("/{agreementId}/document")
    public ResponseEntity<Resource> downloadDocument(
            @PathVariable Long agreementId
    ) {

        AgreementDocumentResponseDTO document =
                documentService.getDocument(
                        agreementId
                );

        try {

            Path path =
                    Paths.get(document.getFilePath())
                            .toAbsolutePath()
                            .normalize();

            Resource resource =
                    new UrlResource(
                            path.toUri()
                    );

            if (!resource.exists() ||
                    !resource.isReadable()) {

                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(
                            MediaType.APPLICATION_OCTET_STREAM
                    )
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            ContentDisposition
                                    .attachment()
                                    .filename(
                                            document.getFileName()
                                    )
                                    .build()
                                    .toString()
                    )
                    .body(resource);

        } catch (MalformedURLException e) {

            return ResponseEntity.notFound().build();

        }
    }

    // Replace Document
    @PutMapping(
            value = "/{agreementId}/document",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<AgreementDocumentResponseDTO>>
    replaceDocument(
            @PathVariable Long agreementId,
            @RequestParam("file") MultipartFile file
    ) {

        AgreementDocumentResponseDTO document =
                documentService.replaceDocument(
                        agreementId,
                        file
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Agreement Document Replaced Successfully",
                        document
                )
        );
    }

    // Delete Document
    @DeleteMapping("/{agreementId}/document")
    public ResponseEntity<ApiResponse<String>>
    deleteDocument(
            @PathVariable Long agreementId
    ) {

        documentService.deleteDocument(
                agreementId
        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Agreement Document Deleted Successfully",
                        null
                )
        );
    }
}