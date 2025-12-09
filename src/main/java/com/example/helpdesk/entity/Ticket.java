package com.example.helpdesk.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Ticket entity representing helpdesk requests and feedback.
 * 
 * @author Facility Helpdesk Team
 */
@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Subject is required")
    @Column(nullable = false, length = 200)
    private String subject;

    @NotBlank(message = "Description is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    /**
     * Priority levels: LOW, MEDIUM, HIGH, URGENT
     */
    @Column(nullable = false, length = 20)
    private String priority;

    /**
     * Ticket status: CREATED, ASSIGNED, IN_PROGRESS, RESOLVED, CLOSED, OVERDUE
     */
    @Column(nullable = false, length = 20)
    private String status;

    /**
     * Timestamp when ticket was created.
     * Automatically set by service layer.
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * User who created the ticket.
     */
    @ManyToOne
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    /**
     * Department associated with the ticket.
     */
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    /**
     * Room associated with the ticket (optional).
     */
    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    /**
     * Category of the ticket.
     */
    @ManyToOne
    @JoinColumn(name = "category_id")
    private FeedbackCategory category;
}

