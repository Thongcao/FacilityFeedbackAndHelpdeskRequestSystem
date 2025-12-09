package com.example.helpdesk.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * FeedbackCategory entity representing categories for tickets.
 * Each category has an SLA (Service Level Agreement) in hours.
 * 
 * @author Facility Helpdesk Team
 */
@Entity
@Table(name = "feedback_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    /**
     * SLA (Service Level Agreement) in hours.
     * Represents the expected time to resolve tickets in this category.
     */
    @Column(nullable = false)
    private Integer slaHours;
}



