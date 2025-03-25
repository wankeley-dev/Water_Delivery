package com.example.Learn.WaterDeliveryApp.Repository;

import com.example.Learn.WaterDeliveryApp.Entity.Order;
import com.example.Learn.WaterDeliveryApp.Entity.Supplier;
import com.example.Learn.WaterDeliveryApp.Entity.Users;
import com.example.Learn.WaterDeliveryApp.Entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Get all orders by a supplier
    List<Order> findBySupplier(Supplier supplier);

    // Get all orders by a user
    List<Order> findByConsumer(Users users);

    // Count total orders for a supplier
    int countBySupplierId(Long supplierId);

    // Count orders by supplier and status
    int countBySupplierIdAndOrderStatus(Long supplierId, OrderStatus orderStatus);

    // Get orders by supplier excluding a specific status
    List<Order> findBySupplierAndOrderStatusNot(Supplier supplier, OrderStatus orderStatus);

    // Fetch orders with total price >= given amount
    List<Order> findByTotalPriceGreaterThanEqual(Long totalPrice);


}