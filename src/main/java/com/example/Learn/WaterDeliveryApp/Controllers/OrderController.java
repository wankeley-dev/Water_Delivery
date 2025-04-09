package com.example.Learn.WaterDeliveryApp.Controllers;

import com.example.Learn.WaterDeliveryApp.Entity.Order;
import com.example.Learn.WaterDeliveryApp.Entity.OrderStatus;
import com.example.Learn.WaterDeliveryApp.Entity.Supplier;
import com.example.Learn.WaterDeliveryApp.Entity.Users;
import com.example.Learn.WaterDeliveryApp.Repository.OrderRepository;
import com.example.Learn.WaterDeliveryApp.Services.OrderService;
import com.example.Learn.WaterDeliveryApp.Repository.SupplierRepository;
import com.example.Learn.WaterDeliveryApp.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private final OrderRepository orderRepository;
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @GetMapping("/order")
    public String showOrderForm(@RequestParam("supplierId") Long supplierId, Model model, Principal principal) {
        logger.info("Showing order form for supplier ID: {}", supplierId);
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        if (principal != null) {
            Users user = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            model.addAttribute("preferredDeliveryTime", user.getPreferredDeliveryTime());
        }

        model.addAttribute("supplier", supplier);
        model.addAttribute("currentTime", LocalDateTime.now());
        return "order";
    }

    @PostMapping("/addOrders")
    public String placeOrder(@RequestParam("supplierId") Long supplierId,
                             @RequestParam("quantity") Long quantity,
                             @RequestParam("deliveryAddress") String deliveryAddress,
                             @RequestParam("waterType") String waterType,
                             @RequestParam("pricePerLitre") Double pricePerLitre,
                             @RequestParam("preferredDeliveryTime") String preferredDeliveryTime,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        String email = principal.getName();
        Optional<Users> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            Supplier supplier = supplierRepository.findById(supplierId)
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));

            Order order = new Order();
            order.setConsumer(user.get());
            order.setSupplier(supplier);
            order.setOrderDate(LocalDateTime.now());
            order.setDeliveryAddress(deliveryAddress);
            order.setWaterType(waterType);
            order.setQuantity(quantity);
            order.setPricePerLitre(pricePerLitre);
            order.setPreferredDeliveryTime(preferredDeliveryTime);

            orderService.placeOrder(order);
            redirectAttributes.addFlashAttribute("message", "Order placed successfully!");
            return "redirect:/orders/success";
        } else {
            return "redirect:/customer-dashboard";
        }
    }

    @GetMapping("/my-orders")
    public String getUserOrders(@RequestParam(value = "status", required = false) String status,
                                Model model,
                                Principal principal) {
        String email = principal.getName();

        Optional<Users> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return "error"; // or redirect to login
        }

        Users user = userOptional.get();
        List<Order> orders;

        if (status == null || status.isEmpty()) {
            orders = orderRepository.findByConsumer(user); // All orders
        } else {
            try {
                OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
                orders = orderRepository.findByConsumerAndOrderStatus(user, orderStatus); // Filtered by status
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid status provided: {}", status);
                model.addAttribute("error", "Invalid status selected.");
                orders = orderRepository.findByConsumer(user); // Fallback to all orders
            }
        }

        model.addAttribute("orders", orders);
        model.addAttribute("selectedStatus", status); // Pass selected status for form persistence
        return "order-history";
    }

    @GetMapping("/supplier/pending-orders")
    public String getPendingDeliveries(Model model, Principal principal) {
        String email = principal.getName();
        Optional<Users> supplierUser = userRepository.findByEmail(email);

        if (supplierUser.isPresent()) {
            List<Order> pendingOrders = orderService.getPendingDeliveries(supplierUser.get().getId());
            model.addAttribute("pendingOrders", pendingOrders);
            return "supplier-order";
        }
        return "redirect:/supplier-dashboard";
    }

    @GetMapping("/filter-by-price")
    public String getOrdersByTotalPrice(@RequestParam("totalPrice") Long totalPrice, Model model) {
        List<Order> orders = orderService.getOrdersByTotalPrice(totalPrice);
        model.addAttribute("filteredOrders", orders);
        return "filtered-orders";
    }

    @GetMapping("/success")
    public String showSuccessPage(Model model, Principal principal) {
        String email = principal.getName();
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Order> orders = orderService.getCustomerOrders(user.getId());
        Order latestOrder = orders.stream()
                .max((o1, o2) -> o1.getOrderDate().compareTo(o2.getOrderDate()))
                .orElse(null);
        model.addAttribute("order", latestOrder);
        return "order-success";
    }

    @PostMapping("/confirm/{id}")
    public String confirmOrder(@PathVariable Long id, Principal principal) {
        String email = principal.getName();
        Users supplierUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        orderService.confirmOrder(id);
        return "redirect:/orders/supplier/pending-orders";
    }

    @PostMapping("/update-status/{id}")
    public String updateOrderStatus(@PathVariable Long id, @RequestParam("orderStatus") String orderStatusStr) {
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(orderStatusStr.toUpperCase());
            orderService.updateOrderStatus(id, orderStatus);
        } catch (IllegalArgumentException e) {
            return "redirect:/orders?error=invalid_status";
        }
        return "redirect:/orders/my-orders";
    }
}