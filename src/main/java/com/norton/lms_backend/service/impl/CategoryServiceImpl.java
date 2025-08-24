package com.norton.lms_backend.service.impl;

import com.norton.lms_backend.exception.NotFoundException;
import com.norton.lms_backend.model.entity.Category;
import com.norton.lms_backend.repository.CategoryRepository;
import com.norton.lms_backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategory() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategory(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category with id " + id + " not found"));
    }
}
