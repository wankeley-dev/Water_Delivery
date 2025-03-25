package com.example.Learn.WaterDeliveryApp.Controllers;

import com.example.Learn.WaterDeliveryApp.Entity.Review;
import com.example.Learn.WaterDeliveryApp.Entity.Supplier;
import com.example.Learn.WaterDeliveryApp.Entity.Users;
import com.example.Learn.WaterDeliveryApp.Repository.ReviewRepository;
import com.example.Learn.WaterDeliveryApp.Repository.SupplierRepository;
import com.example.Learn.WaterDeliveryApp.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ReviewController {

    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    // ✅ Show Review Form with Supplier List
    @GetMapping("/reviews")
    public String showReviewForm(Model model) {
        List<Supplier> suppliers = supplierRepository.findAll(); // Fetch suppliers from DB
        model.addAttribute("suppliers", suppliers); // Pass suppliers to the template
        return "review"; // Returns review.html
    }

    // ✅ Submit a Review
    @PostMapping("/addReview")
    public String addReview(@RequestParam("supplierId") Long supplierId,
                            @RequestParam("rating") int rating,
                            @RequestParam("content") String comment,
                            Principal principal) {
        String email = principal.getName();
        Optional<Users> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            Review review = new Review();
            review.setUsers(user.get()); // ✅ Set the logged-in user
            review.setSupplier(supplierRepository.findById(supplierId).orElse(null));
            review.setRating(rating);
            review.setContent(comment);

            reviewRepository.save(review);
            return "redirect:/reviews?success=true";
        } else {
            return "redirect:/customer-dashboard"; // Redirect if user is not found
        }
    }

}
