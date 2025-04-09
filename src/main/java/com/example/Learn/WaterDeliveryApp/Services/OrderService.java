package com.example.Learn.WaterDeliveryApp.Services;

import com.example.Learn.WaterDeliveryApp.Entity.Order;
import com.example.Learn.WaterDeliveryApp.Entity.OrderStatus;
import com.example.Learn.WaterDeliveryApp.Entity.Supplier;
import com.example.Learn.WaterDeliveryApp.Repository.OrderRepository;
import com.example.Learn.WaterDeliveryApp.Repository.SupplierRepository;
import com.example.Learn.WaterDeliveryApp.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final SupplierRepository supplierRepository;
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Transactional
    public void updateOrderStatus(Long id, OrderStatus newStatus) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            logger.info("Updating order ID {} from {} to {}", id, order.getOrderStatus(), newStatus);
            order.setOrderStatus(newStatus);
            orderRepository.save(order);
            logger.info("Order status updated successfully.");
        } else {
            logger.warn("Order ID {} not found!", id);
        }
    }

    @Transactional(readOnly = true)
    public List<Order> getCustomerOrders(Long consumerId) {
        return userRepository.findById(consumerId)
                .map(orderRepository::findByConsumer)
                .orElseThrow(() -> new RuntimeException("Consumer not found"));
    }

    @Transactional(readOnly = true)
    public List<Order> getSupplierOrders(Long supplierId) {
        return supplierRepository.findById(supplierId)
                .map(orderRepository::findBySupplier)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
    }

    @Transactional(readOnly = true)
    public List<Order> getPendingDeliveries(Long supplierId) {
        return supplierRepository.findById(supplierId)
                .map(supplier -> orderRepository.findBySupplierAndOrderStatusNot(supplier, OrderStatus.DELIVERED))
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
    }

    @Transactional
    public Order placeOrder(Order order) {
        order.setOrderStatus(OrderStatus.PENDING);
        order.setConfirmed(false); // ✅ Default to unconfirmed

        // Apply promotion discount if available
        Supplier supplier = order.getSupplier();
        if (supplier.getPromotionCode() != null && supplier.getPromotionExpiry() != null &&
                supplier.getPromotionExpiry().isAfter(LocalDateTime.now())) {
            double discount = supplier.getPromotionDiscount() / 100.0;
            order.setTotalPrice(order.getQuantity() * order.getPricePerLitre() * (1 - discount));
            logger.info("Applied promotion {} with {}% discount to order.", supplier.getPromotionCode(), supplier.getPromotionDiscount());
        } else {
            order.setTotalPrice(order.getQuantity() * order.getPricePerLitre());
        }

        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByTotalPrice(Long totalPrice) {
        return orderRepository.findByTotalPriceGreaterThanEqual(totalPrice);
    }

    @Transactional(readOnly = true)
    public int countSupplierOrders(Long supplierId) {
        return orderRepository.countBySupplierId(supplierId);
    }

    @Transactional(readOnly = true)
    public int countOrdersByStatus(Long supplierId, OrderStatus status) {
        return orderRepository.countBySupplierIdAndOrderStatus(supplierId, status);
    }

    // ✅ New Method: Confirm Order
    @Transactional
    public void confirmOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setConfirmed(true);
        order.setOrderStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);
        logger.info("Order ID {} confirmed by supplier.", orderId);
    }
}