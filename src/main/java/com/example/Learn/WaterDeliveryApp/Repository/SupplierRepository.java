package com.example.Learn.WaterDeliveryApp.Repository;

import com.example.Learn.WaterDeliveryApp.Entity.Supplier;
import com.example.Learn.WaterDeliveryApp.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    // 🔍 Find suppliers by location (case-insensitive)
    @Query("SELECT s.id, s.name, s.location FROM Supplier s WHERE LOWER(s.location) LIKE LOWER(CONCAT('%', :location, '%'))")
    List<Supplier> searchByLocation(@Param("location") String location);

    // 🔍 Find suppliers by exact user ID
    Optional<Supplier> findByUsersId(Long usersId);

    // 🔍 Find suppliers by associated user entity
    Optional<Supplier> findByUsers(Users users);

    // 🔍 Find suppliers by name (case-sensitive)
    Optional<Supplier> findByName(String name);

    // 🔍 Find suppliers whose names contain a given keyword (case-insensitive)
    List<Supplier> findByNameContainingIgnoreCase(String name);
}