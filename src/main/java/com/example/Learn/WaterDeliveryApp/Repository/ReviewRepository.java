package com.example.Learn.WaterDeliveryApp.Repository;

import com.example.Learn.WaterDeliveryApp.Entity.Review;
import com.example.Learn.WaterDeliveryApp.Entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT r FROM Review r WHERE r.supplier.id = :supplierId ORDER BY r.createdAt DESC")
    List<Review> findTop5BySupplierIdOrderByCreatedAtDesc(@Param("supplierId") Long supplierId);
    List<Review> findBySupplier(Supplier supplier);
}
