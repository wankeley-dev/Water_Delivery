package com.example.Learn.WaterDeliveryApp.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "users_id", nullable = false)
    private Users users;

    @ManyToOne
    @JoinColumn(name = "order_id") // ✅ Link to an Order for verification
    private Order order;

    @Column(nullable = false)
    private String content;

    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private int rating;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean isVerified = false; // ✅ Indicates if tied to a completed order
}