package com.example.Learn.WaterDeliveryApp.Repository;

import com.example.Learn.WaterDeliveryApp.Entity.Supplier;
import com.example.Learn.WaterDeliveryApp.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    // Existing Queries
    @Query("SELECT s.id, s.name, s.location FROM Supplier s WHERE LOWER(s.location) LIKE LOWER(CONCAT('%', :location, '%'))")
    List<Supplier> searchByLocation(@Param("location") String location);

    Optional<Supplier> findByUsersId(Long usersId);
    Optional<Supplier> findByUsers(Users users);
    Optional<Supplier> findByName(String name);
    List<Supplier> findByNameContainingIgnoreCase(String name);

    // ✅ New Queries
    List<Supplier> findByIsAvailableTrue(); // Find available suppliers
    List<Supplier> findByDeliveryRadiusKmGreaterThanEqual(double radius); // Find suppliers with sufficient delivery range
    List<Supplier> findByPromotionCodeNotNull(); // Find suppliers with active promotions

}