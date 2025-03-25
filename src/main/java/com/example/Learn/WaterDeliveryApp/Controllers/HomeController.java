package com.example.Learn.WaterDeliveryApp.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "redirect:/login";  // Or redirect to another page like "/dashboard"
    }
}
