package com.example.beQuanTri.dto.request.authentication;

import com.example.beQuanTri.validator.BirthDateConstraint;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest {
    @Size(min = 4, message = "USERNAME_INVALID")
    String username;

    @Size(min = 8, message = "PASSWORD_INVALID")
    String password;

    @Email(message = "EMAIL_INVALID", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    @Size(min = 6, message = "INVALID_EMAIL")
    @NotEmpty(message = "EMPTY_EMAIL")
    String email;

    @Size(min = 10, max = 15, message = "INVALID_PHONE_NUMBER")
    @NotEmpty(message = "EMPTY_PHONE_NUMBER")
    String phoneNumber;

    String lastName;
    String firstName;

    @BirthDateConstraint(min = 10, message = "BIRTHDATE_INVALID")
    LocalDate birthDate;
}