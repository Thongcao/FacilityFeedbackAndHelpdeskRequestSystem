package com.example.helpdesk.controller;

import com.example.helpdesk.entity.Department;
import com.example.helpdesk.entity.FeedbackCategory;
import com.example.helpdesk.entity.Ticket;
import com.example.helpdesk.entity.User;
import com.example.helpdesk.repository.DepartmentRepository;
import com.example.helpdesk.repository.FeedbackCategoryRepository;
import com.example.helpdesk.repository.UserRepository;
import com.example.helpdesk.service.DepartmentService;
import com.example.helpdesk.service.FeedbackCategoryService;
import com.example.helpdesk.service.TicketService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for handling ticket-related HTTP requests.
 * Provides endpoints for submitting and viewing tickets.
 * 
 * @author Facility Helpdesk Team
 */
@Controller
@RequestMapping("/tickets")
public class TicketController {

    private static final Logger logger = LoggerFactory.getLogger(TicketController.class);

    private final TicketService ticketService;
    private final DepartmentService departmentService;
    private final FeedbackCategoryService categoryService;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final FeedbackCategoryRepository categoryRepository;

    public TicketController(
            TicketService ticketService,
            DepartmentService departmentService,
            FeedbackCategoryService categoryService,
            UserRepository userRepository,
            DepartmentRepository departmentRepository,
            FeedbackCategoryRepository categoryRepository) {
        this.ticketService = ticketService;
        this.departmentService = departmentService;
        this.categoryService = categoryService;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Display the ticket submission form.
     * 
     * @param model the model to add attributes
     * @return the view name for the ticket submission form
     */
    @GetMapping("/new")
    public String showTicketForm(Model model) {
        logger.info("Displaying ticket submission form");

        // Create new ticket object for form binding
        Ticket ticket = new Ticket();
        ticket.setPriority("MEDIUM");
        ticket.setStatus("CREATED");

        // Add ticket to model for form binding
        model.addAttribute("ticket", ticket);

        // Add departments and categories for dropdowns
        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("categories", categoryService.findAll());

        return "submit-ticket";
    }

    /**
     * Process ticket submission.
     * Validates the ticket and saves it to the database.
     * 
     * @param ticket the ticket entity from the form
     * @param bindingResult the binding result for validation errors
     * @param model the model to add attributes
     * @param redirectAttributes attributes for redirect
     * @return redirect URL or form view if validation fails
     */
    @PostMapping
    public String submitTicket(
            @Valid @ModelAttribute Ticket ticket,
            BindingResult bindingResult,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long categoryId,
            Model model,
            RedirectAttributes redirectAttributes) {

        logger.info("Processing ticket submission with subject: {}", ticket.getSubject());

        // Check for validation errors
        if (bindingResult.hasErrors()) {
            logger.warn("Ticket submission has validation errors");
            // Re-populate form data
            model.addAttribute("departments", departmentService.findAll());
            model.addAttribute("categories", categoryService.findAll());
            return "submit-ticket";
        }

        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User currentUser = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalStateException("User not found: " + email));

            // Set the creator of the ticket
            ticket.setCreatedBy(currentUser);

            // Set department if provided
            if (departmentId != null) {
                Department department = departmentRepository.findById(departmentId)
                        .orElse(null);
                ticket.setDepartment(department);
            }

            // Set category if provided
            if (categoryId != null) {
                FeedbackCategory category = categoryRepository.findById(categoryId)
                        .orElse(null);
                ticket.setCategory(category);
            }

            // Delegate to service layer for business logic
            Ticket savedTicket = ticketService.createTicket(ticket);

            logger.info("Ticket submitted successfully with ID: {}", savedTicket.getId());
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Ticket submitted successfully! Ticket ID: " + savedTicket.getId());

            return "redirect:/tickets";

        } catch (IllegalArgumentException e) {
            logger.error("Error creating ticket: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("departments", departmentService.findAll());
            model.addAttribute("categories", categoryService.findAll());
            return "submit-ticket";
        }
    }

    /**
     * Display list of tickets.
     * 
     * @param model the model to add attributes
     * @return the view name for the ticket list
     */
    @GetMapping
    public String listTickets(Model model) {
        logger.info("Displaying ticket list");
        // This will be implemented later
        return "ticket-list";
    }
}

