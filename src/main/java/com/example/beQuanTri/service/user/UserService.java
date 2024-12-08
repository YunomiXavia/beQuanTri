package com.example.beQuanTri.service.user;

import com.example.beQuanTri.constant.PredefinedRole;
import com.example.beQuanTri.dto.request.user.UserCreationRequest;
import com.example.beQuanTri.dto.request.user.UserUpdateRequest;
import com.example.beQuanTri.dto.request.user.UserUpdateWithoutPasswordRequest;
import com.example.beQuanTri.dto.response.user.UserResponse;
import com.example.beQuanTri.entity.role.Role;
import com.example.beQuanTri.entity.user.AnonymousUser;
import com.example.beQuanTri.entity.user.User;
import com.example.beQuanTri.exception.CustomException;
import com.example.beQuanTri.exception.ErrorCode;
import com.example.beQuanTri.mapper.user.UserMapper;
import com.example.beQuanTri.repository.collaborator.CollaboratorRepository;
import com.example.beQuanTri.repository.user.AnonymousUserRepository;
import com.example.beQuanTri.repository.user.UserRepository;
import com.example.beQuanTri.service.role.RoleService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing user-related operations.
 */
@Builder
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Service
public class UserService {

    // Dependencies
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    UserMapper userMapper;
    RoleService roleService;
    CollaboratorRepository collaboratorRepository;
    AnonymousUserRepository anonymousUserRepository;

    /**
     * Retrieves all users.
     *
     * @param pageable the pagination information
     * @return the list of users
     */
    @PreAuthorize("hasRole('Admin')")
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        log.info("In method getAllUsers");

        String userRole = PredefinedRole.USER_ROLE;

        return userRepository
                .findByRole_RoleName(userRole, pageable)
                .map(userMapper::toUserResponse);
    }

    /**
     * Retrieves all admins.
     *
     * @param pageable the pagination information
     * @return the list of admins
     */
    @PreAuthorize("hasRole('Admin')")
    public Page<UserResponse> getAllAdmins(Pageable pageable) {
        log.info("In method getAllAdmins");

        String adminRole = PredefinedRole.ADMIN_ROLE;

        return userRepository
                .findByRole_RoleName(adminRole, pageable)
                .map(userMapper::toUserResponse);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user
     * @return the user response
     * @throws CustomException if the user is not found
     */
    @PreAuthorize("hasRole('Admin')")
    public UserResponse getUserById(String id) {
        log.info("In method getUserById");

        return userMapper.toUserResponse(
                findUserById(id)
        );
    }

    /**
     * Retrieves the information of the currently logged-in user.
     *
     * @return the user response
     */
    public UserResponse getMyInfo() {
        log.info("In method getMyInfo");

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = findUserByUsername(username);

        return userMapper.toUserResponse(user);
    }

    /**
     * Creates a new user.
     *
     * @param userCreationRequest the request object containing user details
     * @return the created user response
     * @throws CustomException if the user already exists
     */
    public UserResponse createUser(UserCreationRequest userCreationRequest) {
        log.info("In method createUser");

        User user = userMapper.toUser(userCreationRequest);

        user.setPassword(
                passwordEncoder.encode(userCreationRequest.getPassword())
        );

        Role userRole = roleService.getRoleByName(PredefinedRole.USER_ROLE);
        user.setRole(userRole);

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(ErrorCode.USER_EXISTED);
        }

        return userMapper.toUserResponse(user);
    }

    /**
     * Creates a new admin user.
     *
     * @param userCreationRequest the request object containing user details
     * @return the created user response
     * @throws CustomException if the user already exists
     */
    public UserResponse createAdmin(UserCreationRequest userCreationRequest) {
        log.info("In method createAdmin");

        User user = userMapper.toUser(userCreationRequest);

        user.setPassword(
                passwordEncoder.encode(userCreationRequest.getPassword())
        );

        Role userRole = roleService.getRoleByName(PredefinedRole.ADMIN_ROLE);
        user.setRole(userRole);

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(ErrorCode.USER_EXISTED);
        }

        return userMapper.toUserResponse(user);
    }

    /**
     * Updates an existing user's information.
     *
     * @param id               the ID of the user to update
     * @param userUpdateRequest the request object containing updated details
     * @return the updated user response
     * @throws CustomException if the user is not found
     */
    @PreAuthorize("hasRole('Admin')")
    public UserResponse updateUser(
            String id,
            UserUpdateRequest userUpdateRequest) {
        log.info("In method updateUser");

        User user = findUserById(id);

        userMapper.updateUser(
                user,
                userUpdateRequest
        );

        if (userUpdateRequest.getPassword() != null && !userUpdateRequest.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));
        }

        return userMapper.toUserResponse(
                userRepository.save(user)
        );
    }

    /**
     * Updates user information without modifying the password.
     *
     * @param id                            the ID of the user
     * @param userUpdateWithoutPasswordRequest the request object containing updated details
     * @return the updated user response
     */
    @PreAuthorize("hasRole('Admin')")
    public UserResponse updateUserWithoutPassword(
            String id,
            UserUpdateWithoutPasswordRequest userUpdateWithoutPasswordRequest) {
        log.info("In method updateUserWithoutPassword");

        User user = findUserById(id);

        userMapper.updateUserWithoutPassword(
                user,
                userUpdateWithoutPasswordRequest
        );

        return userMapper.toUserResponse(
                userRepository.save(user)
        );
    }

    /**
     * Updates the current user's information.
     * Only the authenticated user can update their own data.
     *
     * @param id               the ID of the user
     * @param userUpdateRequest the request object containing updated details
     * @return the updated user response
     */
    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse updateUserInfo(
            String id,
            UserUpdateRequest userUpdateRequest) {
        log.info("In method updateUserInfo");

        User user = findUserById(id);

        userMapper.updateUser(
                user,
                userUpdateRequest
        );

        user.setPassword(passwordEncoder.encode(
                userUpdateRequest.getPassword()
        ));

        return userMapper.toUserResponse(
                userRepository.save(user)
        );
    }

    /**
     * Updates the current user's information.
     * Only the authenticated user can update their own data.
     *
     * @param id                            the ID of the user
     * @param userUpdateWithoutPasswordRequest the request object containing updated details
     * @return the updated user response
     */
    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse updateUserInfoWithoutPassword(
            String id,
            UserUpdateWithoutPasswordRequest userUpdateWithoutPasswordRequest) {
        log.info("In method updateUserInfoWithoutPassword");

        User user = findUserById(id);

        userMapper.updateUserWithoutPassword(
                user,
                userUpdateWithoutPasswordRequest
        );

        return userMapper.toUserResponse(
                userRepository.save(user)
        );
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete
     * @throws CustomException if the user is not found
     */
    @Transactional
    @PreAuthorize("hasRole('Admin')")
    public void deleteUser(String id) {
        log.info("In method deleteUser");

        if (!userRepository.existsById(id)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        collaboratorRepository.deleteByUserId(id);
        userRepository.deleteById(id);
    }

    /**
     * Deletes multiple users by their IDs.
     *
     * @param ids the list of user IDs to delete
     * @throws CustomException if one or more users are not found
     */
    @Transactional
    @PreAuthorize("hasRole('Admin')")
    public void deleteUsers(List<String> ids) {
        log.info("In method deleteUsers");

        List<User> users = userRepository.findAllById(ids);

        if (users.size() != ids.size()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        for (String userId : ids) {
            collaboratorRepository.deleteByUserId(userId);
            userRepository.deleteById(userId);
        }
    }

    /**
     * Finds a user by their ID.
     *
     * @param userId the ID of the user
     * @return the User entity
     * @throws CustomException if the user is not found
     */
    public User findUserById(String userId) {
        log.info("In Method findUserById");

        return userRepository
                .findById(userId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_NOT_FOUND)
                );
    }

    /**
     * Finds a user by their username.
     *
     * @param username the username of the user
     * @return the User entity
     * @throws CustomException if the user is not found
     */
    public User findUserByUsername(String username) {
        log.info("In Method findUserByUsername");

        return userRepository
                .findByUsername(username)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_NOT_FOUND)
                );
    }

    /**
     * Finds a user by their email.
     *
     * @param email the email of the user
     * @return the User entity
     * @throws CustomException if the user is not found
     */
    public User findUserByEmail(String email) {
        log.info("In Method findUserByEmail");

        return userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_NOT_FOUND)
                );
    }

    /**
     * Finds or creates an anonymous user by name, email, and phone number.
     *
     * @param name        the name of the anonymous user
     * @param email       the email of the anonymous user
     * @param phoneNumber the phone number of the anonymous user
     * @return the AnonymousUser entity
     */
    public AnonymousUser findAndGetAnonymousUserByNameAndEmailAndPhoneNumber(
            String name,
            String email,
            String phoneNumber
    ) {
        log.info("In Method findAnonymousUserByNameAndEmailAndPhoneNumber");

        return anonymousUserRepository
                .findByNameAndEmailAndPhoneNumber(name, email, phoneNumber)
                .orElseGet(() -> {
                    AnonymousUser newAnonymousUser = AnonymousUser.builder()
                            .name(name)
                            .email(email)
                            .phoneNumber(phoneNumber)
                            .build();
                    return anonymousUserRepository.save(newAnonymousUser);
                });
    }
}