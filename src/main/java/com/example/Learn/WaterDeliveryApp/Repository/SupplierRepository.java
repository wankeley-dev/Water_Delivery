package com.example.Learn.WaterDeliveryApp.Repository;

import com.example.Learn.WaterDeliveryApp.Entity.Supplier;
import com.example.Learn.WaterDeliveryApp.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    // Find suppliers by location (case-insensitive search)
    @Query("SELECT s FROM Supplier s WHERE LOWER(s.location) LIKE LOWER(CONCAT('%', :location, '%'))")
    List<Supplier> findByLocation(@Param("location") String location);

    // Find supplier by user ID (assuming a Supplier is linked to a User)
    Optional<Supplier> findByUserId(Long userId);

    Optional<Supplier> findByUser(Users users);  // ✅ Correct method

    List<Supplier> findByLocationContainingIgnoreCase(String location);


    Supplier findByUserEmail(String email);
}
