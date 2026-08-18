package com.erp.service;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.erp.dto.RawMaterialTransactionRequest;
import com.erp.entity.RawMaterial;
import com.erp.entity.RawMaterialPurchase;
import com.erp.entity.RawMaterialPurchaseItem;
import com.erp.entity.RawMaterialTransaction;
import com.erp.entity.RawMaterialTransactionType;
import com.erp.entity.Production;
import com.erp.entity.User;
import com.erp.repository.RawMaterialPurchaseItemRepository;
import com.erp.repository.RawMaterialRepository;
import com.erp.repository.RawMaterialTransactionRepository;
import com.erp.repository.ProductionRepository;
import com.erp.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RawMaterialTransactionService {
    private final RawMaterialTransactionRepository transactionRepository;
    private final RawMaterialRepository rawMaterialRepository;
    private final ProductionRepository productionRepository;
    private final UserRepository userRepository;
    private final RawMaterialPurchaseItemRepository purchaseItemRepository;

    @Transactional
    public RawMaterialTransaction create(RawMaterialTransactionRequest request) {
        if (request.getRawMaterialId() == null) {
            throw new RuntimeException("Raw material ID is required");
        }
        if (request.getTransactionType() == null) {
            throw new RuntimeException("Transaction type is required");
        }
        if (request.getQuantity() == null || request.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Quantity must be greater than zero");
        }

        RawMaterial rawMaterial = rawMaterialRepository.findById(request.getRawMaterialId())
                .orElseThrow(() -> new RuntimeException("Raw material not found with id: " + request.getRawMaterialId()));

        BigDecimal currentStock = rawMaterial.getAvailableStock() != null ? rawMaterial.getAvailableStock() : BigDecimal.ZERO;
        BigDecimal quantity = request.getQuantity();
        BigDecimal newStock;

        if (request.getTransactionType() == RawMaterialTransactionType.PURCHASE_RECEIVED
                || request.getTransactionType() == RawMaterialTransactionType.RETURNED) {
            newStock = currentStock.add(quantity);
        } else if (request.getTransactionType() == RawMaterialTransactionType.PRODUCTION_ISSUED) {
            if (quantity.compareTo(currentStock) > 0) {
                throw new RuntimeException("Insufficient raw material stock. Available: " + currentStock + ", Required: " + quantity);
            }
            newStock = currentStock.subtract(quantity);
        } else if (request.getTransactionType() == RawMaterialTransactionType.ADJUSTMENT) {
            newStock = quantity;
        } else {
            throw new RuntimeException("Invalid transaction type");
        }

        Production production = null;
        if (request.getProductionId() != null) {
            production = productionRepository.findById(request.getProductionId())
                    .orElseThrow(() -> new RuntimeException("Production not found with id: " + request.getProductionId()));
        }

        User user = null;
        if (request.getCreatedBy() != null) {
            user = userRepository.findById(request.getCreatedBy())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getCreatedBy()));
        }

        RawMaterialTransaction transaction = RawMaterialTransaction.builder()
                .rawMaterial(rawMaterial)
                .transactionType(request.getTransactionType())
                .quantity(quantity)
                .stockBefore(currentStock)
                .stockAfter(newStock)
                .production(production)
                .createdBy(user)
                .remarks(request.getRemarks())
                .build();

        rawMaterial.setAvailableStock(newStock);
        rawMaterialRepository.save(rawMaterial);
        return transactionRepository.save(transaction);
    }

    @Transactional
    public void receivePurchase(RawMaterialPurchase purchase, Long userId) {
        List<RawMaterialPurchaseItem> items = purchaseItemRepository.findByPurchaseId(purchase.getId());
        if (items.isEmpty()) {
            throw new RuntimeException("Purchase has no items");
        }

        User user = userId != null ? userRepository.findById(userId).orElse(null) : null;

        for (RawMaterialPurchaseItem item : items) {
            RawMaterial rawMaterial = item.getRawMaterial();
            BigDecimal quantity = item.getQuantity();

            if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Invalid quantity for raw material: " + rawMaterial.getMaterialName());
            }

            BigDecimal currentStock = rawMaterial.getAvailableStock() != null ? rawMaterial.getAvailableStock() : BigDecimal.ZERO;
            BigDecimal newStock = currentStock.add(quantity);

            rawMaterial.setAvailableStock(newStock);
            rawMaterialRepository.save(rawMaterial);

            RawMaterialTransaction transaction = RawMaterialTransaction.builder()
                    .rawMaterial(rawMaterial)
                    .transactionType(RawMaterialTransactionType.PURCHASE_RECEIVED)
                    .quantity(quantity)
                    .stockBefore(currentStock)
                    .stockAfter(newStock)
                    .purchase(purchase)
                    .createdBy(user)
                    .remarks("Raw material received from purchase " + purchase.getPurchaseNumber())
                    .build();
            transactionRepository.save(transaction);
        }
    }

    @Transactional
    public RawMaterialTransaction issueForProduction(Long rawMaterialId, Long productionId, BigDecimal quantity, Long userId, String remarks) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Quantity must be greater than zero");
        }

        RawMaterial rawMaterial = rawMaterialRepository.findById(rawMaterialId)
                .orElseThrow(() -> new RuntimeException("Raw material not found with id: " + rawMaterialId));

        Production production = productionRepository.findById(productionId)
                .orElseThrow(() -> new RuntimeException("Production not found with id: " + productionId));

        BigDecimal currentStock = rawMaterial.getAvailableStock() != null ? rawMaterial.getAvailableStock() : BigDecimal.ZERO;
        if (quantity.compareTo(currentStock) > 0) {
            throw new RuntimeException("Insufficient raw material stock for " + rawMaterial.getMaterialName() + ". Available: " + currentStock + ", Required: " + quantity);
        }

        BigDecimal newStock = currentStock.subtract(quantity);
        User user = userId != null ? userRepository.findById(userId).orElse(null) : null;

        rawMaterial.setAvailableStock(newStock);
        rawMaterialRepository.save(rawMaterial);

        RawMaterialTransaction transaction = RawMaterialTransaction.builder()
                .rawMaterial(rawMaterial)
                .transactionType(RawMaterialTransactionType.PRODUCTION_ISSUED)
                .quantity(quantity)
                .stockBefore(currentStock)
                .stockAfter(newStock)
                .production(production)
                .createdBy(user)
                .remarks(remarks)
                .build();
        return transactionRepository.save(transaction);
    }

    public List<RawMaterialTransaction> getAll() {
        return transactionRepository.findAll();
    }

    public RawMaterialTransaction getById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Raw material transaction not found with id: " + id));
    }

    public List<RawMaterialTransaction> getByRawMaterialId(Long rawMaterialId) {
        return transactionRepository.findByRawMaterialId(rawMaterialId);
    }

    public List<RawMaterialTransaction> getByProductionId(Long productionId) {
        return transactionRepository.findByProductionId(productionId);
    }

    public List<RawMaterialTransaction> getByPurchaseId(Long purchaseId) {
        return transactionRepository.findByPurchaseId(purchaseId);
    }
}