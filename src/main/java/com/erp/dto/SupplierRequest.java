package com.erp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class SupplierRequest {

    @NotBlank(message = "Supplier name is required")
    @Size(max = 150, message = "Supplier name must not exceed 150 characters")
    private String supplierName;

    @Size(max = 100, message = "Contact person must not exceed 100 characters")
    private String contactPerson;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phone;

    @Email(message = "Invalid email format")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    @Size(max = 30, message = "GST number must not exceed 30 characters")
    private String gstNumber;

    private Boolean isActive;
}