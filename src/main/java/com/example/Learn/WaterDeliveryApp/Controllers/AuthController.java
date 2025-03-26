package com.example.Learn.WaterDeliveryApp.Controllers;

import com.example.Learn.WaterDeliveryApp.Entity.Role;
import com.example.Learn.WaterDeliveryApp.Entity.Users;
import com.example.Learn.WaterDeliveryApp.Services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.beans.PropertyEditorSupport;

@Controller
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Role.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(Role.valueOf(text.toUpperCase())); // Converts input string to Role enum
            }
        });
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("users", new Users());
        model.addAttribute("roles", Role.values()); // Pass available roles to the view
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute Users users, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Received registration request for user: {} with email: {}", users.getFullName(), users.getEmail());

            // Ensure role is set (defaults to CUSTOMER if not provided)
            if (users.getRole() == null) {
                users.setRole(Role.CUSTOMER);
                logger.info("No role provided, defaulting to CUSTOMER");
            }

            userService.registerUser(users);
            logger.info("User registration successful: {}", users.getEmail());

            redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please log in.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            logger.error("Registration failed for user: {} - Error: {}", users.getEmail(), e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Registration failed: " + e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
}