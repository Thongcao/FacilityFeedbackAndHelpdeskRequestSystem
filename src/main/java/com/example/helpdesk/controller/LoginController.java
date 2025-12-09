package com.example.helpdesk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for login page.
 * 
 * @author Facility Helpdesk Team
 */
@Controller
public class LoginController {

    /**
     * Display login page.
     * 
     * @return the view name for the login page
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}



