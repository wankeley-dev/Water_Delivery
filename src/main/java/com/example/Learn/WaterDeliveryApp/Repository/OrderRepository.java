package com.example.Learn.WaterDeliveryApp.Repository;

import com.example.Learn.WaterDeliveryApp.Entity.Order;
import com.example.Learn.WaterDeliveryApp.Entity.Supplier;
import com.example.Learn.WaterDeliveryApp.Entity.Users;
import com.example.Learn.WaterDeliveryApp.Entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findBySupplier(Supplier supplier);
    List<Order> findByConsumer(Users users);
    int countBySupplierId(Long supplierId);
    int countBySupplierIdAndOrderStatus(Long supplierId, OrderStatus orderStatus);
    List<Order> findBySupplierAndOrderStatusNot(Supplier supplier, OrderStatus orderStatus);
    List<Order> findByTotalPriceGreaterThanEqual(Long totalPrice);

    // ✅ New Queries
    List<Order> findByPreferredDeliveryTime(String preferredDeliveryTime);
    List<Order> findByIsConfirmed(boolean isConfirmed);
    List<Order> findByConsumerAndOrderStatus(Users consumer, OrderStatus orderStatus);
}