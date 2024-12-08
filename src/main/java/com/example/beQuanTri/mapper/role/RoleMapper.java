package com.example.beQuanTri.mapper.role;

import com.example.beQuanTri.dto.request.role.RoleRequest;
import com.example.beQuanTri.dto.response.role.RoleResponse;
import com.example.beQuanTri.entity.role.Role;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    Role toRole(RoleRequest roleRequest);

    RoleResponse toRoleResponse(Role role);

    void UpdateRole(
            @MappingTarget Role role,
            RoleRequest roleRequest
    );
}
