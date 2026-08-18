package com.erp.dto;

import java.util.List;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RawMaterialPurchaseRequest {

    private Long supplierId;

    private Long requestedBy;

    private String remarks;

    private List<RawMaterialPurchaseItemRequest> items;
}