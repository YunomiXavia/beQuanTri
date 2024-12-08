package com.example.beQuanTri.mapper.collaborator;

import com.example.beQuanTri.dto.request.collaborator.CollaboratorUpdateCommissionRequest;
import com.example.beQuanTri.dto.response.collaborator.CollaboratorResponse;
import com.example.beQuanTri.entity.collaborator.Collaborator;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CollaboratorMapper {

    // Mapping User to Response
    @Mapping(source = "user", target = "user")
    @Mapping(source = "user.role.roleName", target = "user.role")
    CollaboratorResponse toCollaboratorResponse(
            Collaborator collaborator
    );

    // Mapping Update Collaborator Commission Rate
    void updateCollaborator(
            @MappingTarget Collaborator collaborator,
            CollaboratorUpdateCommissionRequest collaboratorUpdateCommissionRequest
    );
}
