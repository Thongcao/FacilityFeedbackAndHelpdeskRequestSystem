package com.example.helpdesk.controller;

import com.example.helpdesk.entity.Comment;
import com.example.helpdesk.entity.Department;
import com.example.helpdesk.entity.FeedbackCategory;
import com.example.helpdesk.entity.Ticket;
import com.example.helpdesk.entity.User;
import com.example.helpdesk.repository.DepartmentRepository;
import com.example.helpdesk.repository.FeedbackCategoryRepository;
import com.example.helpdesk.repository.UserRepository;
import com.example.helpdesk.service.CommentService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller for handling ticket-related HTTP requests.
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
    private final CommentService commentService;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final FeedbackCategoryRepository categoryRepository;

    public TicketController(
            TicketService ticketService,
            DepartmentService departmentService,
            FeedbackCategoryService categoryService,
            CommentService commentService,
            UserRepository userRepository,
            DepartmentRepository departmentRepository,
            FeedbackCategoryRepository categoryRepository) {
        this.ticketService = ticketService;
        this.departmentService = departmentService;
        this.categoryService = categoryService;
        this.commentService = commentService;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/new")
    public String showTicketForm(Model model) {
        Ticket ticket = new Ticket();
        ticket.setPriority("MEDIUM");
        ticket.setStatus("CREATED");
        model.addAttribute("ticket", ticket);
        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        return "submit-ticket";
    }

    @PostMapping
    public String submitTicket(
            @Valid @ModelAttribute Ticket ticket,
            BindingResult bindingResult,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long categoryId,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("departments", departmentService.findAll());
            model.addAttribute("categories", categoryService.findAll());
            return "submit-ticket";
        }

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userRepository.findByEmail(auth.getName())
                    .orElseThrow(() -> new IllegalStateException("User not found"));

            ticket.setCreatedBy(currentUser);

            if (departmentId != null) {
                ticket.setDepartment(departmentRepository.findById(departmentId).orElse(null));
            }
            if (categoryId != null) {
                ticket.setCategory(categoryRepository.findById(categoryId).orElse(null));
            }

            Ticket saved = ticketService.createTicket(ticket);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Ticket submitted successfully! ID: " + saved.getId());
            return "redirect:/tickets";

        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("departments", departmentService.findAll());
            model.addAttribute("categories", categoryService.findAll());
            return "submit-ticket";
        }
    }

    @GetMapping
    public String listTickets(Model model, @RequestParam(required = false) String status) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        List<Ticket> tickets;
        if (status != null && !status.isEmpty()) {
            tickets = ticketService.getTicketsByUserAndStatus(currentUser.getId(), status);
            model.addAttribute("currentFilter", status);
        } else {
            tickets = ticketService.getTicketsByUser(currentUser.getId());
        }

        model.addAttribute("tickets", tickets);
        model.addAttribute("username", currentUser.getFullName());
        return "ticket-list";
    }

    @GetMapping("/{id}")
    public String viewTicket(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        Ticket ticket = ticketService.getTicketById(id);

        if (!ticket.getCreatedBy().getId().equals(currentUser.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You can only view your own tickets.");
            return "redirect:/tickets";
        }

        List<Comment> comments = commentService.getCommentsByTicket(id);
        model.addAttribute("ticket", ticket);
        model.addAttribute("comments", comments);
        return "ticket-detail";
    }

    @PostMapping("/{id}/comment")
    public String addComment(@PathVariable Long id,
                             @RequestParam String content,
                             RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        Ticket ticket = ticketService.getTicketById(id);
        if (!ticket.getCreatedBy().getId().equals(currentUser.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You can only comment on your own tickets.");
            return "redirect:/tickets";
        }

        try {
            commentService.addComment(id, content, auth.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Reply sent successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/tickets/" + id;
    }
}
