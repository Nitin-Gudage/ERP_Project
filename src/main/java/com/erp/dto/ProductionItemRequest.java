package com.erp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
public class ProductionItemRequest {

    @NotNull(message = "Production ID is required")
    private Long productionId;

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Required quantity is required")
    @Min(value = 1, message = "Required quantity must be at least 1")
    private Integer requiredQuantity;
}