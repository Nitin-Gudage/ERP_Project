package com.erp.dto;

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
public class ProductionRequest {

    private Long salesOrderId;

    @NotNull(message = "Requested by user ID is required")
    private Long requestedBy;

    @Size(max = 500, message = "Remarks must not exceed 500 characters")
    private String remarks;
}