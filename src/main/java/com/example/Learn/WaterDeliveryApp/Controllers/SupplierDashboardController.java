package com.example.Learn.WaterDeliveryApp.Controllers;

import com.example.Learn.WaterDeliveryApp.Entity.*;
import com.example.Learn.WaterDeliveryApp.Repository.OrderRepository;
import com.example.Learn.WaterDeliveryApp.Repository.ReviewRepository;
import com.example.Learn.WaterDeliveryApp.Repository.SupplierRepository;
import com.example.Learn.WaterDeliveryApp.Services.OrderService;
import com.example.Learn.WaterDeliveryApp.Services.ReviewService;
import com.example.Learn.WaterDeliveryApp.Services.SupplierService;
import com.example.Learn.WaterDeliveryApp.Repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class SupplierDashboardController {

    private final SupplierService supplierService;
    private final UserRepository userRepository;
    private final SupplierRepository supplierRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;

    public SupplierDashboardController(SupplierService supplierService, ReviewRepository reviewRepository,ReviewService reviewService, OrderService orderService,UserRepository userRepository, SupplierRepository supplierRepository, OrderRepository orderRepository) {
        this.supplierService = supplierService;
        this.userRepository = userRepository;
        this.supplierRepository = supplierRepository;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
        this.reviewService = reviewService;
        this.reviewRepository = reviewRepository;

    }

    @GetMapping("/supplier-dashboard")
    public String showSupplierDashboard(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        Optional<Users> supplierUserOptional = userRepository.findByEmail(email);

        if (supplierUserOptional.isEmpty()) {
           // logger.warn("No user found for email: {}, redirecting to login", email);
            return "redirect:/login";
        }

        Users supplierUser = supplierUserOptional.get();
        Optional<Supplier> supplierOptional = supplierRepository.findByUsersId(supplierUser.getId());

        if (supplierOptional.isEmpty()) {
           // logger.warn("No supplier profile found for user ID: {}, redirecting to login", supplierUser.getId());
            return "redirect:/login";
        }

        Supplier supplier = supplierOptional.get();

        // Dashboard stats
        int totalOrders = orderService.countSupplierOrders(supplier.getId());
        int pendingDeliveries = orderService.countOrdersByStatus(supplier.getId(), OrderStatus.PENDING);

        // Recent reviews (top 5)
        List<Review> recentReviews = reviewRepository.findTop5BySupplierIdOrderByCreatedAtDesc(supplier.getId());

        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("pendingDeliveries", pendingDeliveries);
        model.addAttribute("promotionCode", supplier.getPromotionCode());
        model.addAttribute("promotionDiscount", supplier.getPromotionDiscount());
        model.addAttribute("isAvailable", supplier.isAvailable());
        model.addAttribute("recentReviews", recentReviews);

        return "supplier-dashboard";
    }


    @GetMapping("/supplier/orders")
    @Transactional(readOnly = true)
    public String showSupplierOrders(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Users users = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Supplier supplier = supplierRepository.findByUsers(users)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        List<Order> orders = orderRepository.findBySupplier(supplier);
        model.addAttribute("orders", orders);
        return "supplier-deliveries";
    }

}