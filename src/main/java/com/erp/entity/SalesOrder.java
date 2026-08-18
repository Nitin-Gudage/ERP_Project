package com.erp.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sales_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false, unique = true)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SalesOrderStatus status = SalesOrderStatus.PENDING_APPROVAL;

    @Column(name = "total_amount", precision = 12, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "dispatch_tracking_id", unique = true)
    private String dispatchTrackingId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dispatch_approved_by")
    private User dispatchApprovedBy;

    @Column(name = "dispatch_approved_at")
    private LocalDateTime dispatchApprovedAt;

    @Column(name = "dispatched_at")
    private LocalDateTime dispatchedAt;

    @Column(length = 500)
    private String remarks;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}