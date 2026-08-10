package com.rentagreement.dto.document;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgreementDocumentResponseDTO {

    private Long id;

    private Long agreementId;

    private String fileName;

    private String filePath;

    private LocalDateTime uploadDate;

}