package com.example.Learn.WaterDeliveryApp.Controllers;

import com.example.Learn.WaterDeliveryApp.Entity.*;
import com.example.Learn.WaterDeliveryApp.Repository.OrderRepository;
import com.example.Learn.WaterDeliveryApp.Repository.SupplierRepository;
import com.example.Learn.WaterDeliveryApp.Services.SupplierService;
import com.example.Learn.WaterDeliveryApp.Repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class SupplierDashboardController {

    private final SupplierService supplierService;
    private final UserRepository userRepository;
    private final SupplierRepository supplierRepository;
    private final OrderRepository orderRepository;

    public SupplierDashboardController(SupplierService supplierService, UserRepository userRepository, SupplierRepository supplierRepository, OrderRepository orderRepository) {
        this.supplierService = supplierService;
        this.userRepository = userRepository;
        this.supplierRepository = supplierRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/supplier-dashboard")
    @Transactional(readOnly = true)
    public String getDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Users users = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Supplier supplier = supplierRepository.findByUsers(users)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        int totalOrders = supplierService.getTotalOrders(supplier.getId());
        int pendingDeliveries = supplierService.getPendingDeliveries(supplier.getId());
        List<Review> recentReviews = supplierService.getRecentReviews(supplier.getId());

        model.addAttribute("supplier", supplier);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("pendingDeliveries", pendingDeliveries);
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
        return "supplier-order";
    }

    @GetMapping("/supplier/deliveries")
    @Transactional(readOnly = true)
    public String showSupplierDeliveries(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Users users = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Supplier supplier = supplierRepository.findByUsers(users)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        List<Order> pendingDeliveries = orderRepository.findBySupplierAndOrderStatusNot(supplier, OrderStatus.DELIVERED);
        model.addAttribute("pendingDeliveries", pendingDeliveries);
        return "supplier-deliveries";
    }
}