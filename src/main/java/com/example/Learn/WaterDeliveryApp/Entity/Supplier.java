package com.example.Learn.WaterDeliveryApp.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "users_id", nullable = false, unique = true)
    private Users users;

    private String name;
    private String location;
    private String contactDetails;
    private double pricing;


    private String imagePath; // Existing field for business image
    @Lob
    @Column(columnDefinition = "TEXT")
    private String description; // Existing field for supplier description

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL)
    private List<Review> reviews;

    @Transient
    private double averageRating;

    @Transient
    private Review latestReview;

    // ✅ New Fields
    private boolean isAvailable; // Supplier availability status
    private double deliveryRadiusKm; // Delivery radius in kilometers
    private String promotionCode; // Promotional offer code
    private double promotionDiscount; // Discount percentage (e.g., 10.0 for 10%)
    private LocalDateTime promotionExpiry; // Expiry date for promotion

    // Getters & Setters for existing transient fields
    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

    public Review getLatestReview() { return latestReview; }
    public void setLatestReview(Review latestReview) { this.latestReview = latestReview; }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(String contactDetails) {
        this.contactDetails = contactDetails;
    }

    public double getPricing() {
        return pricing;
    }

    public void setPricing(double pricing) {
        this.pricing = pricing;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public boolean isAvailable() {
        return isAvailable;
    }



    public double getDeliveryRadiusKm() {
        return deliveryRadiusKm;
    }

    public void setDeliveryRadiusKm(double deliveryRadiusKm) {
        this.deliveryRadiusKm = deliveryRadiusKm;
    }

    public String getPromotionCode() {
        return promotionCode;
    }

    public void setPromotionCode(String promotionCode) {
        this.promotionCode = promotionCode;
    }

    public double getPromotionDiscount() {
        return promotionDiscount;
    }

    public void setPromotionDiscount(double promotionDiscount) {
        this.promotionDiscount = promotionDiscount;
    }

    public LocalDateTime getPromotionExpiry() {
        return promotionExpiry;
    }

    public void setPromotionExpiry(LocalDateTime promotionExpiry) {
        this.promotionExpiry = promotionExpiry;
    }

    public void setIsAvailable(boolean available) {
        isAvailable = available;
    }

}