package com.norton.lms_backend.controller;

import com.norton.lms_backend.model.dto.response.ApiResponse;
import com.norton.lms_backend.model.entity.Category;
import com.norton.lms_backend.service.CategoryService;
import com.norton.lms_backend.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
@RestController
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategory() {
        return ResponseUtils.createResponse("Get all category", categoryService.getAllCategory());
    }

    @GetMapping("{category-id}")
    public ResponseEntity<ApiResponse<Category>> getCategoryById(@PathVariable("category-id") Long id) {
        return ResponseUtils.createResponse("Get all category", categoryService.getCategory(id));
    }
}
