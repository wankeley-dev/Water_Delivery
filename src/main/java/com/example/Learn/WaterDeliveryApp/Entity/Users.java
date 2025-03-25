package com.example.Learn.WaterDeliveryApp.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users") // Explicitly naming table to avoid conflicts
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

    @Enumerated(EnumType.STRING) // Ensures proper String-to-Enum conversion
    @Column(nullable = false)
    private Role role;
}
