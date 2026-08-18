package com.erp.dto;

import com.erp.entity.HistoryAction;

import jakarta.validation.constraints.NotBlank;
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
public class HistoryRequest {

    @NotNull(message = "Action is required")
    private HistoryAction action;

    private Long referenceId;

    @NotBlank(message = "Reference type is required")
    private String referenceType;

    private Long performedBy;

    private String description;
}