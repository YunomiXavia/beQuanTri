package com.example.beQuanTri.controller.category;

import com.example.beQuanTri.dto.request.category.CategoryCreationRequest;
import com.example.beQuanTri.dto.request.category.CategoryUpdateRequest;
import com.example.beQuanTri.dto.response.ApiResponse;
import com.example.beQuanTri.dto.response.category.CategoryResponse;
import com.example.beQuanTri.service.category.CategoryService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for managing categories,
 * including CRUD operations for categories.
 */
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/admin")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    /**
     * Retrieves all categories in the system.
     *
     * @return a list of category responses
     */
    @GetMapping("/categories")
    ApiResponse<List<CategoryResponse>> getAllCategories() {
        return ApiResponse.<List<CategoryResponse>>builder()
                .message("Get All Categories Successfully!")
                .result(
                        categoryService.getAllCategories()
                )
                .build();
    }

    /**
     * Retrieves a category by its ID.
     *
     * @param categoryId the ID of the category
     * @return the category response
     */
    @GetMapping("/category/{categoryId}")
    ApiResponse<CategoryResponse> getCategoryById(
            @PathVariable("categoryId") String categoryId) {
        return ApiResponse.<CategoryResponse>builder()
                .message("Get Category By Id Successfully!")
                .result(
                        categoryService.getCategoryById(categoryId)
                )
                .build();
    }

    /**
     * Creates a new category.
     *
     * @param categoryCreationRequest the request containing category details
     * @return the created category response
     */
    @PostMapping("/category")
    ApiResponse<CategoryResponse> createCategory(
            @RequestBody CategoryCreationRequest categoryCreationRequest) {
        return ApiResponse.<CategoryResponse>builder()
                .message("Create Category Successfully!")
                .result(
                        categoryService.createCategory(categoryCreationRequest)
                )
                .build();
    }

    /**
     * Updates an existing category by its ID.
     *
     * @param categoryId             the ID of the category to update
     * @param categoryUpdateRequest  the request containing updated category details
     * @return the updated category response
     */
    @PutMapping("/category/{categoryId}")
    ApiResponse<CategoryResponse> updateCategory(
            @PathVariable("categoryId") String categoryId,
            @RequestBody CategoryUpdateRequest categoryUpdateRequest) {
        return ApiResponse.<CategoryResponse>builder()
                .message("Update Category Successfully!")
                .result(
                        categoryService.updateCategory(
                                categoryId,
                                categoryUpdateRequest
                        )
                )
                .build();
    }

    /**
     * Deletes a category by its ID.
     *
     * @param categoryId the ID of the category to delete
     * @return an empty API response confirming deletion
     */
    @DeleteMapping("/category/{categoryId}")
    ApiResponse<Void> deleteCategory(
            @PathVariable("categoryId") String categoryId) {
        categoryService.deleteCategory(categoryId);
        return ApiResponse.<Void>builder()
                .message("Delete Category Successfully!")
                .build();
    }
}
