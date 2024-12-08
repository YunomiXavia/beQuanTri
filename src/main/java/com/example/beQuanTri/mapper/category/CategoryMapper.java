package com.example.beQuanTri.mapper.category;

import com.example.beQuanTri.dto.request.category.CategoryCreationRequest;
import com.example.beQuanTri.dto.request.category.CategoryUpdateRequest;
import com.example.beQuanTri.dto.response.category.CategoryResponse;
import com.example.beQuanTri.entity.category.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toCategory(CategoryCreationRequest categoryCreationRequest);
    CategoryResponse toResponse(Category category);
    void updateCategory(
            @MappingTarget Category category,
            CategoryUpdateRequest categoryUpdateRequest);
}