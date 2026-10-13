package com.campusfix.service;

import com.campusfix.exception.DuplicateResourceException;
import com.campusfix.exception.ResourceNotFoundException;
import com.campusfix.model.Category;
import com.campusfix.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Cacheable(value = "categoriesList")
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        log.info("Fetching all categories from database (cache miss)");
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    }

    @CacheEvict(value = "categoriesList", allEntries = true)
    @Transactional
    public Category createCategory(Category category) {
        if (categoryRepository.findByNameIgnoreCase(category.getName()).isPresent()) {
            throw new DuplicateResourceException("Category already exists with name: " + category.getName());
        }
        return categoryRepository.save(category);
    }

    @CacheEvict(value = "categoriesList", allEntries = true)
    @Transactional
    public Category updateCategory(Long id, Category updatedData) {
        Category category = getCategoryById(id);
        category.setName(updatedData.getName());
        category.setDescription(updatedData.getDescription());
        category.setSlaHours(updatedData.getSlaHours());
        category.setIcon(updatedData.getIcon());
        category.setKeywords(updatedData.getKeywords());
        return categoryRepository.save(category);
    }

    @CacheEvict(value = "categoriesList", allEntries = true)
    @Transactional
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }
}
