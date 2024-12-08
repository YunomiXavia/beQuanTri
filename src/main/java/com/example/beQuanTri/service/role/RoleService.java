package com.example.beQuanTri.service.role;

import com.example.beQuanTri.dto.request.role.RoleRequest;
import com.example.beQuanTri.dto.response.role.RoleResponse;
import com.example.beQuanTri.entity.role.Role;
import com.example.beQuanTri.exception.CustomException;
import com.example.beQuanTri.exception.ErrorCode;
import com.example.beQuanTri.mapper.role.RoleMapper;
import com.example.beQuanTri.repository.role.RoleRepository;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for handling role-related operations, including creation,
 * updating, retrieval, and deletion of roles.
 */
@Builder
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Service
public class RoleService {

    // Dependencies
    RoleRepository roleRepository;
    RoleMapper roleMapper;

    /**
     * Retrieves all roles from the database.
     *
     * @return a list of role responses
     */
    @PreAuthorize("hasRole('Admin')")
    public List<RoleResponse> getAllRoles() {
        log.info("In Method getAllRoles");

        return roleRepository
                .findAll()
                .stream()
                .map(roleMapper::toRoleResponse)
                .toList();
    }

    /**
     * Retrieves a role by its ID.
     *
     * @param id the ID of the role
     * @return the role response
     * @throws CustomException if the role is not found
     */
    @PreAuthorize("hasRole('Admin')")
    public RoleResponse getRole(String id) {
        log.info("In Method getRole");

        Role role = findRoleById(id);

        return roleMapper.toRoleResponse(role);
    }

    /**
     * Creates a new role.
     *
     * @param roleRequest the request object containing role details
     * @return the created role response
     * @throws CustomException if the role already exists
     */
    @PreAuthorize("hasRole('Admin')")
    public RoleResponse createRole(RoleRequest roleRequest) {
        log.info("In Method createRole");

        Role role = roleMapper.toRole(roleRequest);

        try {
            role = roleRepository.save(role);
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(ErrorCode.ROLE_EXISTED);
        }

        return roleMapper.toRoleResponse(role);
    }

    /**
     * Updates an existing role.
     *
     * @param id          the ID of the role to update
     * @param roleRequest the request object containing updated role details
     * @return the updated role response
     * @throws CustomException if the role is not found
     */
    @PreAuthorize("hasRole('Admin')")
    public RoleResponse updateRole(
            String id,
            RoleRequest roleRequest) {
        log.info("In Method updateRole");

        Role role = findRoleById(id);

        roleMapper.UpdateRole(
                role,
                roleRequest
        );

        role.setRoleName(roleRequest.getRoleName());

        return roleMapper.toRoleResponse(
                roleRepository.save(role)
        );
    }

    /**
     * Deletes a role by its ID.
     *
     * @param id the ID of the role to delete
     * @throws CustomException if the role is not found
     */
    @PreAuthorize("hasRole('Admin')")
    public void deleteRole(String id) {
        log.info("In Method deleteRole");

        if (!roleRepository.existsById(id)) {
            throw new CustomException(ErrorCode.ROLE_NOT_FOUND);
        }

        roleRepository.deleteById(id);
    }

    /**
     * Deletes multiple roles by their IDs.
     *
     * @param roleIds a list of role IDs to delete
     * @throws CustomException if one or more roles are not found
     */
    @PreAuthorize("hasRole('Admin')")
    public void deleteRoles(List<String> roleIds) {
        log.info("In Method deleteRoles");

        List<Role> roles = roleRepository
                .findAllById(roleIds);

        if (roles.size() != roleIds.size()) {
            throw new CustomException(ErrorCode.ROLE_NOT_FOUND);
        }

        for (String roleId : roleIds) {
            roleRepository.deleteById(roleId);
        }
    }

    /**
     * Finds a role by its ID.
     *
     * @param id the ID of the role
     * @return the role entity
     * @throws CustomException if the role is not found
     */
    public Role findRoleById(String id) {
        log.info("In Method findRoleById");

        return roleRepository
                .findById(id)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.ROLE_NOT_FOUND)
                );
    }

    /**
     * Finds a role by its name.
     *
     * @param roleName the name of the role
     * @return the role entity
     * @throws CustomException if the role is not found
     */
    public Role getRoleByName(String roleName) {
        log.info("In Method getRoleByName");

        return roleRepository
                .findByRoleName(roleName)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.ROLE_NOT_FOUND)
                );
    }
}