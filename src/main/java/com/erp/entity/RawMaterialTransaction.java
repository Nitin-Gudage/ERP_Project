package com.erp.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "raw_material_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RawMaterialTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "raw_material_id", nullable = false)
    private RawMaterial rawMaterial;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private RawMaterialTransactionType transactionType;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal quantity;

    @Column(name = "stock_before", nullable = false, precision = 12, scale = 3)
    private BigDecimal stockBefore;

    @Column(name = "stock_after", nullable = false, precision = 12, scale = 3)
    private BigDecimal stockAfter;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "production_id")
    private Production production;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "purchase_id")
    private RawMaterialPurchase purchase;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(length = 500)
    private String remarks;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}