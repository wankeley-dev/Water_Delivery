package com.example.Learn.WaterDeliveryApp.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private Users users;  // ✅ Link Supplier with a User

    private String name;
    private String location;
    private String contactDetails;
    private double pricing;
    private String imagePath; // ✅ Field for storing business image path

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description; // ✅ New field for supplier description

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL)
    private List<Review> reviews;


    @Transient
    private double averageRating;

    @Transient
    private Review latestReview;

    // Getters & Setters
    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

    public Review getLatestReview() { return latestReview; }
    public void setLatestReview(Review latestReview) { this.latestReview = latestReview; }

}
