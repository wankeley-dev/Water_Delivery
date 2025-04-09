package com.example.Learn.WaterDeliveryApp.Controllers;

import com.example.Learn.WaterDeliveryApp.Entity.Supplier;
import com.example.Learn.WaterDeliveryApp.Entity.Users;
import com.example.Learn.WaterDeliveryApp.Repository.SupplierRepository;
import com.example.Learn.WaterDeliveryApp.Repository.UserRepository;
import com.example.Learn.WaterDeliveryApp.Services.SupplierService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.UUID;

@Controller
@RequestMapping("/supplier/profile")
public class SupplierProfileController {

    private final SupplierService supplierService;
    private final UserRepository userRepository;
    private final SupplierRepository supplierRepository;
    private static final String UPLOAD_DIR = "uploads/";

    public SupplierProfileController(SupplierService supplierService, UserRepository userRepository, SupplierRepository supplierRepository) {
        this.supplierService = supplierService;
        this.userRepository = userRepository;
        this.supplierRepository = supplierRepository;
    }

    @GetMapping("/edit")
    @Transactional(readOnly = true)
    public String showProfileForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Users users = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Supplier supplier = supplierService.getSupplierByUser(users)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        model.addAttribute("supplier", supplier);
        return "manage-profile";
    }

    @PostMapping("/update")
    @Transactional
    public String updateSupplierProfile(@ModelAttribute Supplier supplier,
                                        @RequestParam("image") MultipartFile imageFile,
                                        Principal principal,
                                        RedirectAttributes redirectAttributes) {
        String email = principal.getName();
        Users users = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Supplier existingSupplier = supplierRepository.findByUsers(users)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        existingSupplier.setName(supplier.getName());
        existingSupplier.setLocation(supplier.getLocation());
        existingSupplier.setContactDetails(supplier.getContactDetails());
        existingSupplier.setPricing(supplier.getPricing());
        existingSupplier.setDeliveryRadiusKm(supplier.getDeliveryRadiusKm()); // ✅ New field
        existingSupplier.setPromotionCode(supplier.getPromotionCode());
        existingSupplier.setPromotionDiscount(supplier.getPromotionDiscount());
        existingSupplier.setPromotionExpiry(supplier.getPromotionExpiry());

        if (!imageFile.isEmpty()) {
            try {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                String fileExtension = imageFile.getOriginalFilename().substring(imageFile.getOriginalFilename().lastIndexOf("."));
                String newFileName = UUID.randomUUID() + fileExtension;
                Path filePath = uploadPath.resolve(newFileName);
                Files.write(filePath, imageFile.getBytes());
                existingSupplier.setImagePath("/uploads/" + newFileName);
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "Failed to upload image.");
                return "redirect:/supplier/profile/edit";
            }
        }

        supplierRepository.save(existingSupplier);
        redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        return "redirect:/supplier-dashboard";
    }

    // ✅ New Endpoint: Toggle Availability
    @PostMapping("/toggle-availability")
    @Transactional
    public String toggleAvailability(Principal principal, RedirectAttributes redirectAttributes) {
        String email = principal.getName();
        Users users = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Supplier supplier = supplierRepository.findByUsers(users)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        boolean newStatus = !supplier.isAvailable();
        supplierService.toggleAvailability(supplier.getId(), newStatus);
        redirectAttributes.addFlashAttribute("success", "Availability updated to " + (newStatus ? "Available" : "Unavailable"));
        return "redirect:/supplier-dashboard";
    }

    // ✅ New Endpoint: Add Promotion
    @PostMapping("/add-promotion")
    @Transactional
    public String addPromotion(@RequestParam("promotionCode") String promotionCode,
                               @RequestParam("discount") double discount,
                               @RequestParam("expiry") String expiry,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        String email = principal.getName();
        Users users = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Supplier supplier = supplierRepository.findByUsers(users)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        LocalDateTime expiryDate = LocalDateTime.parse(expiry);
        supplierService.addPromotion(supplier.getId(), promotionCode, discount, expiryDate);
        redirectAttributes.addFlashAttribute("success", "Promotion added successfully!");
        return "redirect:/supplier-dashboard";
    }
}