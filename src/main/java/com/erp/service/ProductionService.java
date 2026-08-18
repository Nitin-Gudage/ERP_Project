package com.erp.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.erp.dto.ProductionDetailsResponse;
import com.erp.dto.ProductionRequest;
import com.erp.entity.HistoryAction;
import com.erp.entity.Production;
import com.erp.entity.ProductionStatus;
import com.erp.entity.SalesOrder;
import com.erp.entity.SalesOrderStatus;
import com.erp.entity.User;
import com.erp.repository.ProductionRepository;
import com.erp.repository.SalesOrderRepository;
import com.erp.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductionService {
    private final ProductionRepository productionRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final UserRepository userRepository;
    private final HistoryService historyService;
    private final GoodsTransactionService goodsTransactionService;

    @Transactional
    public Production create(ProductionRequest request) {
        SalesOrder salesOrder = salesOrderRepository.findById(request.getSalesOrderId())
                .orElseThrow(() -> new RuntimeException("Sales order not found with id: " + request.getSalesOrderId()));

        User user = userRepository.findById(request.getRequestedBy())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getRequestedBy()));

        if (salesOrder.getStatus() != SalesOrderStatus.PRODUCTION_REQUIRED) {
            throw new RuntimeException("Production can only be created for orders with status PRODUCTION_REQUIRED");
        }

        Production production = Production.builder()
                .productionNumber(generateProductionNumber())
                .salesOrder(salesOrder)
                .requestedBy(user)
                .status(ProductionStatus.PENDING)
                .remarks(request.getRemarks())
                .build();

        Production savedProduction = productionRepository.save(production);
        historyService.createHistory(HistoryAction.PRODUCTION_CREATED, "PRODUCTION", savedProduction.getId(), request.getRequestedBy(), "Production " + savedProduction.getProductionNumber() + " created");
        return savedProduction;
    }

    public List<Production> getAll() {
        return productionRepository.findAll();
    }

    public Production getById(Long id) {
        return productionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Production not found with id: " + id));
    }

    public ProductionDetailsResponse getProductionDetails(Long id) {
        Production production = getById(id);
        return ProductionDetailsResponse.builder()
                .id(production.getId())
                .productionNumber(production.getProductionNumber())
                .salesOrderId(production.getSalesOrder() != null ? production.getSalesOrder().getId() : null)
                .salesOrderNumber(production.getSalesOrder() != null ? production.getSalesOrder().getOrderNumber() : null)
                .requestedBy(production.getRequestedBy().getId())
                .requestedByUsername(production.getRequestedBy().getUsername())
                .status(production.getStatus())
                .remarks(production.getRemarks())
                .approvedBy(production.getApprovedBy() != null ? production.getApprovedBy().getId() : null)
                .approvedByUsername(production.getApprovedBy() != null ? production.getApprovedBy().getUsername() : null)
                .approvedAt(production.getApprovedAt())
                .createdAt(production.getCreatedAt())
                .updatedAt(production.getUpdatedAt())
                .build();
    }

    @Transactional
    public Production update(Long id, ProductionRequest request) {
        Production production = getById(id);

        if (production.getStatus() != ProductionStatus.PENDING) {
            throw new RuntimeException("Only pending production can be updated");
        }

        SalesOrder salesOrder = salesOrderRepository.findById(request.getSalesOrderId())
                .orElseThrow(() -> new RuntimeException("Sales order not found"));

        production.setSalesOrder(salesOrder);
        production.setRemarks(request.getRemarks());

        Production updatedProduction = productionRepository.save(production);
        historyService.createHistory(HistoryAction.PRODUCTION_CREATED, "PRODUCTION", updatedProduction.getId(), request.getRequestedBy(), "Production " + updatedProduction.getProductionNumber() + " updated");
        return updatedProduction;
    }

    public String delete(Long id) {
        Production production = getById(id);
        if (production.getStatus() != ProductionStatus.PENDING) {
            throw new RuntimeException("Only pending production can be deleted");
        }
        productionRepository.delete(production);
        return "Production deleted successfully with id : " + id;
    }

    @Transactional
    public Production changeStatus(Long id, String status, Long userId) {
        Production production = getById(id);
        String upperStatus = status.trim().toUpperCase();
        ProductionStatus newStatus;
        try {
            newStatus = ProductionStatus.valueOf(upperStatus);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid production status: " + status);
        }

        ProductionStatus currentStatus = production.getStatus();
        validateStatusChange(currentStatus, newStatus);

        if (newStatus == ProductionStatus.APPROVED) {
            if (userId == null) {
                throw new RuntimeException("User ID is required for approval");
            }
            User manager = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            production.setApprovedBy(manager);
            production.setApprovedAt(LocalDateTime.now());
        }

        production.setStatus(newStatus);
        Production savedProduction = productionRepository.save(production);

        if (newStatus == ProductionStatus.COMPLETED) {
            completeProduction(savedProduction, userId);
        } else {
            HistoryAction action = getHistoryAction(newStatus);
            historyService.createHistory(action, "PRODUCTION", savedProduction.getId(), userId, "Production " + savedProduction.getProductionNumber() + " status changed from " + currentStatus + " to " + newStatus);
        }

        return savedProduction;
    }

    @Transactional
    private void completeProduction(Production production, Long userId) {
        goodsTransactionService.receiveProductionGoods(production, userId);
        historyService.createHistory(HistoryAction.PRODUCTION_COMPLETED, "PRODUCTION", production.getId(), userId, "Production " + production.getProductionNumber() + " completed and finished goods received into warehouse");

        SalesOrder salesOrder = production.getSalesOrder();
        if (salesOrder == null) {
            return;
        }

        salesOrder.setStatus(SalesOrderStatus.PENDING_DISPATCH);
        SalesOrder savedOrder = salesOrderRepository.save(salesOrder);
        historyService.createHistory(HistoryAction.SALES_ORDER_READY_FOR_DISPATCH, "SALES_ORDER", savedOrder.getId(), userId, "Sales order " + savedOrder.getOrderNumber() + " is ready for dispatch after production completion");
    }

    private void validateStatusChange(ProductionStatus currentStatus, ProductionStatus newStatus) {
        if (currentStatus == newStatus) {
            throw new RuntimeException("Production is already in status: " + newStatus);
        }
        if (currentStatus == ProductionStatus.PENDING && (newStatus == ProductionStatus.APPROVED || newStatus == ProductionStatus.REJECTED)) {
            return;
        }
        if (currentStatus == ProductionStatus.APPROVED && newStatus == ProductionStatus.IN_PRODUCTION) {
            return;
        }
        if (currentStatus == ProductionStatus.IN_PRODUCTION && newStatus == ProductionStatus.COMPLETED) {
            return;
        }
        if (currentStatus == ProductionStatus.REJECTED || currentStatus == ProductionStatus.COMPLETED) {
            throw new RuntimeException((currentStatus == ProductionStatus.REJECTED ? "Rejected" : "Completed") + " production cannot change status");
        }
        throw new RuntimeException("Invalid production status change: " + currentStatus + " → " + newStatus);
    }

    private HistoryAction getHistoryAction(ProductionStatus status) {
        return switch (status) {
            case APPROVED -> HistoryAction.PRODUCTION_CREATED;
            case IN_PRODUCTION -> HistoryAction.PRODUCTION_STARTED;
            case REJECTED -> HistoryAction.SALES_ORDER_REJECTED;
            default -> HistoryAction.PRODUCTION_CREATED;
        };
    }

    private String generateProductionNumber() {
        long count = productionRepository.count();
        return String.format("PR-%04d", count + 1);
    }
}