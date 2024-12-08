package com.example.beQuanTri.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    // Other Exceptions
    UNCATEGORIZED_EXCEPTION(5000, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),

    // 500 Error
    CANNOT_REGISTER(5001, "Cannot generating token", HttpStatus.INTERNAL_SERVER_ERROR),
    CANNOT_UPDATE_COLLABORATOR(5002, "Cannot update collaborator", HttpStatus.INTERNAL_SERVER_ERROR),
    CANNOT_PARSING_TOKEN(5003, "Cannot parse token", HttpStatus.INTERNAL_SERVER_ERROR),
    REFRESH_TOKEN_FAILED(5004, "Refresh token failed", HttpStatus.INTERNAL_SERVER_ERROR),
    TOKEN_INVALID(5005, "Token is invalid", HttpStatus.INTERNAL_SERVER_ERROR),
    LOGOUT_FAILED(5006, "Logout failed", HttpStatus.INTERNAL_SERVER_ERROR),
    EMAIL_SEND_FAILED(5007, "Email send failed", HttpStatus.INTERNAL_SERVER_ERROR),
    IMAGE_SAVE_FAILED(5008, "Image save failed", HttpStatus.INTERNAL_SERVER_ERROR),
    SERVER_ERROR(5009, "Server error", HttpStatus.INTERNAL_SERVER_ERROR),

    // 400 Error
    INVALID_KEY(4001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN(4002, "Invalid Token", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL(4003, "Invalid email", HttpStatus.BAD_REQUEST),
    EMPTY_EMAIL(4004, "Email is empty", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_STOCK(4005, "Insufficient stock", HttpStatus.BAD_REQUEST),
    INVALID_COLLABORATOR(4006, "Invalid collaborator", HttpStatus.BAD_REQUEST),
    INVALID_QUANTITY(4007, "Invalid quantity", HttpStatus.BAD_REQUEST),
    INVALID_REFERRAL_CODE(4008, "Invalid referral code", HttpStatus.BAD_REQUEST),
    OUT_OF_STOCK(4009, "Out of stock", HttpStatus.BAD_REQUEST),
    CART_EMPTY(40010, "Cart is empty", HttpStatus.BAD_REQUEST),
    INVALID_STATUS(40011, "Invalid status", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(40020, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(40021, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    BIRTHDATE_INVALID(40022, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(40023, "Invalid email", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_FOUND_IN_CART(40024, "Product not found in cart", HttpStatus.BAD_REQUEST),
    COMMISSION_NOT_FOUND(40025, "Commission not found", HttpStatus.BAD_REQUEST),
    EMPTY_PHONE_NUMBER(40026, "Phone number is empty", HttpStatus.BAD_REQUEST),
    INVALID_PHONE_NUMBER(40027, "Phone number must be between {min} and {max} characters", HttpStatus.BAD_REQUEST),
    CANCEL_TIME_LIMIT_EXCEEDED(40028, "Cancel time limit exceeded", HttpStatus.BAD_REQUEST),
    INVALID_FIRST_NAME(40029, "First name must not be exceeded {max} characters", HttpStatus.BAD_REQUEST),
    INVALID_LAST_NAME(40030, "Last name must not be exceeded {max} characters", HttpStatus.BAD_REQUEST),
    IMAGE_NOT_FOUND(40031, "Image not found", HttpStatus.BAD_REQUEST),

    // 401 Error
    UNAUTHENTICATED(4011, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID_EXPIRED(4012, "Invalid token or expired.", HttpStatus.UNAUTHORIZED),

    // 403 Error
    UNAUTHORIZED(4031, "You do not have permission", HttpStatus.FORBIDDEN),

    // 404 Error
    USER_NOT_FOUND(4041, "User Not Found", HttpStatus.NOT_FOUND),
    COLLABORATOR_NOT_FOUND(4042, "Collaborator Not Found", HttpStatus.NOT_FOUND),
    ROLE_NOT_FOUND(4043, "Role Not Found", HttpStatus.NOT_FOUND),
    PRODUCT_NOT_FOUND(4044, "Product Not Found", HttpStatus.NOT_FOUND),
    ORDER_NOT_FOUND(4045, "Order Not Found", HttpStatus.NOT_FOUND),
    ORDER_ITEM_NOT_FOUND(4046, "Order Item Not Found", HttpStatus.NOT_FOUND),
    STATUS_NOT_FOUND(4047, "Status Not Found", HttpStatus.NOT_FOUND),
    SURVEY_FORM_NOT_FOUND(4048, "Survey Form Not Found", HttpStatus.NOT_FOUND),
    CART_NOT_FOUND(4049, "Cart Not Found", HttpStatus.NOT_FOUND),
    CART_ITEM_NOT_FOUND(40410, "Cart Item Not Found", HttpStatus.NOT_FOUND),
    NO_AVAILABLE_COLLABORATOR(40411, "No available collaborator", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND(40412, "No available category", HttpStatus.NOT_FOUND),

    // 409 Error
    USER_EXISTED(4091, "User existed", HttpStatus.CONFLICT),
    ROLE_EXISTED(4092, "Role Existed", HttpStatus.CONFLICT),
    CATEGORY_ALREADY_EXISTS(40012, "Category already exists", HttpStatus.CONFLICT),
    ;

    int code;
    String message;
    HttpStatus statusCode;
}
