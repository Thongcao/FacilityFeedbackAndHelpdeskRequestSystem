package com.example.helpdesk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for admin/staff login page.
 * 
 * @author Facility Helpdesk Team
 */
@Controller
public class AdminLoginController {

    /**
     * Display admin/staff login page.
     * 
     * @return the view name for the admin login page
     */
    @GetMapping("/admin/login")
    public String adminLogin() {
        return "admin-login";
    }
}



