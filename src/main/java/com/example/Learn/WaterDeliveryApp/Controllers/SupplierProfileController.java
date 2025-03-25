package com.example.Learn.WaterDeliveryApp.Controllers;

import com.example.Learn.WaterDeliveryApp.Entity.Supplier;
import com.example.Learn.WaterDeliveryApp.Entity.Users;
import com.example.Learn.WaterDeliveryApp.Repository.SupplierRepository;
import com.example.Learn.WaterDeliveryApp.Repository.UserRepository;
import com.example.Learn.WaterDeliveryApp.Services.SupplierService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.UUID;

@Controller
@RequestMapping("/supplier/profile")
public class SupplierProfileController {

    private final SupplierService supplierService;
    private final UserRepository userRepository;
    private final SupplierRepository supplierRepository;

    // Updated upload directory (relative to project root)
    private static final String UPLOAD_DIR = "uploads/";

    public SupplierProfileController(SupplierService supplierService, UserRepository userRepository, SupplierRepository supplierRepository) {
        this.supplierService = supplierService;
        this.userRepository = userRepository;
        this.supplierRepository = supplierRepository;
    }

    // GET: Display Profile Form
    @GetMapping("/edit")
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
    public String updateSupplierProfile(@ModelAttribute Supplier supplier,
                                        @RequestParam("image") MultipartFile imageFile,
                                        Principal principal,
                                        RedirectAttributes redirectAttributes) {
        // Get authenticated user
        String email = principal.getName();
        Users users = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get existing supplier by user
        Supplier existingSupplier = supplierRepository.findByUser(users)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        // Update supplier details
        existingSupplier.setName(supplier.getName());
        existingSupplier.setLocation(supplier.getLocation());
        existingSupplier.setContactDetails(supplier.getContactDetails());
        existingSupplier.setPricing(supplier.getPricing());

        // Handle image upload (if a new image is provided)
        if (!imageFile.isEmpty()) {
            try {
                // Ensure upload directory exists
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Generate a unique file name
                String fileExtension = imageFile.getOriginalFilename().substring(imageFile.getOriginalFilename().lastIndexOf("."));
                String newFileName = UUID.randomUUID() + fileExtension;

                // Save file
                Path filePath = uploadPath.resolve(newFileName);
                Files.write(filePath, imageFile.getBytes());

                // Save the image path in the database (relative path for display)
                existingSupplier.setImagePath("/uploads/" + newFileName);
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "Failed to upload image.");
                return "redirect:/supplier/profile/edit";
            }
        }

        // Save updated supplier
        supplierRepository.save(existingSupplier);

        redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        return "redirect:/supplier-dashboard";
    }
}
