package com.example.Learn.WaterDeliveryApp.Controllers;

import com.example.Learn.WaterDeliveryApp.Entity.*;
import com.example.Learn.WaterDeliveryApp.Services.OrderService;
import com.example.Learn.WaterDeliveryApp.Services.ReviewService;
import com.example.Learn.WaterDeliveryApp.Repository.SupplierRepository;
import com.example.Learn.WaterDeliveryApp.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReviewController {

    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;
    private final ReviewService reviewService;
    private final OrderService orderService;

    @GetMapping("/reviews")
    public String showReviewForm(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        Users user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        List<Supplier> suppliers = supplierRepository.findAll();
        List<Order> deliveredOrders = orderService.getCustomerOrders(user.getId())
                .stream()
                .filter(order -> order.getOrderStatus() == OrderStatus.DELIVERED)
                .toList();

        model.addAttribute("suppliers", suppliers);
        model.addAttribute("deliveredOrders", deliveredOrders); // Pass delivered orders for selection
        return "review";
    }

    @PostMapping("/addReview")
    public String addReview(@RequestParam("supplierId") Long supplierId,
                            @RequestParam("rating") int rating,
                            @RequestParam("content") String content,
                            @RequestParam(value = "orderId", required = false) Long orderId,
                            Principal principal,
                            RedirectAttributes redirectAttributes) {
        String email = principal.getName();
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        try {
            Review review = new Review();
            review.setUsers(user);
            review.setSupplier(supplierRepository.findById(supplierId)
                    .orElseThrow(() -> new IllegalArgumentException("Supplier not found")));
            review.setRating(rating);
            review.setContent(content);
            if (orderId != null) {
                Order order = orderService.getCustomerOrders(user.getId())
                        .stream()
                        .filter(o -> o.getId().equals(orderId))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Order not found"));
                review.setOrder(order);
            }

            reviewService.submitReview(review);
            redirectAttributes.addFlashAttribute("success", "Review submitted successfully!");
            return "redirect:/reviews";
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/reviews";
        }
    }
}