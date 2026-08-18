package com.erp.service;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.erp.dto.GoodsTransactionRequest;
import com.erp.entity.GoodsStock;
import com.erp.entity.GoodsTransaction;
import com.erp.entity.GoodsTransactionType;
import com.erp.entity.Production;
import com.erp.entity.ProductionItem;
import com.erp.entity.Product;
import com.erp.entity.SalesOrder;
import com.erp.entity.SalesOrderItem;
import com.erp.entity.User;
import com.erp.repository.GoodsStockRepository;
import com.erp.repository.GoodsTransactionRepository;
import com.erp.repository.ProductionItemRepository;
import com.erp.repository.ProductionRepository;
import com.erp.repository.ProductRepository;
import com.erp.repository.SalesOrderItemRepository;
import com.erp.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoodsTransactionService {
    private final GoodsTransactionRepository transactionRepository;
    private final GoodsStockRepository goodsStockRepository;
    private final ProductRepository productRepository;
    private final ProductionRepository productionRepository;
    private final ProductionItemRepository productionItemRepository;
    private final SalesOrderItemRepository salesOrderItemRepository;
    private final UserRepository userRepository;

    @Transactional
    public GoodsTransaction create(GoodsTransactionRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + request.getProductId()));

        GoodsStock goodsStock = goodsStockRepository.findByProductId(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Goods stock not found for product: " + request.getProductId()));

        BigDecimal currentStock = goodsStock.getAvailableStock() != null ? goodsStock.getAvailableStock() : BigDecimal.ZERO;
        BigDecimal quantity = request.getQuantity();

        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Quantity must be greater than zero");
        }

        BigDecimal newStock;
        if (request.getTransactionType() == GoodsTransactionType.PRODUCTION_RECEIVED
                || request.getTransactionType() == GoodsTransactionType.RETURNED) {
            newStock = currentStock.add(quantity);
        } else if (request.getTransactionType() == GoodsTransactionType.DISPATCHED) {
            if (quantity.compareTo(currentStock) > 0) {
                throw new RuntimeException("Insufficient goods stock. Available: " + currentStock + ", Required: " + quantity);
            }
            newStock = currentStock.subtract(quantity);
        } else if (request.getTransactionType() == GoodsTransactionType.ADJUSTMENT) {
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

        GoodsTransaction transaction = GoodsTransaction.builder()
                .product(product)
                .transactionType(request.getTransactionType())
                .quantity(quantity)
                .stockBefore(currentStock)
                .stockAfter(newStock)
                .production(production)
                .createdBy(user)
                .remarks(request.getRemarks())
                .build();

        goodsStock.setAvailableStock(newStock);
        goodsStockRepository.save(goodsStock);
        return transactionRepository.save(transaction);
    }

    @Transactional
    public void receiveProductionGoods(Production production, Long userId) {
        List<ProductionItem> items = productionItemRepository.findByProductionId(production.getId());
        if (items.isEmpty()) {
            throw new RuntimeException("No production items found for production: " + production.getProductionNumber());
        }

        for (ProductionItem item : items) {
            Integer requiredQuantity = item.getRequiredQuantity();
            Integer completedQuantity = item.getCompletedQuantity();

            if (requiredQuantity == null || completedQuantity == null) {
                throw new RuntimeException("Production quantity is missing for product: " + item.getProduct().getProductName());
            }
            if (completedQuantity <= 0) {
                throw new RuntimeException("Completed quantity must be greater than zero for product: " + item.getProduct().getProductName());
            }
            if (!completedQuantity.equals(requiredQuantity)) {
                throw new RuntimeException("Production cannot be completed. Required: " + requiredQuantity + ", Completed: " + completedQuantity + " for product: " + item.getProduct().getProductName());
            }

            Product product = item.getProduct();
            GoodsStock goodsStock = goodsStockRepository.findByProductId(product.getId())
                    .orElseThrow(() -> new RuntimeException("Goods stock not found for product: " + product.getProductName()));

            BigDecimal currentStock = goodsStock.getAvailableStock() != null ? goodsStock.getAvailableStock() : BigDecimal.ZERO;
            BigDecimal quantity = BigDecimal.valueOf(completedQuantity);
            BigDecimal newStock = currentStock.add(quantity);

            goodsStock.setAvailableStock(newStock);
            goodsStockRepository.save(goodsStock);

            User user = userId != null ? userRepository.findById(userId).orElse(null) : null;

            GoodsTransaction transaction = GoodsTransaction.builder()
                    .product(product)
                    .transactionType(GoodsTransactionType.PRODUCTION_RECEIVED)
                    .quantity(quantity)
                    .stockBefore(currentStock)
                    .stockAfter(newStock)
                    .production(production)
                    .createdBy(user)
                    .remarks("Finished goods received from production " + production.getProductionNumber())
                    .build();
            transactionRepository.save(transaction);
        }
    }

    public void validateFullOrderStock(Long salesOrderId) {
        List<SalesOrderItem> items = salesOrderItemRepository.findBySalesOrderId(salesOrderId);
        if (items.isEmpty()) {
            throw new RuntimeException("Sales order has no items");
        }

        for (SalesOrderItem item : items) {
            Product product = item.getProduct();
            GoodsStock goodsStock = goodsStockRepository.findByProductId(product.getId())
                    .orElseThrow(() -> new RuntimeException("Goods stock not found for product: " + product.getProductName()));

            BigDecimal availableStock = goodsStock.getAvailableStock() != null ? goodsStock.getAvailableStock() : BigDecimal.ZERO;
            BigDecimal requiredQuantity = BigDecimal.valueOf(item.getQuantity());

            if (availableStock.compareTo(requiredQuantity) < 0) {
                throw new RuntimeException("Insufficient stock for product: " + product.getProductName() + ". Required: " + requiredQuantity + ", Available: " + availableStock);
            }
        }
    }

    @Transactional
    public void dispatchFullOrder(SalesOrder salesOrder, Long userId) {
        List<SalesOrderItem> items = salesOrderItemRepository.findBySalesOrderId(salesOrder.getId());
        if (items.isEmpty()) {
            throw new RuntimeException("Sales order has no items");
        }

        validateFullOrderStock(salesOrder.getId());

        User user = userId != null ? userRepository.findById(userId).orElse(null) : null;

        for (SalesOrderItem item : items) {
            Product product = item.getProduct();
            GoodsStock goodsStock = goodsStockRepository.findByProductId(product.getId())
                    .orElseThrow(() -> new RuntimeException("Goods stock not found for product: " + product.getProductName()));

            BigDecimal currentStock = goodsStock.getAvailableStock() != null ? goodsStock.getAvailableStock() : BigDecimal.ZERO;
            BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
            BigDecimal newStock = currentStock.subtract(quantity);

            if (newStock.compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("Insufficient stock while dispatching product: " + product.getProductName());
            }

            goodsStock.setAvailableStock(newStock);
            goodsStockRepository.save(goodsStock);

            GoodsTransaction transaction = GoodsTransaction.builder()
                    .product(product)
                    .transactionType(GoodsTransactionType.DISPATCHED)
                    .quantity(quantity)
                    .stockBefore(currentStock)
                    .stockAfter(newStock)
                    .createdBy(user)
                    .remarks("Goods dispatched for sales order " + salesOrder.getOrderNumber())
                    .build();
            transactionRepository.save(transaction);
        }
    }

    public List<GoodsTransaction> getAll() {
        return transactionRepository.findAll();
    }

    public GoodsTransaction getById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goods transaction not found with id: " + id));
    }

    public List<GoodsTransaction> getByProductId(Long productId) {
        return transactionRepository.findByProductId(productId);
    }

    public List<GoodsTransaction> getByProductionId(Long productionId) {
        return transactionRepository.findByProductionId(productionId);
    }
}