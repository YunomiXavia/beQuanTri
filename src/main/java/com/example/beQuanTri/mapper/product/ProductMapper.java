package com.example.beQuanTri.mapper.product;

import com.example.beQuanTri.dto.request.product.ProductCreationRequest;
import com.example.beQuanTri.dto.request.product.ProductUpdateRequest;
import com.example.beQuanTri.dto.response.product.ProductResponse;
import com.example.beQuanTri.entity.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "category.name", source = "category")
    Product toProduct(ProductCreationRequest productCreationRequest);

    @Mapping(target = "category", source = "category.name")
    ProductResponse toProductResponse(Product product);

    void updateProduct(
            @MappingTarget Product product,
            ProductUpdateRequest productUpdateRequest);

    @Mapping(target = "category", source = "category.name")
    default ProductResponse toProductResponseByRole(Product product, String role) {
        ProductResponse response = toProductResponse(product);

        if("ROLE_User".equalsIgnoreCase(role) || "ROLE_Anonymous".equalsIgnoreCase(role)) {
            response.setId(null);
        }
        return response;
    }
}
