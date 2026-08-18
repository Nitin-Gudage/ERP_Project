package com.erp.dto;

import java.math.BigDecimal;

import com.erp.entity.GoodsTransactionType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoodsTransactionRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Transaction type is required")
    private GoodsTransactionType transactionType;

    @NotNull(message = "Quantity is required")
    @DecimalMin(
            value = "0.001",
            message = "Quantity must be greater than 0"
    )
    private BigDecimal quantity;

    private Long productionId;

    private Long createdBy;

    @Size(max = 500)
    private String remarks;
}