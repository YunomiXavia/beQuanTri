package com.example.beQuanTri.dto.request.user;

import com.example.beQuanTri.validator.BirthDateConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateWithoutPasswordRequest {
    @Email(message = "EMAIL_INVALID")
    @Size(min = 6, message = "INVALID_EMAIL")
    @NotEmpty(message = "EMPTY_EMAIL")
    String email;

    @Size(min = 10, max = 15, message = "INVALID_PHONE_NUMBER")
    @NotEmpty(message = "EMPTY_PHONE_NUMBER")
    String phoneNumber;

    @Size(max = 50, message = "INVALID_LAST_NAME")
    String lastName;

    @Size(max = 50, message = "INVALID_FIRST_NAME")
    String firstName;

    @BirthDateConstraint(min = 10, message = "BIRTHDATE_INVALID")
    LocalDate birthDate;
}
