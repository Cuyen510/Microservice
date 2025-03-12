package com.productservice.service.CategoryService;

import com.productservice.dto.CategoryDTO;
import com.productservice.model.Category;

import java.util.List;

public interface ICategoryService {
    Category createCategory(CategoryDTO categoryDTO);

    Category getCategoryById(Long id);

    List<Category> getAllCategories();

    Category updateCategory(Long id, CategoryDTO categoryDTO);

    void deleteCategory(Long id);
}
