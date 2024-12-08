package com.example.beQuanTri.dto.response.collaborator;

import com.example.beQuanTri.dto.response.user.UserResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CollaboratorResponse {
    String id;

    UserResponse user;

    String referralCode;
    double commissionRate;
    int totalOrdersHandled = 0;
    int totalSurveyHandled = 0;
    double totalCommissionEarned = 0.0;
}
