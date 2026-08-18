package com.erp.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RawMaterialPurchaseItemRequest {

    private Long purchaseId;

    private Long rawMaterialId;

    private BigDecimal quantity;

    private BigDecimal unitPrice;
}