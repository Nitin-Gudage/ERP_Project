package com.erp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.erp.dto.CategoryRequest;
import com.erp.entity.Category;
import com.erp.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // Create Category
    public Category create(CategoryRequest request) {
    	
        Category category = new Category();

        category.setCategoryName(request.getCategoryName());
        category.setDescription(request.getDescription());
        category.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        return categoryRepository.save(category);
    }

    // Get All Categories
    public List<Category> getAll() {
    	
        return categoryRepository.findAll();
    }

    // Get Category By ID
    public Category getById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() ->new RuntimeException("Category not found with id: " + id));
    }

    // Update Category
    public Category update(Long id, CategoryRequest request) {
    	
        Category category = getById(id);

        category.setCategoryName(request.getCategoryName());
        category.setDescription(request.getDescription());

        if (request.getIsActive() != null) {
            category.setIsActive(request.getIsActive());
        }

        return categoryRepository.save(category);
    }

    public String delete(Long id) {
        Category category = getById(id);
        categoryRepository.delete(category);
        return "Category deleted successfully with id : " + id;
    }
}