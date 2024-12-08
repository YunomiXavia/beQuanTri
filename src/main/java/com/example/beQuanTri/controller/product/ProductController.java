package com.example.beQuanTri.controller.product;

import com.example.beQuanTri.dto.request.product.ProductCreationRequest;
import com.example.beQuanTri.dto.request.product.ProductDeletionRequest;
import com.example.beQuanTri.dto.request.product.ProductUpdateRequest;
import com.example.beQuanTri.dto.response.ApiResponse;
import com.example.beQuanTri.dto.response.PaginatedResponse;
import com.example.beQuanTri.dto.response.product.ProductResponse;
import com.example.beQuanTri.service.product.ProductService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


/**
 * Controller for managing products.
 * Includes operations to retrieve, create, update, and delete products.
 */
@Builder
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/admin")
public class ProductController {

    ProductService productService;

    /**
     * Retrieves all products with pagination.
     *
     * @param page the page number
     * @param size the number of items per page
     * @return ApiResponse containing a paginated list of products
     */
    @GetMapping("/products")
    ApiResponse<PaginatedResponse<Page<ProductResponse>>> getAllProducts(
            @RequestParam(defaultValue ="0") int page,
            @RequestParam(defaultValue ="5") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponse> productPage = productService.getAllProducts(pageable);

        PaginatedResponse<Page<ProductResponse>> paginatedResponse = PaginatedResponse
                .<Page<ProductResponse>>builder()
                .message("Get Products with Pagination Successfully")
                .data(productPage)
                .currentPage(productPage.getNumber())
                .totalPages(productPage.getTotalPages())
                .totalElements(productPage.getTotalElements())
                .last(productPage.isLast())
                .build();

        return ApiResponse.<PaginatedResponse<Page<ProductResponse>>>builder()
                .message("Get All Products Successfully!")
                .result(paginatedResponse)
                .build();
    }

    /**
     * Retrieves details of a product by its ID.
     *
     * @param productId the ID of the product
     * @return ApiResponse containing the ProductResponse object
     */
    @GetMapping("/product/{productId}")
    ApiResponse<ProductResponse> getProductById(
            @PathVariable("productId") String productId) {
        return ApiResponse.<ProductResponse>builder()
                .message("Get Product Successfully!")
                .result(
                        productService.getProductById(productId)
                )
                .build();
    }

    /**
     * Creates a new product.
     *
     * @param productCreationRequest the request object containing product details
     * @param image the image file uploaded
     * @return ApiResponse containing the created ProductResponse object
     */
    @PostMapping(value = "/product", consumes = {"multipart/form-data"})
    ApiResponse<ProductResponse> createProduct(
            @ModelAttribute ProductCreationRequest productCreationRequest,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        productCreationRequest.setImage(image);
        return ApiResponse.<ProductResponse>builder()
                .message("Create Product Successfully!")
                .result(
                        productService.createProduct(productCreationRequest)
                )
                .build();
    }

    /**
     * Updates the details of an existing product.
     *
     * @param productId             the ID of the product to update
     * @param productUpdateRequest  the request object containing updated product details
     * @param image the image file uploaded (optional)
     * @return ApiResponse containing the updated ProductResponse object
     */
    @PutMapping(value = "/product/{productId}", consumes = {"multipart/form-data"})
    ApiResponse<ProductResponse> updateProduct(
            @PathVariable("productId") String productId,
            @ModelAttribute ProductUpdateRequest productUpdateRequest,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        productUpdateRequest.setImage(image);
        return ApiResponse.<ProductResponse>builder()
                .message("Update Product Successfully!")
                .result(
                        productService.updateProduct(
                                productId,
                                productUpdateRequest
                        )
                )
                .build();
    }

    /**
     * Deletes a product by its ID.
     *
     * @param productId the ID of the product to delete
     * @return ApiResponse indicating the result of the operation
     */
    @DeleteMapping("/product/{productId}")
    ApiResponse<Void> deleteProduct(
            @PathVariable("productId") String productId) {
        productService.deleteProduct(productId);
        return ApiResponse.<Void>builder()
                .message("Delete Product Successfully!")
                .build();
    }

    /**
     * Deletes multiple products by their IDs.
     *
     * @param productDeletionRequest the request object containing a list of product IDs to delete
     * @return ApiResponse indicating the result of the operation
     */
    @DeleteMapping("/products")
    ApiResponse<Void> deleteProducts(
            @RequestBody ProductDeletionRequest productDeletionRequest) {
        productService.deleteProducts(
                productDeletionRequest.getProductIds()
        );
        return ApiResponse.<Void>builder()
                .message("Delete Products Successfully!")
                .build();
    }

    /**
     * Retrieves a product by its unique product code.
     *
     * @param productCode the unique code of the product
     * @return ApiResponse containing the ProductResponse object
     */
    @GetMapping("/product/code/{productCode}")
    ApiResponse<ProductResponse> getProductByCode(
            @PathVariable("productCode") String productCode) {
        return ApiResponse.<ProductResponse>builder()
                .message("Get Product Successfully!")
                .result(
                        productService.getProductByCode(productCode)
                )
                .build();
    }
}
