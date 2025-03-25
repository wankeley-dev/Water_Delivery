package com.example.Learn.WaterDeliveryApp.Services;

import com.example.Learn.WaterDeliveryApp.Entity.Role;
import com.example.Learn.WaterDeliveryApp.Entity.Users;
import com.example.Learn.WaterDeliveryApp.Entity.Supplier;
import com.example.Learn.WaterDeliveryApp.Repository.UserRepository;
import com.example.Learn.WaterDeliveryApp.Repository.SupplierRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final SupplierRepository supplierRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, SupplierRepository supplierRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.supplierRepository = supplierRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Users registerUser(Users users) {
        // Check if the email is already registered
        if (userRepository.existsByEmail(users.getEmail())) {
            throw new RuntimeException("Email is already in use.");
        }

        // Encrypt password before saving
        users.setPassword(passwordEncoder.encode(users.getPassword()));

        // Assign default role if not set
        if (users.getRole() == null) {
            users.setRole(Role.CUSTOMER); // Default role is CUSTOMER
        }

        // Save user to database
        Users savedUsers = userRepository.save(users);

        // If user role is SUPPLIER, create a corresponding supplier entry
        if (users.getRole() == Role.SUPPLIER) {
            Supplier supplier = new Supplier();
            supplier.setUsers(savedUsers);
            supplier.setName("");  // Empty fields for supplier to update later
            supplier.setLocation("");
            supplier.setContactDetails("");
            supplier.setPricing(0.00);
            supplier.setDescription("");
            supplier.setImagePath(""); // Empty initially

            supplierRepository.save(supplier);
        }

        return savedUsers;
    }

    public Optional<Users> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
