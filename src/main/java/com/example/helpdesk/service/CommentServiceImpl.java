package com.example.helpdesk.service;

import com.example.helpdesk.entity.Comment;
import com.example.helpdesk.entity.Ticket;
import com.example.helpdesk.entity.User;
import com.example.helpdesk.repository.CommentRepository;
import com.example.helpdesk.repository.TicketRepository;
import com.example.helpdesk.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service implementation for Comment operations.
 * 
 * @author Facility Helpdesk Team
 */
@Service
public class CommentServiceImpl implements CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public CommentServiceImpl(CommentRepository commentRepository,
                              TicketRepository ticketRepository,
                              UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Comment addComment(Long ticketId, String content, String userEmail) {
        logger.info("Adding comment to ticket {} by user {}", ticketId, userEmail);

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found: " + ticketId));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userEmail));

        Comment comment = Comment.builder()
                .content(content)
                .createdAt(LocalDateTime.now())
                .aiGenerated(false)
                .ticket(ticket)
                .author(user)
                .build();

        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public Comment addAiComment(Long ticketId, String content, String userEmail) {
        logger.info("Adding AI-generated comment to ticket {} by user {}", ticketId, userEmail);

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found: " + ticketId));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userEmail));

        Comment comment = Comment.builder()
                .content(content)
                .createdAt(LocalDateTime.now())
                .aiGenerated(true)
                .ticket(ticket)
                .author(user)
                .build();

        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getCommentsByTicket(Long ticketId) {
        return commentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId);
    }
}
