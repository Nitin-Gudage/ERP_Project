package com.erp.dto;

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
public class ProductionItemDetailsResponse {

    private Long id;

    private Long productId;

    private String productName;

    private Integer requiredQuantity;

    private Integer completedQuantity;
}