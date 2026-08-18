package com.erp.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.erp.dto.RawMaterialPurchaseItemRequest;
import com.erp.dto.RawMaterialPurchaseRequest;
import com.erp.entity.HistoryAction;
import com.erp.entity.RawMaterial;
import com.erp.entity.RawMaterialPurchase;
import com.erp.entity.RawMaterialPurchaseItem;
import com.erp.entity.RawMaterialPurchaseStatus;
import com.erp.entity.Supplier;
import com.erp.entity.User;
import com.erp.repository.RawMaterialPurchaseItemRepository;
import com.erp.repository.RawMaterialPurchaseRepository;
import com.erp.repository.RawMaterialRepository;
import com.erp.repository.SupplierRepository;
import com.erp.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RawMaterialPurchaseService {
    private final RawMaterialPurchaseRepository purchaseRepository;
    private final RawMaterialPurchaseItemRepository itemRepository;
    private final RawMaterialRepository rawMaterialRepository;
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;
    private final RawMaterialTransactionService rawMaterialTransactionService;
    private final HistoryService historyService;

    @Transactional
    public RawMaterialPurchase create(RawMaterialPurchaseRequest request) {
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + request.getSupplierId()));

        User user = userRepository.findById(request.getRequestedBy())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getRequestedBy()));

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Purchase must contain at least one item");
        }

        RawMaterialPurchase purchase = RawMaterialPurchase.builder()
                .purchaseNumber(generatePurchaseNumber())
                .supplier(supplier)
                .requestedBy(user)
                .status(RawMaterialPurchaseStatus.PENDING_APPROVAL)
                .remarks(request.getRemarks())
                .build();

        RawMaterialPurchase savedPurchase = purchaseRepository.save(purchase);
        for (RawMaterialPurchaseItemRequest itemRequest : request.getItems()) {
            createItem(savedPurchase, itemRequest);
        }

        historyService.createHistory(HistoryAction.RAW_MATERIAL_PURCHASE_CREATED, "RAW_MATERIAL_PURCHASE", savedPurchase.getId(), request.getRequestedBy(), "Raw material purchase " + savedPurchase.getPurchaseNumber() + " created");
        return savedPurchase;
    }

    private void createItem(RawMaterialPurchase purchase, RawMaterialPurchaseItemRequest request) {
        RawMaterial rawMaterial = rawMaterialRepository.findById(request.getRawMaterialId())
                .orElseThrow(() -> new RuntimeException("Raw material not found with id: " + request.getRawMaterialId()));

        if (request.getQuantity() == null || request.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Quantity must be greater than zero");
        }

        BigDecimal totalPrice = BigDecimal.ZERO;
        if (request.getUnitPrice() != null) {
            if (request.getUnitPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("Unit price cannot be negative");
            }
            totalPrice = request.getUnitPrice().multiply(request.getQuantity());
        }

        RawMaterialPurchaseItem item = RawMaterialPurchaseItem.builder()
                .purchase(purchase)
                .rawMaterial(rawMaterial)
                .quantity(request.getQuantity())
                .unitPrice(request.getUnitPrice())
                .totalPrice(totalPrice)
                .build();
        itemRepository.save(item);
    }

    public List<RawMaterialPurchase> getAll() {
        return purchaseRepository.findAll();
    }

    public RawMaterialPurchase getById(Long id) {
        return purchaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Raw material purchase not found with id: " + id));
    }

    public List<RawMaterialPurchaseItem> getItems(Long purchaseId) {
        getById(purchaseId);
        return itemRepository.findByPurchaseId(purchaseId);
    }

    @Transactional
    public RawMaterialPurchase changeStatus(Long id, String status, Long userId) {
        RawMaterialPurchase purchase = getById(id);
        String upperStatus = status.trim().toUpperCase();
        RawMaterialPurchaseStatus newStatus;
        try {
            newStatus = RawMaterialPurchaseStatus.valueOf(upperStatus);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid purchase status: " + status);
        }

        RawMaterialPurchaseStatus currentStatus = purchase.getStatus();
        validateStatusChange(currentStatus, newStatus);

        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        }

        if (newStatus == RawMaterialPurchaseStatus.APPROVED) {
            if (userId == null) {
                throw new RuntimeException("User ID is required for approval");
            }
            purchase.setApprovedBy(user);
            purchase.setApprovedAt(LocalDateTime.now());
        }

        if (newStatus == RawMaterialPurchaseStatus.RECEIVED) {
            rawMaterialTransactionService.receivePurchase(purchase, userId);
            purchase.setReceivedAt(LocalDateTime.now());
        }

        purchase.setStatus(newStatus);
        RawMaterialPurchase savedPurchase = purchaseRepository.save(purchase);

        HistoryAction action = getHistoryAction(newStatus);
        historyService.createHistory(action, "RAW_MATERIAL_PURCHASE", savedPurchase.getId(), userId, "Raw material purchase " + savedPurchase.getPurchaseNumber() + " status changed from " + currentStatus + " to " + newStatus);
        return savedPurchase;
    }

    private void validateStatusChange(RawMaterialPurchaseStatus currentStatus, RawMaterialPurchaseStatus newStatus) {
        if (currentStatus == newStatus) {
            throw new RuntimeException("Purchase is already in status: " + newStatus);
        }
        if (currentStatus == RawMaterialPurchaseStatus.PENDING_APPROVAL && (newStatus == RawMaterialPurchaseStatus.APPROVED || newStatus == RawMaterialPurchaseStatus.REJECTED)) {
            return;
        }
        if (currentStatus == RawMaterialPurchaseStatus.APPROVED && newStatus == RawMaterialPurchaseStatus.RECEIVED) {
            return;
        }
        if (currentStatus == RawMaterialPurchaseStatus.CANCELLED || currentStatus == RawMaterialPurchaseStatus.REJECTED || currentStatus == RawMaterialPurchaseStatus.RECEIVED) {
            throw new RuntimeException((currentStatus == RawMaterialPurchaseStatus.CANCELLED ? "Cancelled" : currentStatus == RawMaterialPurchaseStatus.REJECTED ? "Rejected" : "Received") + " purchase cannot be changed");
        }
        throw new RuntimeException("Invalid purchase status change: " + currentStatus + " → " + newStatus);
    }

    private HistoryAction getHistoryAction(RawMaterialPurchaseStatus status) {
        return switch (status) {
            case APPROVED -> HistoryAction.RAW_MATERIAL_PURCHASE_APPROVED;
            case REJECTED -> HistoryAction.RAW_MATERIAL_PURCHASE_REJECTED;
            case RECEIVED -> HistoryAction.RAW_MATERIAL_RECEIVED;
            default -> HistoryAction.RAW_MATERIAL_PURCHASE_CREATED;
        };
    }

    private String generatePurchaseNumber() {
        long count = purchaseRepository.count();
        return String.format("RMP-%04d", count + 1);
    }
}