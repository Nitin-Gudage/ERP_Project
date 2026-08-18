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
public class GoodsStockRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Stock quantity is required")
    @DecimalMin(
            value = "0.000",
            message = "Stock cannot be negative"
    )
    private BigDecimal availableStock;
}