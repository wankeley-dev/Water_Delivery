package com.example.Learn.WaterDeliveryApp.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")  // ✅ Avoids SQL keyword conflict
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Users consumer;

    @ManyToOne
    private Supplier supplier;

    private LocalDateTime orderDate;
    private String deliveryAddress;
    private String waterType;
    private Long quantity;
    private Double  pricePerLitre;
    private Double  totalPrice;

    @Enumerated(EnumType.STRING) // Ensures proper String-to-Enum conversion
    @Column(nullable = false)
    private OrderStatus orderStatus;

}
