package com.example.helpdesk.service;

import com.example.helpdesk.entity.Ticket;
import com.example.helpdesk.entity.User;
import com.example.helpdesk.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TicketService.
 * Tests ticket creation with automatic status and timestamp assignment.
 * 
 * @author Facility Helpdesk Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TicketService Tests")
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketServiceImpl ticketService;

    private User testUser;
    private Ticket testTicket;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = User.builder()
                .id(1L)
                .fullName("Test User")
                .email("test@fpt.edu.vn")
                .password("password")
                .role("STUDENT")
                .build();

        // Create test ticket
        testTicket = Ticket.builder()
                .subject("Test Subject")
                .description("Test Description")
                .createdBy(testUser)
                .build();
    }

    @Test
    @DisplayName("Should create ticket with default status CREATED")
    void testCreateTicket_WithDefaultStatus() {
        // Arrange
        Ticket savedTicket = Ticket.builder()
                .id(1L)
                .subject("Test Subject")
                .description("Test Description")
                .status("CREATED")
                .priority("MEDIUM")
                .createdBy(testUser)
                .createdAt(LocalDateTime.now())
                .build();

        when(ticketRepository.save(any(Ticket.class))).thenReturn(savedTicket);

        // Act
        Ticket result = ticketService.createTicket(testTicket);

        // Assert
        assertNotNull(result);
        assertEquals("CREATED", result.getStatus());
        assertEquals("MEDIUM", result.getPriority());
        assertNotNull(result.getCreatedAt());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Should set createdAt timestamp automatically")
    void testCreateTicket_SetsCreatedAtTimestamp() {
        // Arrange
        LocalDateTime beforeCreation = LocalDateTime.now();
        Ticket savedTicket = Ticket.builder()
                .id(1L)
                .subject("Test Subject")
                .description("Test Description")
                .status("CREATED")
                .priority("MEDIUM")
                .createdBy(testUser)
                .createdAt(LocalDateTime.now())
                .build();

        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> {
            Ticket ticket = invocation.getArgument(0);
            assertNotNull(ticket.getCreatedAt());
            assertTrue(ticket.getCreatedAt().isAfter(beforeCreation.minusSeconds(1)));
            return savedTicket;
        });

        // Act
        Ticket result = ticketService.createTicket(testTicket);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getCreatedAt());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Should throw exception when ticket is null")
    void testCreateTicket_ThrowsExceptionWhenTicketIsNull() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ticketService.createTicket(null)
        );

        assertEquals("Ticket cannot be null", exception.getMessage());
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Should throw exception when subject is empty")
    void testCreateTicket_ThrowsExceptionWhenSubjectIsEmpty() {
        // Arrange
        testTicket.setSubject("");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ticketService.createTicket(testTicket)
        );

        assertEquals("Ticket subject is required", exception.getMessage());
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Should throw exception when description is empty")
    void testCreateTicket_ThrowsExceptionWhenDescriptionIsEmpty() {
        // Arrange
        testTicket.setDescription("");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ticketService.createTicket(testTicket)
        );

        assertEquals("Ticket description is required", exception.getMessage());
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Should throw exception when createdBy is null")
    void testCreateTicket_ThrowsExceptionWhenCreatedByIsNull() {
        // Arrange
        testTicket.setCreatedBy(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ticketService.createTicket(testTicket)
        );

        assertEquals("Ticket must have a creator (createdBy)", exception.getMessage());
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Should preserve custom status if provided")
    void testCreateTicket_PreservesCustomStatus() {
        // Arrange
        testTicket.setStatus("ASSIGNED");
        Ticket savedTicket = Ticket.builder()
                .id(1L)
                .subject("Test Subject")
                .description("Test Description")
                .status("ASSIGNED")
                .priority("MEDIUM")
                .createdBy(testUser)
                .createdAt(LocalDateTime.now())
                .build();

        when(ticketRepository.save(any(Ticket.class))).thenReturn(savedTicket);

        // Act
        Ticket result = ticketService.createTicket(testTicket);

        // Assert
        assertNotNull(result);
        assertEquals("ASSIGNED", result.getStatus());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Should preserve custom priority if provided")
    void testCreateTicket_PreservesCustomPriority() {
        // Arrange
        testTicket.setPriority("HIGH");
        Ticket savedTicket = Ticket.builder()
                .id(1L)
                .subject("Test Subject")
                .description("Test Description")
                .status("CREATED")
                .priority("HIGH")
                .createdBy(testUser)
                .createdAt(LocalDateTime.now())
                .build();

        when(ticketRepository.save(any(Ticket.class))).thenReturn(savedTicket);

        // Act
        Ticket result = ticketService.createTicket(testTicket);

        // Assert
        assertNotNull(result);
        assertEquals("HIGH", result.getPriority());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }
}



