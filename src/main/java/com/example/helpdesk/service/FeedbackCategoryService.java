package com.example.helpdesk.service;

import com.example.helpdesk.entity.FeedbackCategory;

import java.util.List;

/**
 * Service interface for FeedbackCategory operations.
 * 
 * @author Facility Helpdesk Team
 */
public interface FeedbackCategoryService {
    List<FeedbackCategory> findAll();
}



