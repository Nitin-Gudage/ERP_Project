package com.erp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.erp.entity.SalesOrderStatus;

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
public class SalesOrderDetailsResponse {

    private Long id;

    private String orderNumber;

    private Long customerId;

    private String customerName;

    private SalesOrderStatus status;

    private BigDecimal totalAmount;

    private String remarks;

    /*
     * Dispatch details
     */
    private String dispatchTrackingId;

    private Long dispatchApprovedBy;

    private String dispatchApprovedByUsername;

    private LocalDateTime dispatchApprovedAt;

    private LocalDateTime dispatchedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<SalesOrderItemDetailsResponse> items;
}