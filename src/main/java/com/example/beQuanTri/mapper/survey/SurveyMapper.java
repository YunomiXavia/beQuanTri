package com.example.beQuanTri.mapper.survey;

import com.example.beQuanTri.dto.basic.collaborator.CollaboratorSurveyBasicInfo;
import com.example.beQuanTri.dto.basic.user.UserBasicInfo;
import com.example.beQuanTri.dto.request.survey.SurveyRequest;
import com.example.beQuanTri.dto.response.survey.SurveyResponse;
import com.example.beQuanTri.entity.collaborator.Collaborator;
import com.example.beQuanTri.entity.survey.Survey;
import com.example.beQuanTri.entity.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SurveyMapper {
    //@Mapping(target = "user.id", source = "userId")
    Survey toSurvey(SurveyRequest surveyRequest);

    @Mapping(target = "user", source = "user")
    @Mapping(target = "collaborator", source = "collaborator")
    @Mapping(target = "collaborator.username", source = "collaborator.user.username")
    @Mapping(target = "createdAt", source = "createdAt")
    SurveyResponse toSurveyResponse(Survey survey);

    default SurveyResponse toSurveyResponseByRole(
            Survey survey,
            String role) {
        SurveyResponse response = toSurveyResponse(survey);

        if("ROLE_Collaborator".equalsIgnoreCase(role)) {
            if(response.getUser() != null) {
                response.getUser().setId(null);
            }
            if(response.getCollaborator() != null) {
                response.getCollaborator().setId(null);
            }
            if(response.getStatus() != null){
                response.getStatus().setId(null);
            }
        }

        if("ROLE_User".equalsIgnoreCase(role)) {
            if(response.getUser() != null) {
                response.getUser().setId(null);
            }
            if(response.getCollaborator() != null) {
                response.getCollaborator().setId(null);
            }
            if(response.getStatus() != null){
                response.getStatus().setId(null);
            }
        }

        return response;
    }

    default UserBasicInfo toUserBasicInfo(User user) {
        if (user == null) return null;
        return new UserBasicInfo(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber());
    }

    default CollaboratorSurveyBasicInfo toCollaboratorSurveyBasicInfo(
            Collaborator collaborator) {
        if (collaborator == null) return null;
        return new CollaboratorSurveyBasicInfo(
                collaborator.getId(),
                collaborator.getUser().getUsername(),
                collaborator.getTotalSurveyHandled());
    }

}
