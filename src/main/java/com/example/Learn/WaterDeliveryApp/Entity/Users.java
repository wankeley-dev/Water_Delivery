package com.example.Learn.WaterDeliveryApp.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // ✅ New Fields
    @Column
    private Long preferredSupplierId; // ID of the user's preferred supplier

    @Column
    private String preferredDeliveryTime; // e.g., "Morning", "Afternoon", "Evening"

    @Column
    private LocalDateTime lastLogin; // Tracks the last time the user logged in
}