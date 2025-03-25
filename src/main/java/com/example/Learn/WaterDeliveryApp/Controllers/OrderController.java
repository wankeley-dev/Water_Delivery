package com.example.Learn.WaterDeliveryApp.Controllers;

import com.example.Learn.WaterDeliveryApp.Entity.Order;
import com.example.Learn.WaterDeliveryApp.Entity.OrderStatus;
import com.example.Learn.WaterDeliveryApp.Entity.Supplier;
import com.example.Learn.WaterDeliveryApp.Entity.Users;
import com.example.Learn.WaterDeliveryApp.Services.OrderService;
import com.example.Learn.WaterDeliveryApp.Repository.SupplierRepository;
import com.example.Learn.WaterDeliveryApp.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);



    @GetMapping("/order")
    public String showOrderForm(@RequestParam("supplierId") Long supplierId, Model model) {
        logger.info("Supplier ID: " + supplierId); // Log the supplier ID
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        model.addAttribute("supplier", supplier);
        return "order";
    }


    // Place a new order
    @PostMapping("/addOrders")
    public String placeOrder(@RequestParam("supplierId") Long supplierId,
                             @RequestParam("quantity") Long quantity,
                             @RequestParam("deliveryAddress") String deliveryAddress,
                             @RequestParam("waterType") String waterType,
                             @RequestParam("pricePerLitre") Double  pricePerLitre,
                             Principal principal) {
        String email = principal.getName(); // Get logged-in user's email
        Optional<Users> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            Supplier supplier = supplierRepository.findById(supplierId).orElse(null);
            if (supplier == null) {
                return "redirect:/orders/new?error=supplier_not_found"; // Redirect if supplier is not found
            }

            Order order = new Order();
            order.setConsumer(user.get());
            order.setSupplier(supplier);
            order.setOrderDate(LocalDateTime.now());
            order.setOrderStatus(OrderStatus.PENDING); // Use Enum
            order.setDeliveryAddress(deliveryAddress);
            order.setWaterType(waterType);
            order.setQuantity(quantity);
            order.setPricePerLitre(pricePerLitre);
            order.setTotalPrice(quantity * pricePerLitre); // Calculate total price

            orderService.placeOrder(order); // Use service to save order
            return "redirect:/orders/success"; // Redirect after order is placed
        } else {
            return "redirect:/customer-dashboard"; // Redirect if user is not found
        }
    }

    // Fetch all orders for the logged-in user
    @GetMapping("/my-orders")
    public String getUserOrders(Model model, Principal principal) {
        String email = principal.getName();
        Optional<Users> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            List<Order> userOrders = orderService.getCustomerOrders(user.get().getId());
            model.addAttribute("userOrders", userOrders);
            return "my-orders";
        }
        return "redirect:/login";
    }

    // Fetch pending deliveries for a supplier
    @GetMapping("/supplier/pending-orders")
    public String getPendingDeliveries(Model model, Principal principal) {
        String email = principal.getName();
        Optional<Users> supplier = userRepository.findByEmail(email);

        if (supplier.isPresent()) {
            List<Order> pendingOrders = orderService.getPendingDeliveries(supplier.get().getId());
            model.addAttribute("pendingOrders", pendingOrders);
            return "supplier-order";
        }
        return "redirect:/supplier-dashboard";
    }

    // Fetch orders with total price >= given amount
    @GetMapping("/filter-by-price")
    public String getOrdersByTotalPrice(@RequestParam("totalPrice") Long totalPrice, Model model) {
        List<Order> orders = orderService.getOrdersByTotalPrice(totalPrice);
        model.addAttribute("filteredOrders", orders);
        return "filtered-orders";
    }

    // Success Page
    @GetMapping("/success")
    public String showSuccessPage() {
        return "order-success"; // Returns order-success.html
    }

    @PostMapping("/update-status/{id}")
    public String updateOrderStatus(@PathVariable Long id, @RequestParam("orderStatus") String orderStatusStr) {
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(orderStatusStr.toUpperCase()); // Convert string to Enum
            orderService.updateOrderStatus(id, orderStatus);
        } catch (IllegalArgumentException e) {
            return "redirect:/orders?error=invalid_status"; // Redirect on error
        }
        return "redirect:/supplier/orders   "; // Redirect after update
    }

}