package com.erp.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "raw_material_purchase_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RawMaterialPurchaseItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "purchase_id",
            nullable = false
    )
    private RawMaterialPurchase purchase;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "raw_material_id",
            nullable = false
    )
    private RawMaterial rawMaterial;


    @Column(
            nullable = false,
            precision = 12,
            scale = 3
    )
    private BigDecimal quantity;


    @Column(
            name = "unit_price",
            precision = 12,
            scale = 2
    )
    private BigDecimal unitPrice;


    @Column(
            name = "total_price",
            precision = 12,
            scale = 2
    )
    private BigDecimal totalPrice;
}