package com.example.Learn.WaterDeliveryApp.Services;

import com.example.Learn.WaterDeliveryApp.Entity.Review;
import com.example.Learn.WaterDeliveryApp.Repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    @Transactional
    public Review submitReview(Review review) {
        return reviewRepository.save(review);
    }
}