package com.rentagreement.service.impl;

import com.rentagreement.dto.document.AgreementDocumentResponseDTO;
import com.rentagreement.entity.AgreementDocument;
import com.rentagreement.entity.Broker;
import com.rentagreement.entity.RentAgreement;
import com.rentagreement.exception.ResourceNotFoundException;
import com.rentagreement.repository.AgreementDocumentRepository;
import com.rentagreement.repository.BrokerRepository;
import com.rentagreement.repository.RentAgreementRepository;
import com.rentagreement.service.AgreementDocumentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
public class AgreementDocumentServiceImpl
        implements AgreementDocumentService {

    private final AgreementDocumentRepository documentRepository;
    private final RentAgreementRepository agreementRepository;
    private final BrokerRepository brokerRepository;

    @Value("${agreement.document.upload-dir}")
    private String uploadDir;

    private static final Set<String> ALLOWED_EXTENSIONS =
            Set.of("pdf", "doc", "docx");

    public AgreementDocumentServiceImpl(
            AgreementDocumentRepository documentRepository,
            RentAgreementRepository agreementRepository,
            BrokerRepository brokerRepository
    ) {

        this.documentRepository = documentRepository;
        this.agreementRepository = agreementRepository;
        this.brokerRepository = brokerRepository;

    }

    @Override
    public AgreementDocumentResponseDTO uploadDocument(
            Long agreementId,
            MultipartFile file
    ) {

        // Broker broker = getCurrentBroker();
        Broker broker = getMainBroker();

        RentAgreement agreement =
                agreementRepository
                        .findByIdAndBuildingBrokerIdAndDeletedFalse(
                                agreementId,
                                broker.getId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Agreement not found"
                                ));

        if (documentRepository
                .findByAgreementId(agreementId)
                .isPresent()) {

            throw new IllegalArgumentException(
                    "Agreement already has a document. Use replace document."
            );

        }

        validateFile(file);

        try {

            Path uploadPath =
                    Paths.get(uploadDir)
                            .toAbsolutePath()
                            .normalize();

            Files.createDirectories(uploadPath);

            String originalFileName =
                    file.getOriginalFilename();

            String extension =
                    getExtension(originalFileName);

            String storedFileName =
                    UUID.randomUUID()
                            + "."
                            + extension;

            Path targetPath =
                    uploadPath.resolve(storedFileName)
                            .normalize();

            if (!targetPath.startsWith(uploadPath)) {

                throw new IllegalArgumentException(
                        "Invalid file path"
                );

            }

            Files.copy(
                    file.getInputStream(),
                    targetPath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            AgreementDocument document =
                    AgreementDocument.builder()
                            .agreement(agreement)
                            .fileName(originalFileName)
                            .filePath(targetPath.toString())
                            .uploadDate(LocalDateTime.now())
                            .build();

            AgreementDocument savedDocument =
                    documentRepository.save(document);

            return mapToDTO(savedDocument);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to store document",
                    e
            );

        }

    }

    @Override
    public AgreementDocumentResponseDTO getDocument(
            Long agreementId
    ) {

        // Broker broker = getCurrentBroker();
        Broker broker = getMainBroker();

        // Verify that this agreement belongs
        // to the logged-in broker.
        RentAgreement agreement =
                agreementRepository
                        .findByIdAndBuildingBrokerIdAndDeletedFalse(
                                agreementId,
                                broker.getId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Agreement not found"
                                ));

        AgreementDocument document =
                documentRepository
                        .findByAgreementId(agreementId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Document not found"
                                ));

        return mapToDTO(document);
    }

    @Override
    public AgreementDocumentResponseDTO replaceDocument(
            Long agreementId,
            MultipartFile file
    ) {

        // Broker broker = getCurrentBroker();
        Broker broker = getMainBroker();

        RentAgreement agreement =
                agreementRepository
                        .findByIdAndBuildingBrokerIdAndDeletedFalse(
                                agreementId,
                                broker.getId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Agreement not found"
                                ));

        validateFile(file);

        AgreementDocument existingDocument =
                documentRepository
                        .findByAgreementId(agreementId)
                        .orElse(null);

        try {

            Path uploadPath =
                    Paths.get(uploadDir)
                            .toAbsolutePath()
                            .normalize();

            Files.createDirectories(uploadPath);

            String originalFileName =
                    file.getOriginalFilename();

            String extension =
                    getExtension(originalFileName);

            String storedFileName =
                    UUID.randomUUID()
                            + "."
                            + extension;

            Path targetPath =
                    uploadPath.resolve(storedFileName)
                            .normalize();

            if (!targetPath.startsWith(uploadPath)) {

                throw new IllegalArgumentException(
                        "Invalid file path"
                );

            }

            Files.copy(
                    file.getInputStream(),
                    targetPath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            if (existingDocument != null) {

                deletePhysicalFile(
                        existingDocument.getFilePath()
                );

                existingDocument.setFileName(
                        originalFileName
                );

                existingDocument.setFilePath(
                        targetPath.toString()
                );

                existingDocument.setUploadDate(
                        LocalDateTime.now()
                );

                AgreementDocument updatedDocument =
                        documentRepository.save(
                                existingDocument
                        );

                return mapToDTO(updatedDocument);

            }

            AgreementDocument newDocument =
                    AgreementDocument.builder()
                            .agreement(agreement)
                            .fileName(originalFileName)
                            .filePath(targetPath.toString())
                            .uploadDate(LocalDateTime.now())
                            .build();

            AgreementDocument savedDocument =
                    documentRepository.save(newDocument);

            return mapToDTO(savedDocument);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to replace document",
                    e
            );

        }

    }

    @Override
    public void deleteDocument(
            Long agreementId
    ) {

        // Broker broker = getCurrentBroker();
        Broker broker = getMainBroker();

        RentAgreement agreement =
                agreementRepository
                        .findByIdAndBuildingBrokerIdAndDeletedFalse(
                                agreementId,
                                broker.getId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Agreement not found"
                                ));

        AgreementDocument document =
                documentRepository
                        .findByAgreementId(agreementId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Document not found"
                                ));

        deletePhysicalFile(
                document.getFilePath()
        );

        documentRepository.delete(document);

    }

    private Broker getCurrentBroker() {

        String username =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        return brokerRepository
                .findByUsernameAndDeletedFalse(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Logged-in broker not found"
                        ));

    }

    private Broker getMainBroker() {

        Broker currentBroker =
                getCurrentBroker();

        if (currentBroker.getParentBroker() == null) {
            return currentBroker;
        }

        return currentBroker.getParentBroker();
    }

    private void validateFile(
            MultipartFile file
    ) {

        if (file == null || file.isEmpty()) {

            throw new IllegalArgumentException(
                    "Please select a document"
            );

        }

        String originalFileName =
                file.getOriginalFilename();

        if (originalFileName == null ||
                originalFileName.isBlank()) {

            throw new IllegalArgumentException(
                    "Invalid file name"
            );

        }

        String extension =
                getExtension(originalFileName);

        if (!ALLOWED_EXTENSIONS
                .contains(extension.toLowerCase())) {

            throw new IllegalArgumentException(
                    "Only PDF, DOC and DOCX files are supported"
            );

        }

    }

    private String getExtension(
            String fileName
    ) {

        int lastDot =
                fileName.lastIndexOf(".");

        if (lastDot == -1 ||
                lastDot == fileName.length() - 1) {

            throw new IllegalArgumentException(
                    "File extension is required"
            );

        }

        return fileName
                .substring(lastDot + 1)
                .toLowerCase();

    }

    private void deletePhysicalFile(
            String filePath
    ) {

        if (filePath == null ||
                filePath.isBlank()) {

            return;

        }

        try {

            Files.deleteIfExists(
                    Paths.get(filePath)
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to delete document file",
                    e
            );

        }

    }

    private AgreementDocumentResponseDTO mapToDTO(
            AgreementDocument document
    ) {

        return AgreementDocumentResponseDTO.builder()

                .id(document.getId())

                .agreementId(
                        document.getAgreement().getId()
                )

                .fileName(
                        document.getFileName()
                )

                .filePath(
                        document.getFilePath()
                )

                .uploadDate(
                        document.getUploadDate()
                )

                .build();

    }

}