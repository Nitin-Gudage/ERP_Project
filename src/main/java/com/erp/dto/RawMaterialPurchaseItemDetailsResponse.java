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
public class RawMaterialPurchaseItemDetailsResponse {

    private Long id;

    private Long rawMaterialId;

    private String rawMaterialName;

    private Integer requiredQuantity;

    private Integer receivedQuantity;

    private BigDecimal estimatedUnitPrice;
}