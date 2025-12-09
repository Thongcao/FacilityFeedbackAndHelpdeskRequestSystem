package com.example.helpdesk.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Room entity representing rooms within departments.
 * 
 * @author Facility Helpdesk Team
 */
@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String roomName;

    /**
     * Many-to-one relationship with Department.
     * Room belongs to a department.
     */
    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
}



