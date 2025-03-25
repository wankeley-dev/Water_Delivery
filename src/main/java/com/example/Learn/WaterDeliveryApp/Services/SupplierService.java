package com.example.Learn.WaterDeliveryApp.Services;

import com.example.Learn.WaterDeliveryApp.Entity.*;
import com.example.Learn.WaterDeliveryApp.Repository.SupplierRepository;
import com.example.Learn.WaterDeliveryApp.Repository.OrderRepository;
import com.example.Learn.WaterDeliveryApp.Repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SupplierService {
    private final SupplierRepository supplierRepository;
    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;
    private static final String UPLOAD_DIR = "uploads/supplier_images/";

    public List<Supplier> getSuppliersByLocation(String location) {
        return supplierRepository.findAll();
    }

    // Get total number of orders for a supplier
    public int getTotalOrders(Long supplierId) {
        return orderRepository.countBySupplierId(supplierId);
    }

    // Get pending deliveries for a supplier
    public int getPendingDeliveries(Long supplierId) {
        return orderRepository.countBySupplierIdAndOrderStatus(supplierId, OrderStatus.PENDING);
    }

    // Get recent reviews for a supplier
    public List<Review> getRecentReviews(Long supplierId) {
        return reviewRepository.findTop5BySupplierIdOrderByCreatedAtDesc(supplierId);
    }

    public Optional<Supplier> getSupplierByUser(Users users) {
        return supplierRepository.findByUser(users);
    }

    // Update supplier profile
    public Supplier updateSupplierProfile(Supplier supplier1, Supplier supplier, MultipartFile imageFile) {
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                byte[] bytes = imageFile.getBytes();
                Path path = Paths.get(UPLOAD_DIR + imageFile.getOriginalFilename());
                Files.createDirectories(path.getParent());
                Files.write(path, bytes);
                supplier.setImagePath(path.toString());
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image", e);
            }
        }
        return supplierRepository.save(supplier);
    }
}