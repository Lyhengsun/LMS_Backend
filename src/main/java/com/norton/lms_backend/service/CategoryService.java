package com.norton.lms_backend.service;

import com.norton.lms_backend.model.entity.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getAllCategory();
    Category getCategory(Long id);
}
