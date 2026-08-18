package com.erp.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.erp.entity.RawMaterialPurchaseStatus;

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
public class RawMaterialPurchaseDetailsResponse {

    private Long id;

    private String purchaseNumber;

    private Long productionId;

    private String productionNumber;

    private Long requestedBy;

    private String requestedByUsername;

    private RawMaterialPurchaseStatus status;

    private String remarks;

    private Long approvedBy;

    private String approvedByUsername;

    private LocalDateTime approvedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<RawMaterialPurchaseItemDetailsResponse> items;
}