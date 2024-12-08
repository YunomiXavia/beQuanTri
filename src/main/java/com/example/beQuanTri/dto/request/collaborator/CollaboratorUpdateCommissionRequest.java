package com.example.beQuanTri.dto.request.collaborator;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CollaboratorUpdateCommissionRequest {
    @NotNull
    double commissionRate;
}
