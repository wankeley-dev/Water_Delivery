package com.example.Learn.WaterDeliveryApp.Services;

import com.example.Learn.WaterDeliveryApp.Entity.Order;
import com.example.Learn.WaterDeliveryApp.Entity.OrderStatus;
import com.example.Learn.WaterDeliveryApp.Entity.Review;
import com.example.Learn.WaterDeliveryApp.Entity.Users;
import com.example.Learn.WaterDeliveryApp.Repository.OrderRepository;
import com.example.Learn.WaterDeliveryApp.Repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);

    @Transactional
    public Review submitReview(Review review) {
        // Check if user has a delivered order with the supplier
        Users user = review.getUsers();
        Order order = review.getOrder();
        if (order != null && order.getOrderStatus() == OrderStatus.DELIVERED &&
                order.getConsumer().equals(user) && order.getSupplier().equals(review.getSupplier())) {
            review.setVerified(true);
            logger.info("Review for order ID {} marked as verified.", order.getId());
        } else if (order != null && reviewRepository.existsByUsersAndOrder(user, order)) {
            throw new IllegalStateException("User has already reviewed this order.");
        }

        return reviewRepository.save(review);
    }
}