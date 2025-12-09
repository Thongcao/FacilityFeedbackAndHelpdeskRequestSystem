package com.example.helpdesk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Home controller for root path handling.
 * Always redirects to login page. After login, users will be redirected based on their role.
 * 
 * @author Facility Helpdesk Team
 */
@Controller
public class HomeController {

    /**
     * Redirect root path to login page.
     * Always redirects to login page - users should login first.
     * After login, they will be redirected to appropriate page by CustomAuthenticationSuccessHandler.
     * 
     * @return redirect to login page
     */
    @GetMapping("/")
    public String home() {
        // Always redirect to login page
        // After successful login, CustomAuthenticationSuccessHandler will redirect based on role
        return "redirect:/login";
    }
}



