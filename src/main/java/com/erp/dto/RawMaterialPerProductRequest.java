package com.erp.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
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
public class RawMaterialPerProductRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Raw material ID is required")
    private Long rawMaterialId;

    @NotNull(message = "Required quantity is required")
    @DecimalMin(
        value = "0.0",
        inclusive = false,
        message = "Required quantity must be greater than 0"
    )
    private BigDecimal requiredQuantity;
}