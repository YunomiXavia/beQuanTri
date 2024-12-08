package com.example.beQuanTri.controller.user;

import com.example.beQuanTri.dto.request.user.UserUpdateRequest;
import com.example.beQuanTri.dto.request.user.UserUpdateWithoutPasswordRequest;
import com.example.beQuanTri.dto.response.ApiResponse;
import com.example.beQuanTri.dto.response.PaginatedResponse;
import com.example.beQuanTri.dto.response.collaborator.CollaboratorResponse;
import com.example.beQuanTri.dto.response.product.ProductResponse;
import com.example.beQuanTri.dto.response.user.UserResponse;
import com.example.beQuanTri.service.collaborator.CollaboratorService;
import com.example.beQuanTri.service.product.ProductService;
import com.example.beQuanTri.service.user.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling user-related operations, including retrieving
 * user information, updating user profiles, and managing collaborators by user ID.
 */
@Builder
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
public class UserController {

    UserService userService;

    CollaboratorService collaboratorService;
    ProductService productService;

    /**
     * Retrieves the information of the currently logged-in user.
     *
     * @return ApiResponse containing the user information
     */
    @GetMapping("/my-info")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .message("Get My Info Successfully")
                .result(
                        userService.getMyInfo()
                )
                .build();
    }

    /**
     * Updates the information of a specific user.
     *
     * @param userId            the ID of the user to be updated
     * @param userUpdateRequest the request object containing updated user details
     * @return ApiResponse containing the updated user information
     */
    @PutMapping("/update-info/{userId}")
    ApiResponse<UserResponse> updateUserInfo(
            @PathVariable("userId") String userId,
            @RequestBody @Valid UserUpdateRequest userUpdateRequest) {

        return ApiResponse.<UserResponse>builder()
                .message("Update User Info Successfully")
                .result(
                        userService.updateUserInfo(
                                userId,
                                userUpdateRequest
                        )
                )
                .build();
    }

    /**
     * Updates user information without modifying the password.
     *
     * @param userId  the ID of the user to be updated
     * @param request the request object containing updated user details (excluding password)
     * @return ApiResponse containing the updated user information
     */
    @PutMapping("/update-info/{userId}/without-password")
    ApiResponse<UserResponse> updateUserInfoWithoutPassword(
            @PathVariable("userId") String userId,
            @RequestBody @Valid UserUpdateWithoutPasswordRequest request) {

        return ApiResponse.<UserResponse>builder()
                .message("Update User Info Successfully")
                .result(
                        userService.updateUserInfoWithoutPassword(
                                userId,
                                request
                        )
                )
                .build();
    }

    /**
     * Retrieves the collaborator information associated with a specific user.
     *
     * @param userId the ID of the user whose collaborator information is to be retrieved
     * @return ApiResponse containing the collaborator details
     */
    @GetMapping("/collaborator/by-user/{userId}")
    ApiResponse<CollaboratorResponse> getCollaboratorByUserId(
            @PathVariable("userId") String userId) {

        return ApiResponse.<CollaboratorResponse>builder()
                .message("Get Collaborator By User ID Successfully!")
                .result(
                        collaboratorService.getCollaboratorByUserId(userId)
                )
                .build();
    }

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
}