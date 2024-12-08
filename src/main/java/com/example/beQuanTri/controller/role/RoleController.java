package com.example.beQuanTri.controller.role;

import com.example.beQuanTri.dto.request.role.RoleDeletionRequest;
import com.example.beQuanTri.dto.request.role.RoleRequest;
import com.example.beQuanTri.dto.response.ApiResponse;
import com.example.beQuanTri.dto.response.role.RoleResponse;
import com.example.beQuanTri.service.role.RoleService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing roles within the system.
 * Includes operations for creating, updating, deleting, and retrieving roles.
 */
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/admin")
public class RoleController {

    @Autowired
    RoleService roleService;

    /**
     * Retrieves all roles in the system.
     *
     * @return ApiResponse containing a list of all roles
     */
    @GetMapping("/roles")
    ApiResponse<List<RoleResponse>> getAllRoles() {
        return ApiResponse.<List<RoleResponse>>builder()
                .message("Get All Roles Successfully!")
                .result(
                        roleService.getAllRoles()
                )
                .build();
    }

    /**
     * Retrieves a specific role by its ID.
     *
     * @param roleId the ID of the role
     * @return ApiResponse containing the requested role
     */
    @GetMapping("/role/{roleId}")
    ApiResponse<RoleResponse> getRole(
            @PathVariable("roleId") String roleId) {
        return ApiResponse.<RoleResponse>builder()
                .message("Get Role Successfully!")
                .result(
                        roleService.getRole(roleId)
                )
                .build();
    }

    /**
     * Creates a new role in the system.
     *
     * @param roleRequest the request object containing role details
     * @return ApiResponse containing the created role
     */
    @PostMapping("/role")
    ApiResponse<RoleResponse> createRole(
            @RequestBody RoleRequest roleRequest) {
        return ApiResponse.<RoleResponse>builder()
                .message("Create Role Successfully!")
                .result(
                        roleService.createRole(roleRequest)
                )
                .build();
    }

    /**
     * Updates an existing role.
     *
     * @param roleId      the ID of the role to be updated
     * @param roleRequest the request object containing updated role details
     * @return ApiResponse containing the updated role
     */
    @PutMapping("/role/{roleId}")
    ApiResponse<RoleResponse> updateRole(
            @PathVariable("roleId") String roleId,
            @RequestBody RoleRequest roleRequest) {
        return ApiResponse.<RoleResponse>builder()
                .message("Update Role Successfully!")
                .result(
                        roleService.updateRole(roleId, roleRequest)
                )
                .build();
    }

    /**
     * Deletes a specific role by its ID.
     *
     * @param roleId the ID of the role to delete
     * @return ApiResponse indicating the success of the operation
     */
    @DeleteMapping("/role/{roleId}")
    ApiResponse<Void> deleteRole(
            @PathVariable("roleId") String roleId) {
        roleService.deleteRole(roleId);
        return ApiResponse.<Void>builder()
                .message("Delete Role Successfully!")
                .build();
    }

    /**
     * Deletes multiple roles by their IDs.
     *
     * @param roleDeletionRequest the request object containing a list of role IDs to delete
     * @return ApiResponse indicating the success of the operation
     */
    @DeleteMapping("/roles")
    ApiResponse<Void> deleteRoles(
            @RequestBody RoleDeletionRequest roleDeletionRequest) {
        roleService.deleteRoles(roleDeletionRequest.getRoleIds());
        return ApiResponse.<Void>builder()
                .message("Delete Roles Successfully!")
                .build();
    }
}
