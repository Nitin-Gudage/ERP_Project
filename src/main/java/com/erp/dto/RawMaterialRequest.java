package com.erp.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class RawMaterialRequest {

    @NotBlank(message = "Material code is required")
    @Size(max = 50, message = "Material code must not exceed 50 characters")
    private String materialCode;

    @NotBlank(message = "Material name is required")
    @Size(max = 150, message = "Material name must not exceed 150 characters")
    private String materialName;

    @NotNull(message = "Available stock is required")
    @DecimalMin(value = "0.0", inclusive = true,
            message = "Available stock cannot be negative")
    private BigDecimal availableStock;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private Boolean isActive;
}