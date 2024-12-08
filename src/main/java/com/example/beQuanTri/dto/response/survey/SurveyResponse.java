package com.example.beQuanTri.dto.response.survey;

import com.example.beQuanTri.dto.basic.collaborator.CollaboratorSurveyBasicInfo;
import com.example.beQuanTri.dto.basic.user.UserBasicInfo;
import com.example.beQuanTri.entity.status.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SurveyResponse {
    String id;
    UserBasicInfo user;
    CollaboratorSurveyBasicInfo collaborator;
    Status status;
    String question;
    String response;
    Date createdAt;
    Date responseAt;
}
