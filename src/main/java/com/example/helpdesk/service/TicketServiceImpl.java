package com.example.helpdesk.service;

import com.example.helpdesk.entity.Ticket;
import com.example.helpdesk.repository.TicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation for Ticket business logic.
 * Handles ticket creation with automatic status and timestamp assignment.
 * 
 * @author Facility Helpdesk Team
 */
@Service
public class TicketServiceImpl implements TicketService {

    private static final Logger logger = LoggerFactory.getLogger(TicketServiceImpl.class);

    private static final String DEFAULT_STATUS = "CREATED";
    private static final String DEFAULT_PRIORITY = "MEDIUM";

    private final TicketRepository ticketRepository;

    public TicketServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    /**
     * Create a new ticket with automatic status and timestamp assignment.
     * 
     * Business rules:
     * - Status is automatically set to CREATED
     * - CreatedAt timestamp is automatically set to current time
     * - Priority defaults to MEDIUM if not provided
     * - Validates that subject and description are not empty
     * - Validates that createdBy user is not null
     * 
     * @Transactional ensures that the entire operation is atomic.
     * If any validation fails or database operation fails, the transaction is rolled back.
     * This maintains data consistency and prevents partial saves.
     * 
     * @param ticket the ticket entity to create
     * @return the saved ticket with generated ID and default values
     * @throws IllegalArgumentException if validation fails
     */
    @Override
    @Transactional
    public Ticket createTicket(Ticket ticket) {
        logger.info("Creating new ticket with subject: {}", ticket.getSubject());

        // Validate required fields
        validateTicket(ticket);

        // Set default status if not provided
        if (ticket.getStatus() == null || ticket.getStatus().isEmpty()) {
            ticket.setStatus(DEFAULT_STATUS);
            logger.debug("Set default status to: {}", DEFAULT_STATUS);
        }

        // Set default priority if not provided
        if (ticket.getPriority() == null || ticket.getPriority().isEmpty()) {
            ticket.setPriority(DEFAULT_PRIORITY);
            logger.debug("Set default priority to: {}", DEFAULT_PRIORITY);
        }

        // Set creation timestamp
        ticket.setCreatedAt(java.time.LocalDateTime.now());
        logger.debug("Set createdAt timestamp to: {}", ticket.getCreatedAt());

        // Save ticket to database
        Ticket savedTicket = ticketRepository.save(ticket);
        logger.info("Ticket created successfully with ID: {}", savedTicket.getId());

        return savedTicket;
    }

    /**
     * Validate ticket business rules.
     * 
     * @param ticket the ticket to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateTicket(Ticket ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket cannot be null");
        }

        if (ticket.getSubject() == null || ticket.getSubject().trim().isEmpty()) {
            throw new IllegalArgumentException("Ticket subject is required");
        }

        if (ticket.getDescription() == null || ticket.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Ticket description is required");
        }

        if (ticket.getCreatedBy() == null) {
            throw new IllegalArgumentException("Ticket must have a creator (createdBy)");
        }

        logger.debug("Ticket validation passed");
    }

    /**
     * Get all tickets.
     * 
     * @return list of all tickets
     */
    @Override
    public List<Ticket> getAllTickets() {
        logger.info("Retrieving all tickets");
        return ticketRepository.findAll();
    }

    /**
     * Get ticket by ID.
     * 
     * @param id the ticket ID
     * @return the ticket if found
     * @throws IllegalArgumentException if ticket not found
     */
    @Override
    public Ticket getTicketById(Long id) {
        logger.info("Retrieving ticket with ID: {}", id);
        return ticketRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Ticket not found with ID: {}", id);
                    return new IllegalArgumentException("Ticket not found with ID: " + id);
                });
    }

    /**
     * Update ticket status.
     * Validates that the new status is valid.
     * 
     * @param ticketId the ticket ID
     * @param newStatus the new status to set
     * @return the updated ticket
     * @throws IllegalArgumentException if ticket not found or invalid status
     */
    @Override
    @Transactional
    public Ticket updateTicketStatus(Long ticketId, String newStatus) {
        logger.info("Updating ticket {} status to: {}", ticketId, newStatus);

        // Validate status
        validateStatus(newStatus);

        // Get ticket
        Ticket ticket = getTicketById(ticketId);

        // Update status
        ticket.setStatus(newStatus);
        Ticket updatedTicket = ticketRepository.save(ticket);

        logger.info("Ticket {} status updated to: {}", ticketId, newStatus);
        return updatedTicket;
    }

    /**
     * Get tickets by status.
     * 
     * @param status the status to filter by
     * @return list of tickets with the given status
     */
    @Override
    public List<Ticket> getTicketsByStatus(String status) {
        logger.info("Retrieving tickets with status: {}", status);
        validateStatus(status);
        return ticketRepository.findByStatus(status);
    }

    /**
     * Validate ticket status.
     * 
     * @param status the status to validate
     * @throws IllegalArgumentException if status is invalid
     */
    private void validateStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }

        List<String> validStatuses = List.of(
                "CREATED", "ASSIGNED", "IN_PROGRESS", "RESOLVED", "CLOSED", "OVERDUE"
        );

        if (!validStatuses.contains(status.toUpperCase())) {
            throw new IllegalArgumentException("Invalid status: " + status + 
                    ". Valid statuses are: " + validStatuses);
        }
    }
}

