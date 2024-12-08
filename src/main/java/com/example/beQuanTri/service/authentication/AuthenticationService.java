package com.example.beQuanTri.service.authentication;

import com.example.beQuanTri.configuration.jwt.JwtTokenUtil;
import com.example.beQuanTri.constant.PredefineTime;
import com.example.beQuanTri.constant.PredefinedRole;
import com.example.beQuanTri.constant.PredefinedUtilsEmail;
import com.example.beQuanTri.dto.request.authentication.ForgotPasswordRequest;
import com.example.beQuanTri.dto.request.authentication.RegisterRequest;
import com.example.beQuanTri.entity.role.Role;
import com.example.beQuanTri.entity.user.User;
import com.example.beQuanTri.exception.CustomException;
import com.example.beQuanTri.exception.ErrorCode;
import com.example.beQuanTri.repository.user.UserRepository;
import com.example.beQuanTri.service.role.RoleService;
import com.example.beQuanTri.service.user.UserService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Authentication Service - Handles all authentication-related operations.
 */
@Builder
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Service
public class AuthenticationService {

    // Dependencies
    UserRepository userRepository;
    RoleService roleService;
    JwtTokenUtil jwtTokenUtil;
    PasswordEncoder passwordEncoder;
    JavaMailSender javaMailSender;
    UserService userService;

    /**
     * Login function to authenticate user and generate a token.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return the generated JWT token
     * @throws CustomException if authentication fails
     * @throws JOSEException   if token generation fails
     */
    public String login(String username, String password)
            throws CustomException, JOSEException {
        log.info("In Method Login");
        User user = userService.findUserByUsername(username);

        if (passwordEncoder.matches(password, user.getPassword())) {
            return jwtTokenUtil.generateToken(
                    user.getUsername(),
                    user.getId(),
                    user.getRole().getRoleName(),
                    user.getEmail(),
                    PredefineTime.MILLISECONDS_IN_DAY
            );
        } else {
            throw new CustomException(ErrorCode.UNAUTHENTICATED);
        }
    }

    /**
     * Logout function to invalidate the token.
     *
     * @param token the JWT token to be invalidated
     * @throws ParseException if token parsing fails
     */
    public void logout(String token)
            throws ParseException {
        log.info("In Method Logout");
        if (!jwtTokenUtil.validateToken(token)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
        jwtTokenUtil.blacklistToken(token);
    }

    /**
     * Register a new user.
     *
     * @param registerRequest the registration request data
     * @return the generated JWT token for the new user
     * @throws JOSEException if token generation fails
     */
    public String register(RegisterRequest registerRequest)
            throws JOSEException {
        log.info("In Method register");
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new CustomException(ErrorCode.USER_EXISTED);
        }

        Role userRole = roleService.getRoleByName(PredefinedRole.USER_ROLE);

        User user = User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .role(userRole)
                .phoneNumber(registerRequest.getPhoneNumber())
                .build();

        userRepository.save(user);
        return jwtTokenUtil.generateToken(
                user.getUsername(),
                user.getId(),
                user.getRole().getRoleName(),
                user.getEmail(),
                PredefineTime.MILLISECONDS_IN_DAY
        );
    }

    /**
     * Refresh the token.
     *
     * @param token the old JWT token
     * @return the new refreshed JWT token
     * @throws ParseException if token parsing fails
     * @throws JOSEException  if token generation fails
     */
    public String refreshToken(String token)
            throws ParseException, JOSEException {
        log.info("In Method refreshToken");
        if (jwtTokenUtil.validateToken(token)) {
            log.info("Validate token Successfully");
            String username = jwtTokenUtil.getUsernameFromToken(token);
            String role = jwtTokenUtil.getRoleFromToken(token);
            String userId = jwtTokenUtil.getUserIdFromToken(token);
            String email = jwtTokenUtil.getEmailFromToken(token);
            return jwtTokenUtil.generateToken(
                    username,
                    userId,
                    role,
                    email,
                    PredefineTime.MILLISECONDS_IN_WEEK);
        } else {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    /**
     * Handle forgot password request by sending a reset token via email.
     *
     * @param forgotPasswordRequest the forgot password request data
     */
    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        log.info("In Method forgotPassword");
        User user = userService.findUserByEmail(forgotPasswordRequest.getEmail());

        // Generate Password Reset Token
        String resetToken = UUID.randomUUID().toString()
                .substring(0, PredefinedUtilsEmail.TOKEN_RESET_PASSWORD_LENGTH);
        user.setResetToken(resetToken);

        // Set token expiry time
        user.setTokenExpiryTime(
                LocalDateTime.now().plusMinutes(PredefinedUtilsEmail.EXPIRY_TIME)
        );
        userRepository.save(user);

        sendEmailWithForgotPasswordToken(user.getEmail(), resetToken);
    }

    /**
     * Send email with the forgot password token.
     *
     * @param email the recipient's email
     * @param token the reset token
     */
    void sendEmailWithForgotPasswordToken(String email, String token) {
        log.info("In Method sendEmailWithForgotPasswordToken");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Yêu cầu đặt lại mật khẩu !");
        message.setText("Mã xác thực yêu cầu đặt lại mật khẩu của bạn là: " + token + "." +
                "\nMã xác thực sẽ có hiệu lực trong " + PredefinedUtilsEmail.EXPIRY_TIME + " phút.");
        javaMailSender.send(message);
    }

    /**
     * Verify the forgot password token and reset password if valid.
     *
     * @param email the user's email
     * @param token the reset token
     * @return true if the token is valid and password is reset; false otherwise
     */
    public boolean verifyForgotPasswordToken(String email, String token) {
        log.info("In Method verifyForgotPasswordToken");
        User user = userService.findUserByEmail(email);

        if (token.equals(user.getResetToken())
                && LocalDateTime.now().isBefore(user.getTokenExpiryTime())) {
            resetPasswordAndSendEmail(user);
            return true;
        }
        return false;
    }

    /**
     * Reset password and send the new password via email.
     *
     * @param user the user whose password is to be reset
     */
    void resetPasswordAndSendEmail(User user) {
        log.info("In Method resetPasswordAndSendEmail");
        String resetPassword = UUID.randomUUID().toString().substring(0, 8);
        user.setPassword(passwordEncoder.encode(resetPassword));
        user.setResetToken(null);
        userRepository.save(user);

        SimpleMailMessage resetEmailMessage = new SimpleMailMessage();
        resetEmailMessage.setTo(user.getEmail());
        resetEmailMessage.setSubject("Yêu cầu đặt lại mật khẩu !");
        resetEmailMessage.setText("Mật khẩu của bạn sau khi reset là: " + resetPassword);

        javaMailSender.send(resetEmailMessage);
    }
}
