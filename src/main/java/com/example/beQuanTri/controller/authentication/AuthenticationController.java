package com.example.beQuanTri.controller.authentication;

import com.example.beQuanTri.dto.request.authentication.ForgotPasswordRequest;
import com.example.beQuanTri.dto.request.authentication.LoginRequest;
import com.example.beQuanTri.dto.request.authentication.RegisterRequest;
import com.example.beQuanTri.dto.request.authentication.TokenRequest;
import com.example.beQuanTri.dto.response.ApiResponse;
import com.example.beQuanTri.exception.ErrorCode;
import com.example.beQuanTri.service.authentication.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

/**
 * Controller class for handling authentication-related operations,
 * including login, logout, registration, and password management.
 */
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    AuthenticationService authenticationService;

    /**
     * Handles user login and returns a JWT token if successful.
     *
     * @param loginRequest the login request containing username and password
     * @return the API response containing the JWT token
     */
    @PostMapping("/login")
    ApiResponse<String> login(@RequestBody LoginRequest loginRequest) {
        try {
            String token = authenticationService
                    .login(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    );
            return ApiResponse.<String>builder()
                    .message("Login Successfully!")
                    .result(token)
                    .build();
        } catch (JOSEException e) {
            return ApiResponse.<String>builder()
                    .code(ErrorCode.UNAUTHENTICATED.getCode())
                    .message(ErrorCode.UNAUTHENTICATED.getMessage())
                    .build();
        }
    }

    /**
     * Handles user logout by invalidating the provided token.
     *
     * @param tokenRequest the request containing the JWT token
     * @return the API response confirming logout
     */
    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody TokenRequest tokenRequest) {
        try {
            authenticationService.logout(tokenRequest.getToken());
            return ApiResponse.<Void>builder()
                    .message("Logout Successfully!")
                    .build();
        } catch (ParseException e) {
            return ApiResponse.<Void>builder()
                    .code(ErrorCode.CANNOT_PARSING_TOKEN.getCode())
                    .message(ErrorCode.CANNOT_PARSING_TOKEN.getMessage())
                    .build();
        }
    }

    /**
     * Handles user registration and returns a JWT token if successful.
     *
     * @param registerRequest the registration request containing user details
     * @return the API response containing the JWT token
     */
    @PostMapping("/register")
    ApiResponse<String> register(
            @RequestBody @Valid RegisterRequest registerRequest) {
        try {
            String token = authenticationService.register(registerRequest);
            return ApiResponse.<String>builder()
                    .message("Register Successfully!")
                    .result(token)
                    .build();
        } catch (JOSEException e) {
            return ApiResponse.<String>builder()
                    .code(ErrorCode.CANNOT_REGISTER.getCode())
                    .message(ErrorCode.CANNOT_REGISTER.getMessage())
                    .build();
        }
    }

    /**
     * Initiates the password reset process by sending a reset token to the user's email.
     *
     * @param forgotPasswordRequest the request containing the user's email
     * @return the API response confirming the reset process
     */
    @PostMapping("/forgot-password")
    ApiResponse<Void> forgotPassword(
            @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        authenticationService.forgotPassword(forgotPasswordRequest);
        return ApiResponse.<Void>builder()
                .message("Reset Password Successfully! Your Token Has Been Sent To Your Email!")
                .build();
    }

    /**
     * Verifies the reset token and updates the user's password if valid.
     *
     * @param email the user's email
     * @param token the reset token
     * @return the API response confirming the reset
     */
    @PostMapping("/forgot-password/verify-token")
    public ApiResponse<Void> forgotPasswordVerifyToken(
            @RequestParam String email,
            @RequestParam String token) {
        boolean success = authenticationService
                .verifyForgotPasswordToken(email, token);
        if (success) {
            return ApiResponse.<Void>builder()
                    .message("Reset Password Successfully And Sent To Your Email!")
                    .build();
        }
        return ApiResponse.<Void>builder()
                .code(ErrorCode.TOKEN_INVALID_EXPIRED.getCode())
                .message(ErrorCode.TOKEN_INVALID_EXPIRED.getMessage())
                .build();
    }

    /**
     * Refreshes an expired JWT token and returns a new one.
     *
     * @param tokenRequest the request containing the expired token
     * @return the API response containing the new JWT token
     */
    @PostMapping("/refresh-token")
    ApiResponse<String> refreshToken(
            @RequestBody TokenRequest tokenRequest) {
        try {
            String newToken = authenticationService.refreshToken(
                    tokenRequest.getToken()
            );
            return ApiResponse.<String>builder()
                    .message("Refresh Successfully!")
                    .result(newToken)
                    .build();
        } catch (JOSEException | ParseException e) {
            return ApiResponse.<String>builder()
                    .code(ErrorCode.REFRESH_TOKEN_FAILED.getCode())
                    .message(ErrorCode.REFRESH_TOKEN_FAILED.getMessage())
                    .build();
        }
    }
}
