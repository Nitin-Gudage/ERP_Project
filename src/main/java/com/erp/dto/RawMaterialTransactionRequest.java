package com.erp.dto;

import java.math.BigDecimal;

import com.erp.entity.RawMaterialTransactionType;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RawMaterialTransactionRequest {

    private Long rawMaterialId;

    private RawMaterialTransactionType transactionType;

    private BigDecimal quantity;

    private Long productionId;

    private Long purchaseId;

    private Long createdBy;

    private String remarks;
}