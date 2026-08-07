package com.rentagreement.entity;

import com.rentagreement.enums.PropertyType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "buildings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String buildingName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyType propertyType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "broker_id", nullable = false)
    private Broker broker;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(nullable = false)
    private boolean deleted;

    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
        this.deleted = false;
    }

}