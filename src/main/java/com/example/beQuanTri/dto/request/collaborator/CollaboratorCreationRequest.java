package com.example.beQuanTri.dto.request.collaborator;

import com.example.beQuanTri.dto.request.user.UserCreationRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CollaboratorCreationRequest {
    @NotNull @Valid
    UserCreationRequest user;

    @NotNull
    double commissionRate;
}
