package com.example.helpdesk.service;

import com.example.helpdesk.entity.FeedbackCategory;
import com.example.helpdesk.repository.FeedbackCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for FeedbackCategory operations.
 * 
 * @author Facility Helpdesk Team
 */
@Service
public class FeedbackCategoryServiceImpl implements FeedbackCategoryService {
    private final FeedbackCategoryRepository categoryRepository;

    public FeedbackCategoryServiceImpl(FeedbackCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<FeedbackCategory> findAll() {
        return categoryRepository.findAll();
    }
}








