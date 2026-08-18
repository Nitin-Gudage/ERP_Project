package com.erp.entity;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HistoryAction action;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "reference_type", nullable = false)
    private String referenceType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "performed_by")
    private User performedBy;

    @Column(length = 1000)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}