package com.example.beQuanTri.mapper.user;

import com.example.beQuanTri.dto.request.user.UserCreationRequest;
import com.example.beQuanTri.dto.request.user.UserUpdateRequest;
import com.example.beQuanTri.dto.request.user.UserUpdateWithoutPasswordRequest;
import com.example.beQuanTri.dto.response.user.UserResponse;
import com.example.beQuanTri.entity.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest userCreationRequest);

    @Mapping(target="role", source = "role.roleName")
    UserResponse toUserResponse(User user);

    void updateUser(
            @MappingTarget User user,
            UserUpdateRequest userUpdateRequest
    );

    void updateUserWithoutPassword(
            @MappingTarget User user,
            UserUpdateWithoutPasswordRequest userUpdateWithoutPasswordRequest
    );
}
