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


    // Fetch orders for a specific consumer
    public List<Order> getCustomerOrders(Long consumerId) {
        return userRepository.findById(consumerId)
                .map(orderRepository::findByConsumer)
                .orElseThrow(() -> new RuntimeException("Consumer not found"));
    }

    // Fetch orders for a specific supplier
    public List<Order> getSupplierOrders(Long supplierId) {
        return supplierRepository.findById(supplierId)
                .map(orderRepository::findBySupplier)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
    }

    // Fetch pending deliveries for a supplier
    public List<Order> getPendingDeliveries(Long supplierId) {
        return supplierRepository.findById(supplierId)
                .map(supplier -> orderRepository.findBySupplierAndOrderStatusNot(supplier, OrderStatus.DELIVERED))
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
    }

    // Place a new order with default status PENDING
    public Order placeOrder(Order order) {

        order.setOrderStatus(OrderStatus.PENDING);

        // Get supplier contact details
        Supplier supplier = order.getSupplier();
        String supplierPhoneNumber = supplier.getContactDetails();


        return orderRepository.save(order);
    }

    // Fetch orders with total price >= given amount
    public List<Order> getOrdersByTotalPrice(Long totalPrice) {
        return orderRepository.findByTotalPriceGreaterThanEqual(totalPrice);
    }

    // Count total orders for a supplier
    public int countSupplierOrders(Long supplierId) {
        return orderRepository.countBySupplierId(supplierId);
    }

    // Count orders by supplier and status
    public int countOrdersByStatus(Long supplierId, OrderStatus status) {
        return orderRepository.countBySupplierIdAndOrderStatus(supplierId, status);
    }

}