package com.example.Learn.WaterDeliveryApp.Services;

import com.example.Learn.WaterDeliveryApp.Entity.Role;
import com.example.Learn.WaterDeliveryApp.Entity.Users;
import com.example.Learn.WaterDeliveryApp.Entity.Supplier;
import com.example.Learn.WaterDeliveryApp.Repository.UserRepository;
import com.example.Learn.WaterDeliveryApp.Repository.SupplierRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final SupplierRepository supplierRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, SupplierRepository supplierRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.supplierRepository = supplierRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Users registerUser(Users users) {
        logger.info("Attempting to register user with email: {}", users.getEmail());

        // Check if the email is already registered
        if (userRepository.existsByEmail(users.getEmail())) {
            logger.error("Registration failed: Email {} is already in use.", users.getEmail());
            throw new IllegalStateException("Email is already in use.");
        }

        // Encrypt password before saving
        users.setPassword(passwordEncoder.encode(users.getPassword()));

        // Assign default role if not set
        users.setRole(Optional.ofNullable(users.getRole()).orElse(Role.CUSTOMER));

        // Save user to database
        Users savedUsers = userRepository.save(users);
        logger.info("User registered successfully: {}", savedUsers.getEmail());

        // If user is a supplier, create a corresponding supplier entry
        if (savedUsers.getRole() == Role.SUPPLIER) {
            Supplier supplier = new Supplier();
            supplier.setUsers(savedUsers);
            supplier.setName("New Supplier");  // Default name
            supplier.setLocation("Not Set");
            supplier.setContactDetails("Not Set");
            supplier.setPricing(0.00);
            supplier.setDescription("Supplier description pending update.");

            supplierRepository.save(supplier);
            logger.info("Supplier profile created for user: {}", savedUsers.getEmail());
        }

        return savedUsers;
    }

    @Transactional(readOnly = true)
    public Optional<Users> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}