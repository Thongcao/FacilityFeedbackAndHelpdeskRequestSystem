package com.example.helpdesk.controller;

import com.example.helpdesk.entity.Ticket;
import com.example.helpdesk.entity.User;
import com.example.helpdesk.service.ExcelImportService;
import com.example.helpdesk.service.TicketService;
import com.example.helpdesk.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Controller for admin/staff dashboard and management pages.
 * 
 * @author Facility Helpdesk Team
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final TicketService ticketService;
    private final UserService userService;
    private final ExcelImportService excelImportService;

    public AdminController(TicketService ticketService, UserService userService, ExcelImportService excelImportService) {
        this.ticketService = ticketService;
        this.userService = userService;
        this.excelImportService = excelImportService;
    }

    /**
     * Display admin/staff dashboard with statistics.
     * 
     * @param model the model to add attributes
     * @param authentication the authentication object
     * @return the view name for the admin dashboard
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        logger.info("Displaying admin dashboard for user: {}", authentication.getName());
        
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean isAdmin = authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        // Get ticket statistics
        List<Ticket> allTickets = ticketService.getAllTickets();
        long totalTickets = allTickets.size();
        long pendingTickets = ticketService.getTicketsByStatus("CREATED").size() +
                             ticketService.getTicketsByStatus("ASSIGNED").size();
        long resolvedTickets = ticketService.getTicketsByStatus("RESOLVED").size() +
                              ticketService.getTicketsByStatus("CLOSED").size();
        long overdueTickets = ticketService.getTicketsByStatus("OVERDUE").size();
        
        model.addAttribute("username", authentication.getName());
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("totalTickets", totalTickets);
        model.addAttribute("pendingTickets", pendingTickets);
        model.addAttribute("resolvedTickets", resolvedTickets);
        model.addAttribute("overdueTickets", overdueTickets);
        
        return "admin-dashboard";
    }

    /**
     * Display all tickets list for admin/staff.
     * 
     * @param model the model to add attributes
     * @param status optional status filter
     * @return the view name for the tickets list
     */
    @GetMapping("/tickets")
    public String listTickets(Model model, @RequestParam(required = false) String status) {
        logger.info("Displaying tickets list for admin/staff, filter: {}", status);

        List<Ticket> tickets;
        if (status != null && !status.isEmpty()) {
            tickets = ticketService.getTicketsByStatus(status);
            model.addAttribute("currentFilter", status);
        } else {
            tickets = ticketService.getAllTickets();
        }

        model.addAttribute("tickets", tickets);
        return "admin-ticket-list";
    }

    /**
     * Display ticket detail for admin/staff.
     * 
     * @param id the ticket ID
     * @param model the model to add attributes
     * @return the view name for the ticket detail
     */
    @GetMapping("/tickets/{id}")
    public String viewTicket(@PathVariable Long id, Model model) {
        logger.info("Displaying ticket detail for ID: {}", id);

        Ticket ticket = ticketService.getTicketById(id);
        model.addAttribute("ticket", ticket);

        return "admin-ticket-detail";
    }

    /**
     * Update ticket status.
     * 
     * @param id the ticket ID
     * @param status the new status
     * @param redirectAttributes attributes for redirect
     * @return redirect URL
     */
    @PostMapping("/tickets/{id}/status")
    public String updateTicketStatus(
            @PathVariable Long id,
            @RequestParam String status,
            RedirectAttributes redirectAttributes) {
        
        logger.info("Updating ticket {} status to: {}", id, status);

        try {
            ticketService.updateTicketStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Ticket status updated successfully to: " + status);
        } catch (IllegalArgumentException e) {
            logger.error("Error updating ticket status: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/tickets/" + id;
    }

    /**
     * Display all users list (ADMIN ONLY).
     * 
     * @param model the model to add attributes
     * @param authentication the authentication object
     * @return the view name for the users list
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String listUsers(Model model, Authentication authentication) {
        logger.info("Displaying users list (ADMIN ONLY)");

        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("currentUserEmail", authentication.getName());

        return "admin-user-list";
    }

    /**
     * Display user detail and edit form (ADMIN ONLY).
     * 
     * @param id the user ID
     * @param model the model to add attributes
     * @param authentication the authentication object
     * @return the view name for the user detail
     */
    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String viewUser(@PathVariable Long id, Model model, Authentication authentication) {
        logger.info("Displaying user detail for ID: {} (ADMIN ONLY)", id);

        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("currentUserEmail", authentication.getName());
        model.addAttribute("isCurrentUser", user.getEmail().equals(authentication.getName()));

        return "admin-user-detail";
    }

    /**
     * Update user information (ADMIN ONLY).
     * 
     * @param id the user ID
     * @param fullName the new full name
     * @param email the new email
     * @param role the new role
     * @param redirectAttributes attributes for redirect
     * @return redirect URL
     */
    @PostMapping("/users/{id}/update")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateUser(
            @PathVariable Long id,
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String role,
            RedirectAttributes redirectAttributes) {
        
        logger.info("Updating user {} (ADMIN ONLY)", id);

        try {
            userService.updateUser(id, fullName, email, role);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "User updated successfully");
        } catch (IllegalArgumentException e) {
            logger.error("Error updating user: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/users/" + id;
    }

    /**
     * Change user role (ADMIN ONLY).
     * 
     * @param id the user ID
     * @param newRole the new role
     * @param redirectAttributes attributes for redirect
     * @return redirect URL
     */
    @PostMapping("/users/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public String changeUserRole(
            @PathVariable Long id,
            @RequestParam String newRole,
            RedirectAttributes redirectAttributes) {
        
        logger.info("Changing user {} role to: {} (ADMIN ONLY)", id, newRole);

        try {
            userService.changeUserRole(id, newRole);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "User role changed successfully to: " + newRole);
        } catch (IllegalArgumentException e) {
            logger.error("Error changing user role: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/users/" + id;
    }

    /**
     * Delete user (ADMIN ONLY).
     * Prevents admin from deleting their own account.
     * 
     * @param id the user ID
     * @param authentication the authentication object
     * @param redirectAttributes attributes for redirect
     * @return redirect URL
     */
    @PostMapping("/users/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUser(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        logger.info("Deleting user {} (ADMIN ONLY) by user: {}", id, authentication.getName());

        try {
            userService.deleteUser(id, authentication.getName());
            redirectAttributes.addFlashAttribute("successMessage", 
                    "User deleted successfully");
        } catch (IllegalArgumentException e) {
            logger.error("Error deleting user: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/users";
    }

    /**
     * Import users from Excel file (ADMIN ONLY).
     * 
     * @param file the Excel file to import
     * @param redirectAttributes attributes for redirect
     * @return redirect URL
     */
    @PostMapping("/users/import")
    @PreAuthorize("hasRole('ADMIN')")
    public String importUsersFromExcel(
            @RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes) {
        
        logger.info("Importing users from Excel file: {} (ADMIN ONLY)", file.getOriginalFilename());

        // Validate file
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a file to upload");
            return "redirect:/admin/users";
        }

        // Validate file extension
        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Invalid file format. Please upload an Excel file (.xlsx or .xls)");
            return "redirect:/admin/users";
        }

        try {
            ExcelImportService.ImportResult result = excelImportService.importUsersFromExcel(file);
            
            if (result.getSuccessCount() > 0) {
                redirectAttributes.addFlashAttribute("successMessage", 
                        String.format("Successfully imported %d user(s)", result.getSuccessCount()));
            }
            
            if (result.getFailedCount() > 0) {
                StringBuilder errorMsg = new StringBuilder();
                errorMsg.append(String.format("Failed to import %d user(s). ", result.getFailedCount()));
                
                // Show first 10 errors
                List<String> errors = result.getErrors();
                int errorCount = Math.min(10, errors.size());
                for (int i = 0; i < errorCount; i++) {
                    errorMsg.append(errors.get(i));
                    if (i < errorCount - 1) {
                        errorMsg.append("; ");
                    }
                }
                
                if (errors.size() > 10) {
                    errorMsg.append(String.format(" ... and %d more error(s)", errors.size() - 10));
                }
                
                redirectAttributes.addFlashAttribute("errorMessage", errorMsg.toString());
            }

            // Store all errors in session for detailed view (optional)
            if (!result.getErrors().isEmpty()) {
                redirectAttributes.addFlashAttribute("importErrors", result.getErrors());
            }

        } catch (Exception e) {
            logger.error("Error importing users from Excel: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Error importing users: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }

    /**
     * Download Excel template file (ADMIN ONLY).
     * 
     * @param response the HTTP response
     * @throws IOException if error generating file
     */
    @GetMapping("/users/import/template")
    @PreAuthorize("hasRole('ADMIN')")
    public void downloadExcelTemplate(HttpServletResponse response) throws IOException {
        logger.info("Downloading Excel template (ADMIN ONLY)");

        byte[] excelBytes = excelImportService.generateExcelTemplate();

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=user_import_template.xlsx");
        response.setContentLength(excelBytes.length);

        response.getOutputStream().write(excelBytes);
        response.getOutputStream().flush();
    }
}

