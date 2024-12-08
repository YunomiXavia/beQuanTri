package com.example.beQuanTri.controller.admin;

import com.example.beQuanTri.dto.request.collaborator.CollaboratorCreationRequest;
import com.example.beQuanTri.dto.request.collaborator.CollaboratorUpdateCommissionRequest;
import com.example.beQuanTri.dto.request.user.UserCreationRequest;
import com.example.beQuanTri.dto.request.user.UserDeletionRequest;
import com.example.beQuanTri.dto.request.user.UserUpdateRequest;
import com.example.beQuanTri.dto.request.user.UserUpdateWithoutPasswordRequest;
import com.example.beQuanTri.dto.response.ApiResponse;
import com.example.beQuanTri.dto.response.PaginatedResponse;
import com.example.beQuanTri.dto.response.collaborator.CollaboratorResponse;
import com.example.beQuanTri.dto.response.user.UserResponse;
import com.example.beQuanTri.service.collaborator.CollaboratorService;
import com.example.beQuanTri.service.user.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for admin-related operations,
 * including managing users and collaborators.
 */
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/admin")
public class AdminController {

    UserService userService;
    CollaboratorService collaboratorService;

    /**
     * Retrieves all users in the system.
     *
     * @param page the page number
     * @param size the number of users per page
     * @return a page of user responses
     */
    @GetMapping("/users")
    ApiResponse<PaginatedResponse<Page<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<UserResponse> userPage = userService.getAllUsers(pageable);

        PaginatedResponse<Page<UserResponse>> paginatedResponse = PaginatedResponse.<Page<UserResponse>>builder()
                .message("Get Users with Pagination Successfully")
                .data(userPage)
                .currentPage(userPage.getNumber())
                .totalPages(userPage.getTotalPages())
                .totalElements(userPage.getTotalElements())
                .last(userPage.isLast())
                .build();

        return ApiResponse.<PaginatedResponse<Page<UserResponse>>>builder()
                .message("Get All Users Successfully")
                .result(paginatedResponse)
                .build();
    }

    /**
     * Retrieves all admins in the system.
     *
     * @param page the page number
     * @param size the number of admins per page
     * @return a page of admin responses
     */
    @GetMapping("/admins")
    ApiResponse<PaginatedResponse<Page<UserResponse>>> getAllAdmins(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<UserResponse> adminPage = userService.getAllAdmins(pageable);

        PaginatedResponse<Page<UserResponse>> paginatedResponse = PaginatedResponse
                .<Page<UserResponse>>builder()
                .message("Get Users with Pagination Successfully")
                .data(adminPage)
                .currentPage(adminPage.getNumber())
                .totalPages(adminPage.getTotalPages())
                .totalElements(adminPage.getTotalElements())
                .last(adminPage.isLast())
                .build();

        return ApiResponse.<PaginatedResponse<Page<UserResponse>>>builder()
                .message("Get All Admins Successfully")
                .result(paginatedResponse)
                .build();
    }

    /**
     * Creates a new user.
     *
     * @param userCreationRequest the request containing user details
     * @return the created user response
     */
    @PostMapping("/user")
    ApiResponse<UserResponse> createUser(
            @RequestBody @Valid UserCreationRequest userCreationRequest) {

        return ApiResponse.<UserResponse>builder()
                .message("Create User Successfully")
                .result(userService.createUser(userCreationRequest))
                .build();
    }

    /**
     * Creates a new admin.
     *
     * @param userCreationRequest the request containing admin details
     * @return the created admin response
     */
    @PostMapping("/admin")
    ApiResponse<UserResponse> createAdmin(
            @RequestBody @Valid UserCreationRequest userCreationRequest){

        return ApiResponse.<UserResponse>builder()
                .message("Create User Successfully")
                .result(userService.createAdmin(userCreationRequest))
                .build();
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param userId the ID of the user
     * @return the user response
     */
    @GetMapping("/user/{userId}")
    ApiResponse<UserResponse> getUser(
            @PathVariable("userId") String userId) {

        return ApiResponse.<UserResponse>builder()
                .message("Get User Successfully")
                .result(userService.getUserById(userId))
                .build();
    }

    /**
     * Retrieves the currently logged-in admin's information.
     *
     * @return the user response of the admin
     */
    @GetMapping("/my-info")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .message("Get My Info Successfully")
                .result(userService.getMyInfo())
                .build();
    }

    /**
     * Updates an existing user's information.
     *
     * @param userId            the ID of the user
     * @param userUpdateRequest the updated details of the user
     * @return the updated user response
     */
    @PutMapping("/user/{userId}")
    ApiResponse<UserResponse> updateUser(
            @PathVariable("userId") String userId,
            @RequestBody @Valid UserUpdateRequest userUpdateRequest) {

        return ApiResponse.<UserResponse>builder()
                .message("Update User Successfully")
                .result(userService.updateUser(userId, userUpdateRequest))
                .build();
    }

    /**
     * Updates an existing user's information, excluding their password.
     *
     * @param userId the ID of the user
     * @param request the updated details of the user (without password)
     * @return the updated user response
     */
    @PutMapping("/user/{userId}/without-password")
    ApiResponse<UserResponse> updateUserWithoutPassword(
            @PathVariable("userId") String userId,
            @RequestBody @Valid UserUpdateWithoutPasswordRequest request) {

        return ApiResponse.<UserResponse>builder()
                .message("Update User Successfully")
                .result(userService.updateUserWithoutPassword(userId, request))
                .build();
    }

    /**
     * Deletes a user by their ID.
     *
     * @param userId the ID of the user
     * @return an empty API response
     */
    @DeleteMapping("/user/{userId}")
    ApiResponse<Void> deleteUser(
            @PathVariable("userId") String userId) {

        userService.deleteUser(userId);

        return ApiResponse.<Void>builder()
                .message("Delete User Successfully")
                .build();
    }

    /**
     * Deletes multiple users by their IDs.
     *
     * @param userDeletionRequest the request containing the list of user IDs
     * @return an empty API response
     */
    @DeleteMapping("/users")
    ApiResponse<Void> deleteUsers(
            @RequestBody UserDeletionRequest userDeletionRequest) {

        userService.deleteUsers(userDeletionRequest.getUserIds());

        return ApiResponse.<Void>builder()
                .message("Delete Users Successfully!")
                .build();
    }

    /**
     * Retrieves all collaborators in the system.
     *
     * @param page the page number
     * @param size the number of collaborators per page
     * @return a page of collaborator responses
     */
    @GetMapping("/collaborators")
    ApiResponse<PaginatedResponse<Page<CollaboratorResponse>>> getAllCollaborators(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CollaboratorResponse> collaboratorPage = collaboratorService
                .getAllCollaborators(pageable);

        PaginatedResponse<Page<CollaboratorResponse>> paginatedResponse = PaginatedResponse
                .<Page<CollaboratorResponse>>builder()
                .message("Get Collaborators with Pagination Successfully")
                .data(collaboratorPage)
                .currentPage(collaboratorPage.getNumber())
                .totalPages(collaboratorPage.getTotalPages())
                .totalElements(collaboratorPage.getTotalElements())
                .last(collaboratorPage.isLast())
                .build();

        return ApiResponse.<PaginatedResponse<Page<CollaboratorResponse>>>builder()
                .message("Get All Collaborators Successfully")
                .result(paginatedResponse)
                .build();
    }

    /**
     * Creates a new collaborator.
     *
     * @param collaboratorCreationRequest the request containing collaborator details
     * @return the created collaborator response
     */
    @PostMapping("/collaborator")
    ApiResponse<CollaboratorResponse> createCollaborator(
            @RequestBody @Valid CollaboratorCreationRequest collaboratorCreationRequest) {

        return ApiResponse.<CollaboratorResponse>builder()
                .message("Create Collaborator Successfully")
                .result(collaboratorService.createCollaborator(collaboratorCreationRequest))
                .build();
    }

    /**
     * Retrieves a collaborator by their ID.
     *
     * @param collaboratorId the ID of the collaborator
     * @return the collaborator response
     */
    @GetMapping("/collaborator/{collaboratorId}")
    ApiResponse<CollaboratorResponse> getCollaborator(
            @PathVariable("collaboratorId") String collaboratorId) {

        return ApiResponse.<CollaboratorResponse>builder()
                .message("Get Collaborator Successfully")
                .result(collaboratorService.getCollaboratorById(collaboratorId))
                .build();
    }

    /**
     * Updates the commission rate of a collaborator.
     *
     * @param collaboratorId the ID of the collaborator
     * @param collaboratorUpdateCommissionRequest the updated commission details
     * @return the updated collaborator response
     */
    @PutMapping("/collaborator/commission-rate/{collaboratorId}")
    ApiResponse<CollaboratorResponse> updateCommissionRateCollaborator(
            @PathVariable("collaboratorId") String collaboratorId,
            @RequestBody CollaboratorUpdateCommissionRequest collaboratorUpdateCommissionRequest) {

        return ApiResponse.<CollaboratorResponse>builder()
                .message("Update Collaborator Successfully")
                .result(
                        collaboratorService.updateCommissionRateCollaborator(
                                collaboratorId,
                                collaboratorUpdateCommissionRequest
                        )
                )
                .build();
    }
}