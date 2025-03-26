package com.example.Learn.WaterDeliveryApp.Controllers;

import com.example.Learn.WaterDeliveryApp.Entity.Review;
import com.example.Learn.WaterDeliveryApp.Entity.Supplier;
import com.example.Learn.WaterDeliveryApp.Entity.Users;
import com.example.Learn.WaterDeliveryApp.Repository.ReviewRepository;
import com.example.Learn.WaterDeliveryApp.Repository.SupplierRepository;
import com.example.Learn.WaterDeliveryApp.Repository.UserRepository;
import com.example.Learn.WaterDeliveryApp.Services.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class CustomerController {
    private final SupplierService supplierService;
    private final SupplierRepository supplierRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @GetMapping("/customer-dashboard")
    @Transactional(readOnly = true)
    public String showDashboard(@RequestParam(value = "location", required = false) String location, Model model) {
        List<Supplier> suppliers;

        if (location != null && !location.isEmpty()) {
            suppliers = supplierRepository.searchByLocation(location);
        } else {
            suppliers = supplierRepository.findAll();
        }

        // Attach rating and latest review for each supplier
        for (Supplier supplier : suppliers) {
            List<Review> reviews = reviewRepository.findBySupplier(supplier);
            supplier.setAverageRating(reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0)); // Calculate average rating
            supplier.setLatestReview(reviews.isEmpty() ? null : reviews.get(0)); // Set latest review
        }

        model.addAttribute("suppliers", suppliers);
        return "customer-dashboard"; // Returns customer-dashboard.html
    }

    @GetMapping("/suppliers")
    @Transactional(readOnly = true)
    public String getSuppliers(@RequestParam(required = false) String location, Model model) {
        List<Supplier> suppliers = supplierService.getSuppliersByLocation(location);
        model.addAttribute("suppliers", suppliers);
        return "supplier_list"; // Returns supplier-listing.html
    }

    @GetMapping("/order-history")
    @Transactional(readOnly = true)
    public String showOrderHistory() {
        return "order-history"; // Returns order-history.html
    }

    @GetMapping("/order/{id}")
    @Transactional(readOnly = true)
    public String showOrderPage(@PathVariable Long id, Model model, Principal principal) {
        Optional<Supplier> supplier = supplierRepository.findById(id);

        if (supplier.isPresent()) {
            // Retrieve the logged-in user
            String email = principal.getName();
            Optional<Users> user = userRepository.findByEmail(email);

            if (user.isPresent()) {
                model.addAttribute("supplier", supplier.get());
                model.addAttribute("consumer", user.get()); // Pass the logged-in user
                return "order"; // Returns order.html
            } else {
                return "redirect:/login"; // Redirect to login if user not found
            }
        } else {
            return "redirect:/customer-dashboard"; // Redirect if supplier not found
        }
    }
}