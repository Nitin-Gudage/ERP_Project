package com.erp.service;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.erp.dto.SalesOrderItemRequest;
import com.erp.entity.Product;
import com.erp.entity.SalesOrder;
import com.erp.entity.SalesOrderItem;
import com.erp.entity.SalesOrderStatus;
import com.erp.repository.ProductRepository;
import com.erp.repository.SalesOrderItemRepository;
import com.erp.repository.SalesOrderRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SalesOrderItemService {
    private final SalesOrderItemRepository itemRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final ProductRepository productRepository;
    private final SalesOrderService salesOrderService;

    @Transactional
    public SalesOrderItem create(SalesOrderItemRequest request) {
        SalesOrder order = salesOrderRepository.findById(request.getSalesOrderId())
                .orElseThrow(() -> new RuntimeException("Sales order not found"));

        if (order.getStatus() != SalesOrderStatus.PENDING_APPROVAL) {
            throw new RuntimeException("Items can only be added to pending orders");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        BigDecimal totalPrice = request.getUnitPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

        SalesOrderItem item = SalesOrderItem.builder()
                .salesOrder(order)
                .product(product)
                .quantity(request.getQuantity())
                .unitPrice(request.getUnitPrice())
                .totalPrice(totalPrice)
                .build();

        SalesOrderItem savedItem = itemRepository.save(item);
        salesOrderService.recalculateTotal(order.getId());
        return savedItem;
    }

    public List<SalesOrderItem> getAll() {
        return itemRepository.findAll();
    }

    public SalesOrderItem getById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sales order item not found with id: " + id));
    }

    public List<SalesOrderItem> getBySalesOrderId(Long salesOrderId) {
        return itemRepository.findBySalesOrderId(salesOrderId);
    }

    @Transactional
    public SalesOrderItem update(Long id, SalesOrderItemRequest request) {
        SalesOrderItem item = getById(id);
        SalesOrder order = item.getSalesOrder();

        if (order.getStatus() != SalesOrderStatus.PENDING_APPROVAL) {
            throw new RuntimeException("Items can only be updated for pending orders");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        BigDecimal totalPrice = request.getUnitPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

        item.setProduct(product);
        item.setQuantity(request.getQuantity());
        item.setUnitPrice(request.getUnitPrice());
        item.setTotalPrice(totalPrice);

        SalesOrderItem savedItem = itemRepository.save(item);
        salesOrderService.recalculateTotal(order.getId());
        return savedItem;
    }

    @Transactional
    public String delete(Long id) {
        SalesOrderItem item = getById(id);
        SalesOrder order = item.getSalesOrder();

        if (order.getStatus() != SalesOrderStatus.PENDING_APPROVAL) {
            throw new RuntimeException("Items can only be deleted from pending orders");
        }

        Long orderId = order.getId();
        itemRepository.delete(item);
        salesOrderService.recalculateTotal(orderId);
        return "Sales order item deleted successfully with id : " + id;
    }
}