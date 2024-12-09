package com.example.beQuanTri.service.product;

import com.example.beQuanTri.dto.request.product.ProductCreationRequest;
import com.example.beQuanTri.dto.request.product.ProductUpdateRequest;
import com.example.beQuanTri.dto.response.product.ProductResponse;
import com.example.beQuanTri.dto.storage.ImageInfo;
import com.example.beQuanTri.entity.category.Category;
import com.example.beQuanTri.entity.product.Product;
import com.example.beQuanTri.exception.CustomException;
import com.example.beQuanTri.exception.ErrorCode;
import com.example.beQuanTri.mapper.product.ProductMapper;
import com.example.beQuanTri.repository.category.CategoryRepository;
import com.example.beQuanTri.repository.product.ProductRepository;
import com.example.beQuanTri.service.category.CategoryService;
import com.example.beQuanTri.service.image.ImageService;
import com.example.beQuanTri.service.role.RoleService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Service class for handling product-related operations.
 */
@Builder
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Service
public class ProductService {

    // Dependencies
    ProductRepository productRepository;
    ProductMapper productMapper;
    CategoryRepository categoryRepository;
    ImageService imageService;
    RoleService roleService;
    CategoryService categoryService;

    /**
     * Retrieves all products.
     *
     * @param pageable the pagination information
     * @return a page of product responses
     */
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        log.info("In Method getAllProducts");

        return productRepository
                .findAll(pageable)
                .map(this::wrapProductResponseByRole);
    }

    /**
     * Retrieves a product by its ID.
     *
     * @param id the ID of the product
     * @return the product response
     * @throws CustomException if the product is not found
     */
    @PreAuthorize("hasRole('Admin')")
    public ProductResponse getProductById(String id) {
        log.info("In Method getProductById");

        return productMapper.toProductResponse(findProductById(id));
    }

    /**
     * Creates a new product.
     *
     * @param productCreationRequest the request object containing product details
     * @return the created product response
     * @throws CustomException if the category is not found
     */
    @PreAuthorize("hasRole('Admin')")
    public ProductResponse createProduct(
            ProductCreationRequest productCreationRequest) {

        log.info("In Method createProduct");

        Category category = categoryRepository
                .findByName(productCreationRequest.getCategory())
                .orElseThrow(
                        () -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND)
                );

        Product product = productMapper.toProduct(productCreationRequest);

        product.setCategory(category);

        // Tạo mã sản phẩm trước khi lưu ảnh
        product = productRepository.save(product);

        MultipartFile image = productCreationRequest.getImage();
        if (image != null && !image.isEmpty()) {
            try {
                // Lưu ảnh và nhận thông tin đường dẫn
                ImageInfo imageInfo = imageService.saveProductImages(product.getProductCode(), image);

                // Đặt tên ảnh vào entity
                product.setOriginalImageName(imageInfo.getOriginalName());

                product = productRepository.save(product);
                log.info("Product images saved for productCode: {}", product.getProductCode());
            } catch (IOException e) {
                log.error("Error saving product images", e);
                throw new CustomException(ErrorCode.IMAGE_SAVE_FAILED);
            }
        }

        return productMapper.toProductResponse(product);
    }

    /**
     * Updates an existing product.
     *
     * @param id                  the ID of the product to update
     * @param productUpdateRequest the request object containing updated product details
     * @return the updated product response
     * @throws CustomException if the product is not found
     */
    @PreAuthorize("hasRole('Admin')")
    public ProductResponse updateProduct(
            String id,
            ProductUpdateRequest productUpdateRequest) {

        log.info("In Method updateProduct");

        Category category = categoryService.findCategoryByName(productUpdateRequest.getCategory());

        Product product = findProductById(id);

        productMapper.updateProduct(
                product,
                productUpdateRequest
        );

        product.setCategory(category);

        // Xử lý cập nhật ảnh nếu có
        MultipartFile image = productUpdateRequest.getImage();
        if (image != null && !image.isEmpty()) {
            try {
                // Xóa ảnh cũ nếu tồn tại
                if (product.getOriginalImageName() != null) {
                    imageService.deleteProductImages(product.getProductCode(),
                            new String[]{product.getOriginalImageName()});
                }

                // Lưu ảnh mới và nhận thông tin đường dẫn
                ImageInfo imageInfo = imageService.saveProductImages(product.getProductCode(), image);

                // Đặt tên ảnh mới vào entity
                product.setOriginalImageName(imageInfo.getOriginalName());

            } catch (IOException e) {
                log.error("Error updating product images", e);
                throw new CustomException(ErrorCode.IMAGE_SAVE_FAILED);
            }
        }

        product = productRepository.save(product);

        return productMapper.toProductResponse(product);
    }

    /**
     * Deletes a product by its ID.
     *
     * @param id the ID of the product to delete
     * @throws CustomException if the product is not found
     */
    @PreAuthorize("hasRole('Admin')")
    public void deleteProduct(String id) {
        log.info("In Method deleteProduct");

        if (!productRepository.existsById(id)) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        Product product = findProductById(id);

        if (product.getOriginalImageName() != null) {
            imageService.deleteProductImages(product.getProductCode(),
                    new String[]{product.getOriginalImageName()});
        }

        productRepository.deleteById(id);
    }

    /**
     * Deletes multiple products by their IDs.
     *
     * @param productIds a list of product IDs to delete
     * @throws CustomException if one or more products are not found
     */
    @PreAuthorize("hasRole('Admin')")
    public void deleteProducts(List<String> productIds) {
        log.info("In Method deleteProducts");

        List<Product> products = productRepository
                .findAllById(productIds);

        if (products.size() != productIds.size()) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        for (Product product : products) {
            if (product.getOriginalImageName() != null) {
                imageService.deleteProductImages(product.getProductCode(),
                        new String[]{product.getOriginalImageName()});
            }
        }

        productRepository.deleteAll(products);
    }

    /**
     * Retrieves a product by its product code.
     *
     * @param productCode the code of the product
     * @return the product response
     * @throws CustomException if the product is not found
     */
    public ProductResponse getProductByCode(String productCode) {
        log.info("In Method getProductByCode");

        Product product = productRepository
                .findByProductCode(productCode)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND)
                );

        return productMapper.toProductResponse(product);
    }

    /**
     * Finds a product by its ID.
     *
     * @param productId the ID of the product
     * @return the product entity
     * @throws CustomException if the product is not found
     */
    public Product findProductById(String productId) {
        return productRepository
                .findById(productId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND)
                );
    }

    /**
     * Finds a product by its product code.
     *
     * @param productCode the code of the product
     * @return the product entity
     * @throws CustomException if the product is not found
     */
    public Product findProductByCode(String productCode) {
        log.info("In Method findProductByCode");

        return productRepository
                .findByProductCode(productCode)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND)
                );
    }

    /**
     * Wraps a product response on the user's role.
     *
     * @param product the product to wrap
     * @return the wrapped product response tailored to the user's role
     */
    ProductResponse wrapProductResponseByRole(Product product) {
        String role = SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_Anonymous");

        return productMapper.toProductResponseByRole(product, role);
    }
}