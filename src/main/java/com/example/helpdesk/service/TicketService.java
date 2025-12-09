package com.example.helpdesk.service;

import com.example.helpdesk.entity.Ticket;

import java.util.List;

/**
 * Service interface for Ticket business logic.
 * Defines operations for managing tickets.
 * 
 * @author Facility Helpdesk Team
 */
public interface TicketService {

    /**
     * Create a new ticket with automatic status and timestamp assignment.
     * Validates business rules before saving.
     * 
     * @param ticket the ticket entity to create
     * @return the saved ticket with generated ID and default values
     * @throws IllegalArgumentException if validation fails
     */
    Ticket createTicket(Ticket ticket);

    /**
     * Get all tickets.
     * 
     * @return list of all tickets
     */
    List<Ticket> getAllTickets();

    /**
     * Get ticket by ID.
     * 
     * @param id the ticket ID
     * @return the ticket if found
     * @throws IllegalArgumentException if ticket not found
     */
    Ticket getTicketById(Long id);

    /**
     * Update ticket status.
     * 
     * @param ticketId the ticket ID
     * @param newStatus the new status to set
     * @return the updated ticket
     * @throws IllegalArgumentException if ticket not found or invalid status
     */
    Ticket updateTicketStatus(Long ticketId, String newStatus);

    /**
     * Get tickets by status.
     * 
     * @param status the status to filter by
     * @return list of tickets with the given status
     */
    List<Ticket> getTicketsByStatus(String status);
}

