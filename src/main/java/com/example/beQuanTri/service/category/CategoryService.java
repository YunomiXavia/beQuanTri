package com.example.beQuanTri.service.category;

import com.example.beQuanTri.dto.request.category.CategoryCreationRequest;
import com.example.beQuanTri.dto.request.category.CategoryUpdateRequest;
import com.example.beQuanTri.dto.response.category.CategoryResponse;
import com.example.beQuanTri.entity.category.Category;
import com.example.beQuanTri.exception.CustomException;
import com.example.beQuanTri.exception.ErrorCode;
import com.example.beQuanTri.mapper.category.CategoryMapper;
import com.example.beQuanTri.repository.category.CategoryRepository;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service to manage category-related operations, including CRUD operations.
 */
@Builder
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Service
public class CategoryService {

    // Dependencies
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;

    /**
     * Retrieves all categories from the database.
     *
     * @return a list of category responses
     */
    @PreAuthorize("hasRole('Admin')")
    public List<CategoryResponse> getAllCategories() {
        log.info("In Method getAllCategories");
        return categoryRepository
                .findAll()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    /**
     * Retrieves a category by its ID.
     *
     * @param id the ID of the category
     * @return the category response
     * @throws CustomException if the category is not found
     */
    @PreAuthorize("hasRole('Admin')")
    public CategoryResponse getCategoryById(String id) {
        log.info("In Method getCategoryById");
        Category category = findCategoryById(id);
        return categoryMapper
                .toResponse(category);
    }

    /**
     * Creates a new category in the database.
     *
     * @param categoryCreationRequest the request containing category creation details
     * @return the created category response
     * @throws CustomException if a category with the same name already exists
     */
    @PreAuthorize("hasRole('Admin')")
    public CategoryResponse createCategory(
            CategoryCreationRequest categoryCreationRequest) {
        log.info("In Method createCategory");

        if (categoryRepository.existsByName(
                categoryCreationRequest.getName())) {
            throw new CustomException(
                    ErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        Category category = categoryMapper
                .toCategory(categoryCreationRequest);

        return categoryMapper
                .toResponse(categoryRepository.save(category));
    }

    /**
     * Updates an existing category.
     *
     * @param id                    the ID of the category to update
     * @param categoryUpdateRequest the request containing category update details
     * @return the updated category response
     * @throws CustomException if the category name already exists for another category
     */
    @PreAuthorize("hasRole('Admin')")
    public CategoryResponse updateCategory(
            String id,
            CategoryUpdateRequest categoryUpdateRequest) {
        log.info("In Method updateCategory");

        categoryRepository.findByName(
                        categoryUpdateRequest.getName())
                .ifPresent(existingCategory -> {
                    if (!existingCategory
                            .getId()
                            .equals(id)) {
                        throw new CustomException(
                                ErrorCode.CATEGORY_ALREADY_EXISTS);
                    }
                });

        Category category = findCategoryById(id);

        categoryMapper.updateCategory(
                category,
                categoryUpdateRequest);

        return categoryMapper
                .toResponse(categoryRepository.save(category));
    }

    /**
     * Deletes a category by its ID.
     *
     * @param id the ID of the category to delete
     * @throws CustomException if the category is not found
     */
    @PreAuthorize("hasRole('Admin')")
    public void deleteCategory(String id) {
        log.info("In Method deleteCategory");
        Category category = findCategoryById(id);

        categoryRepository
                .delete(category);
    }

    /**
     * Finds a category by its ID.
     *
     * @param id the ID of the category
     * @return the category entity
     * @throws CustomException if the category is not found
     */
    public Category findCategoryById(String id) {
        log.info("In Method findCategoryById");

        return categoryRepository
                .findById(id)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.CATEGORY_NOT_FOUND
                ));
    }

    /**
     * Finds a category by its name.
     *
     * @param name the name of the category
     * @return the category entity
     * @throws CustomException if the category is not found
     */
    public Category findCategoryByName(String name) {
        log.info("In Method findCategoryByName");

        return categoryRepository
                .findByName(name)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.CATEGORY_NOT_FOUND
                ));
    }
}