package com.example.beQuanTri.dto.response.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    String id;
    String username;
    String email;
    String phoneNumber;
    String role;
    Date dateJoined;
    double totalSpent;
    String lastName;
    String firstName;
    LocalDate birthDate;
}
