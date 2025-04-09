package com.example.Learn.WaterDeliveryApp.Repository;

import com.example.Learn.WaterDeliveryApp.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);

    boolean existsByEmail(String email);

    // ✅ New Queries
    List<Users> findByPreferredSupplierId(Long supplierId); // Find users preferring a specific supplier

    @Query("SELECT u FROM Users u WHERE u.preferredDeliveryTime = :time")
    List<Users> findByPreferredDeliveryTime(@Param("time") String preferredDeliveryTime);

    List<Users> findByLastLoginAfter(LocalDateTime date); // Find recently active users
}