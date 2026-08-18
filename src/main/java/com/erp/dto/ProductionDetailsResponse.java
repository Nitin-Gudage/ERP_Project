package com.erp.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.erp.entity.ProductionStatus;

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
public class ProductionDetailsResponse {

    private Long id;

    private String productionNumber;

    private Long salesOrderId;

    private String salesOrderNumber;

    private Long requestedBy;

    private String requestedByUsername;

    private ProductionStatus status;

    private String remarks;

    private Long approvedBy;

    private String approvedByUsername;

    private LocalDateTime approvedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<ProductionItemDetailsResponse> items;
}