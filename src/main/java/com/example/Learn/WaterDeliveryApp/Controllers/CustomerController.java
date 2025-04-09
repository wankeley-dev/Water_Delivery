package com.example.Learn.WaterDeliveryApp.Controllers;

import com.example.Learn.WaterDeliveryApp.Entity.Review;
import com.example.Learn.WaterDeliveryApp.Entity.Supplier;
import com.example.Learn.WaterDeliveryApp.Entity.Users;
import com.example.Learn.WaterDeliveryApp.Repository.ReviewRepository;
import com.example.Learn.WaterDeliveryApp.Repository.SupplierRepository;
import com.example.Learn.WaterDeliveryApp.Repository.UserRepository;
import com.example.Learn.WaterDeliveryApp.Services.SupplierService;
import com.example.Learn.WaterDeliveryApp.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private final UserService userService;

    @GetMapping("/customer-dashboard")
    @Transactional(readOnly = true)
    public String showDashboard(@RequestParam(value = "location", required = false) String location,
                                Principal principal, Model model) {
        // Update last login
        if (principal != null) {
            userService.updateLastLogin(principal.getName());
        }

        List<Supplier> suppliers;
        if (location != null && !location.isEmpty()) {
            suppliers = supplierRepository.searchByLocation(location);
        } else {
            suppliers = supplierRepository.findByIsAvailableTrue(); // ✅ Only show available suppliers
        }

        // Attach rating, latest review, and promotion details
        for (Supplier supplier : suppliers) {
            List<Review> reviews = reviewRepository.findBySupplier(supplier);
            supplier.setAverageRating(reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0));
            supplier.setLatestReview(reviews.isEmpty() ? null : reviews.get(0));
        }

        // Add user preferences
        if (principal != null) {
            Optional<Users> user = userRepository.findByEmail(principal.getName());
            user.ifPresent(u -> {
                model.addAttribute("preferredSupplierId", u.getPreferredSupplierId());
                model.addAttribute("preferredDeliveryTime", u.getPreferredDeliveryTime());
            });
        }

        model.addAttribute("suppliers", suppliers);
        return "customer-dashboard";
    }




    @GetMapping("/order/{id}")
    @Transactional(readOnly = true)
    public String showOrderPage(@PathVariable Long id, Model model, Principal principal) {
        Optional<Supplier> supplier = supplierRepository.findById(id);

        if (supplier.isPresent()) {
            String email = principal.getName();
            Optional<Users> user = userRepository.findByEmail(email);

            if (user.isPresent()) {
                model.addAttribute("supplier", supplier.get());
                model.addAttribute("consumer", user.get());
                return "order";
            } else {
                return "redirect:/login";
            }
        } else {
            return "redirect:/customer-dashboard";
        }
    }

    // ✅ New Endpoint: Show Profile Edit Page
    @GetMapping("/profile/edit")
    @Transactional(readOnly = true)
    public String showProfileEditPage(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        List<Supplier> suppliers = supplierRepository.findAll(); // For supplier dropdown

        model.addAttribute("user", user);
        model.addAttribute("suppliers", suppliers);
        return "profile-edit";
    }

    // ✅ New Endpoint: Update Profile Preferences
    @PostMapping("/profile/update")
    @Transactional
    public String updateProfilePreferences(@RequestParam("preferredSupplierId") Long preferredSupplierId,
                                           @RequestParam("preferredDeliveryTime") String preferredDeliveryTime,
                                           Principal principal,
                                           RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        userService.updatePreferences(user.getId(), preferredSupplierId, preferredDeliveryTime);
        redirectAttributes.addFlashAttribute("success", "Preferences updated successfully!");
        return "redirect:/customer-dashboard";
    }
}