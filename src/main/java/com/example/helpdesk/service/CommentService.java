package com.example.helpdesk.service;

import com.example.helpdesk.entity.Comment;

import java.util.List;

/**
 * Service interface for Comment operations.
 * 
 * @author Facility Helpdesk Team
 */
public interface CommentService {

    Comment addComment(Long ticketId, String content, String userEmail);

    Comment addAiComment(Long ticketId, String content, String userEmail);

    List<Comment> getCommentsByTicket(Long ticketId);
}
