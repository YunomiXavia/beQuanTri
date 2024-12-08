package com.example.beQuanTri.dto.basic.collaborator;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CollaboratorSurveyBasicInfo {
    String id;
    String username;
    int totalSurveyHandled;
}
