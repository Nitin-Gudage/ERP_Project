package com.erp.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.erp.dto.SalesOrderDetailsResponse;
import com.erp.dto.SalesOrderItemDetailsResponse;
import com.erp.dto.SalesOrderRequest;
import com.erp.entity.Customer;
import com.erp.entity.HistoryAction;
import com.erp.entity.SalesOrder;
import com.erp.entity.SalesOrderItem;
import com.erp.entity.SalesOrderStatus;
import com.erp.entity.User;
import com.erp.repository.CustomerRepository;
import com.erp.repository.SalesOrderItemRepository;
import com.erp.repository.SalesOrderRepository;
import com.erp.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SalesOrderService {
    private final SalesOrderRepository salesOrderRepository;
    private final SalesOrderItemRepository salesOrderItemRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final HistoryService historyService;
    private final GoodsTransactionService goodsTransactionService;
    private final Random random = new Random();

    @Transactional
    public SalesOrder create(SalesOrderRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + request.getCustomerId()));

        SalesOrder order = SalesOrder.builder()
                .orderNumber(generateOrderNumber())
                .customer(customer)
                .status(SalesOrderStatus.PENDING_APPROVAL)
                .totalAmount(BigDecimal.ZERO)
                .remarks(request.getRemarks())
                .build();

        SalesOrder savedOrder = salesOrderRepository.save(order);
        historyService.createHistory(HistoryAction.SALES_ORDER_CREATED, "SALES_ORDER", savedOrder.getId(), null, "Sales order " + savedOrder.getOrderNumber() + " created");
        return savedOrder;
    }

    public List<SalesOrder> getAll() {
        return salesOrderRepository.findAll();
    }

    public SalesOrder getById(Long id) {
        return salesOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sales order not found with id: " + id));
    }

    public SalesOrderDetailsResponse getOrderDetails(Long id) {
        SalesOrder order = getById(id);
        List<SalesOrderItem> items = salesOrderItemRepository.findBySalesOrderId(id);

        List<SalesOrderItemDetailsResponse> itemDetails = items.stream()
                .map(item -> SalesOrderItemDetailsResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getProductName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .totalPrice(item.getTotalPrice())
                        .build())
                .toList();

        return SalesOrderDetailsResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomer().getId())
                .customerName(order.getCustomer().getCustomerName())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .remarks(order.getRemarks())
                .dispatchTrackingId(order.getDispatchTrackingId())
                .dispatchApprovedBy(order.getDispatchApprovedBy() != null ? order.getDispatchApprovedBy().getId() : null)
                .dispatchApprovedByUsername(order.getDispatchApprovedBy() != null ? order.getDispatchApprovedBy().getUsername() : null)
                .dispatchApprovedAt(order.getDispatchApprovedAt())
                .dispatchedAt(order.getDispatchedAt())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(itemDetails)
                .build();
    }

    @Transactional
    public SalesOrder update(Long id, SalesOrderRequest request) {
        SalesOrder order = getById(id);
        if (order.getStatus() != SalesOrderStatus.PENDING_APPROVAL) {
            throw new RuntimeException("Only pending orders can be updated");
        }

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + request.getCustomerId()));

        order.setCustomer(customer);
        order.setRemarks(request.getRemarks());

        SalesOrder updatedOrder = salesOrderRepository.save(order);
        historyService.createHistory(HistoryAction.SALES_ORDER_UPDATED, "SALES_ORDER", updatedOrder.getId(), null, "Sales order " + updatedOrder.getOrderNumber() + " updated");
        return updatedOrder;
    }

    public String delete(Long id) {
        SalesOrder order = getById(id);
        if (order.getStatus() != SalesOrderStatus.PENDING_APPROVAL) {
            throw new RuntimeException("Only pending orders can be deleted");
        }
        salesOrderRepository.delete(order);
        return "Sales order deleted successfully with id : " + id;
    }

    @Transactional
    public void recalculateTotal(Long salesOrderId) {
        SalesOrder order = getById(salesOrderId);
        List<SalesOrderItem> items = salesOrderItemRepository.findBySalesOrderId(salesOrderId);

        BigDecimal total = items.stream()
                .map(SalesOrderItem::getTotalPrice)
                .filter(price -> price != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(total);
        salesOrderRepository.save(order);
    }

    @Transactional
    public SalesOrder changeStatus(Long id, String status, Long userId) {
        SalesOrder order = getById(id);
        String upperStatus = status.trim().toUpperCase();

        if (upperStatus.equals("APPROVED")) {
            return approveSalesOrder(order, userId);
        }

        SalesOrderStatus newStatus;
        try {
            newStatus = SalesOrderStatus.valueOf(upperStatus);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid sales order status: " + status);
        }

        SalesOrderStatus currentStatus = order.getStatus();
        validateStatusChange(currentStatus, newStatus);

        if (newStatus == SalesOrderStatus.DISPATCH_APPROVED) {
            approveDispatch(order, userId);
        }

        if (newStatus == SalesOrderStatus.DISPATCHED) {
            dispatchOrder(order, userId);
        }

        order.setStatus(newStatus);
        SalesOrder savedOrder = salesOrderRepository.save(order);

        HistoryAction historyAction = getHistoryAction(newStatus);
        historyService.createHistory(historyAction, "SALES_ORDER", savedOrder.getId(), userId, "Sales order " + savedOrder.getOrderNumber() + " status changed from " + currentStatus + " to " + newStatus);
        return savedOrder;
    }

    @Transactional
    private SalesOrder approveSalesOrder(SalesOrder order, Long userId) {
        if (order.getStatus() != SalesOrderStatus.PENDING_APPROVAL) {
            throw new RuntimeException("Only pending orders can be approved");
        }
        if (userId == null) {
            throw new RuntimeException("User ID is required for approval");
        }

        User manager = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        order.setStatus(SalesOrderStatus.APPROVED);
        SalesOrder approvedOrder = salesOrderRepository.save(order);
        historyService.createHistory(HistoryAction.SALES_ORDER_APPROVED, "SALES_ORDER", approvedOrder.getId(), userId, "Sales order " + approvedOrder.getOrderNumber() + " approved by manager " + manager.getUsername());

        boolean allGoodsAvailable = isCompleteOrderAvailable(approvedOrder.getId());

        if (allGoodsAvailable) {
            approvedOrder.setStatus(SalesOrderStatus.PENDING_DISPATCH);
            SalesOrder savedOrder = salesOrderRepository.save(approvedOrder);
            historyService.createHistory(HistoryAction.SALES_ORDER_READY_FOR_DISPATCH, "SALES_ORDER", savedOrder.getId(), userId, "All products are available in warehouse. Order is ready for dispatch.");
            return savedOrder;
        }

        approvedOrder.setStatus(SalesOrderStatus.PRODUCTION_REQUIRED);
        SalesOrder savedOrder = salesOrderRepository.save(approvedOrder);
        historyService.createHistory(HistoryAction.PRODUCTION_REQUIRED, "SALES_ORDER", savedOrder.getId(), userId, "Required goods are not completely available. Production is required.");
        return savedOrder;
    }

    private boolean isCompleteOrderAvailable(Long salesOrderId) {
        try {
            goodsTransactionService.validateFullOrderStock(salesOrderId);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    private void approveDispatch(SalesOrder order, Long userId) {
        if (userId == null) {
            throw new RuntimeException("User ID is required for dispatch approval");
        }

        User manager = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        goodsTransactionService.validateFullOrderStock(order.getId());
        order.setDispatchTrackingId(generateTrackingId());
        order.setDispatchApprovedBy(manager);
        order.setDispatchApprovedAt(LocalDateTime.now());
    }

    private void dispatchOrder(SalesOrder order, Long userId) {
        if (order.getDispatchTrackingId() == null) {
            throw new RuntimeException("Dispatch tracking ID is missing");
        }

        goodsTransactionService.dispatchFullOrder(order, userId);
        order.setDispatchedAt(LocalDateTime.now());
    }

    private HistoryAction getHistoryAction(SalesOrderStatus status) {
        return switch (status) {
            case PRODUCTION_REQUIRED -> HistoryAction.PRODUCTION_REQUIRED;
            case IN_PRODUCTION -> HistoryAction.PRODUCTION_STARTED;
            case PRODUCTION_COMPLETED -> HistoryAction.PRODUCTION_COMPLETED;
            case PENDING_DISPATCH -> HistoryAction.SALES_ORDER_READY_FOR_DISPATCH;
            case DISPATCH_APPROVED -> HistoryAction.DISPATCH_APPROVED;
            case DISPATCHED -> HistoryAction.ORDER_DISPATCHED;
            case COMPLETED -> HistoryAction.ORDER_COMPLETED;
            case REJECTED -> HistoryAction.SALES_ORDER_REJECTED;
            default -> HistoryAction.SALES_ORDER_UPDATED;
        };
    }

    private void validateStatusChange(SalesOrderStatus currentStatus, SalesOrderStatus newStatus) {
        if (currentStatus == newStatus) {
            throw new RuntimeException("Order is already in status: " + newStatus);
        }
        if (currentStatus == SalesOrderStatus.PENDING_APPROVAL && newStatus == SalesOrderStatus.REJECTED) return;
        if (currentStatus == SalesOrderStatus.PRODUCTION_REQUIRED && newStatus == SalesOrderStatus.IN_PRODUCTION) return;
        if (currentStatus == SalesOrderStatus.IN_PRODUCTION && newStatus == SalesOrderStatus.PRODUCTION_COMPLETED) return;
        if (currentStatus == SalesOrderStatus.PRODUCTION_COMPLETED && newStatus == SalesOrderStatus.PENDING_DISPATCH) return;
        if (currentStatus == SalesOrderStatus.PENDING_DISPATCH && newStatus == SalesOrderStatus.DISPATCH_APPROVED) return;
        if (currentStatus == SalesOrderStatus.DISPATCH_APPROVED && newStatus == SalesOrderStatus.DISPATCHED) return;
        if (currentStatus == SalesOrderStatus.DISPATCHED && newStatus == SalesOrderStatus.COMPLETED) return;

        if (currentStatus == SalesOrderStatus.REJECTED) throw new RuntimeException("Rejected order cannot change status");
        if (currentStatus == SalesOrderStatus.COMPLETED) throw new RuntimeException("Completed order cannot change status");
        if (currentStatus == SalesOrderStatus.CANCELLED) throw new RuntimeException("Cancelled order cannot change status");
        throw new RuntimeException("Invalid status change: " + currentStatus + " → " + newStatus);
    }

    private String generateOrderNumber() {
        long count = salesOrderRepository.count();
        return String.format("SO-%04d", count + 1);
    }

    private String generateTrackingId() {
        String trackingId;
        do {
            trackingId = String.valueOf(10000000 + random.nextInt(90000000));
        } while (salesOrderRepository.existsByDispatchTrackingId(trackingId));
        return trackingId;
    }
}