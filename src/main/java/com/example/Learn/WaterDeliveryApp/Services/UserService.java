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

import java.time.LocalDateTime;
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

        if (userRepository.existsByEmail(users.getEmail())) {
            logger.error("Registration failed: Email {} is already in use.", users.getEmail());
            throw new IllegalStateException("Email is already in use.");
        }

        users.setPassword(passwordEncoder.encode(users.getPassword()));
        users.setRole(Optional.ofNullable(users.getRole()).orElse(Role.CUSTOMER));
        users.setLastLogin(LocalDateTime.now()); // ✅ Set initial login time

        Users savedUsers = userRepository.save(users);
        logger.info("User registered successfully: {}", savedUsers.getEmail());

        if (savedUsers.getRole() == Role.SUPPLIER) {
            Supplier supplier = new Supplier();
            supplier.setUsers(savedUsers);
            supplier.setName("New Supplier");
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

    // ✅ New Methods
    @Transactional
    public Users updatePreferences(Long userId, Long preferredSupplierId, String preferredDeliveryTime) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        user.setPreferredSupplierId(preferredSupplierId);
        user.setPreferredDeliveryTime(preferredDeliveryTime);
        return userRepository.save(user);
    }

    @Transactional
    public void updateLastLogin(String email) {
        Optional<Users> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
            logger.info("Updated last login for user: {}", email);
        }
    }
}