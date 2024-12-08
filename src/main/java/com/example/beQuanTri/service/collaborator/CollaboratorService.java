package com.example.beQuanTri.service.collaborator;

import com.example.beQuanTri.constant.PredefinedRole;
import com.example.beQuanTri.dto.request.collaborator.CollaboratorCreationRequest;
import com.example.beQuanTri.dto.request.collaborator.CollaboratorUpdateCommissionRequest;
import com.example.beQuanTri.dto.response.collaborator.CollaboratorResponse;
import com.example.beQuanTri.entity.collaborator.Collaborator;
import com.example.beQuanTri.entity.role.Role;
import com.example.beQuanTri.entity.user.User;
import com.example.beQuanTri.exception.CustomException;
import com.example.beQuanTri.exception.ErrorCode;
import com.example.beQuanTri.mapper.collaborator.CollaboratorMapper;
import com.example.beQuanTri.mapper.user.UserMapper;
import com.example.beQuanTri.repository.collaborator.CollaboratorRepository;
import com.example.beQuanTri.repository.user.UserRepository;
import com.example.beQuanTri.service.role.RoleService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for managing collaborator-related operations, such as creating,
 * updating commission rates, and retrieving collaborator details.
 */
@Builder
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Service
public class CollaboratorService {

    // Dependencies
    CollaboratorRepository collaboratorRepository;
    UserRepository userRepository;
    CollaboratorMapper collaboratorMapper;
    UserMapper userMapper;
    RoleService roleService;
    PasswordEncoder passwordEncoder;

    /**
     * Retrieves all collaborators.
     *
     * @param pageable the pagination information
     * @return the page of collaborator responses
     */
    @PreAuthorize("hasRole('Admin')")
    public Page<CollaboratorResponse> getAllCollaborators(Pageable pageable) {
        log.info("In Method getAllCollaborators");
        return collaboratorRepository
                .findAll(pageable)
                .map(collaboratorMapper::toCollaboratorResponse);
    }

    /**
     * Retrieves a collaborator by their ID.
     *
     * @param id the collaborator's ID
     * @return the collaborator response
     * @throws CustomException if the collaborator is not found
     */
    @PreAuthorize("hasRole('Admin')")
    public CollaboratorResponse getCollaboratorById(String id) {
        log.info("In Method getCollaboratorById");

        Collaborator collaborator = findCollaboratorById(id);

        return collaboratorMapper
                .toCollaboratorResponse(collaborator);
    }

    /**
     * Creates a new collaborator.
     *
     * @param collaboratorCreationRequest the request containing collaborator details
     * @return the created collaborator response
     * @throws CustomException if the user already exists
     */
    @PreAuthorize("hasRole('Admin')")
    public CollaboratorResponse createCollaborator(
            CollaboratorCreationRequest collaboratorCreationRequest) {
        log.info("In Method createCollaborator");

        // Create User
        User user = userMapper
                .toUser(collaboratorCreationRequest.getUser());

        user.setPassword(
                passwordEncoder.encode(
                        collaboratorCreationRequest
                                .getUser()
                                .getPassword()
                )
        );

        // Assign Collaborator Role
        Role collaboratorRole = roleService
                .getRoleByName(PredefinedRole.COLLABORATOR_ROLE);

        user.setRole(collaboratorRole);

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(
                    ErrorCode.USER_EXISTED
            );
        }

        // Create Collaborator
        Collaborator collaborator = Collaborator.builder()
                .user(user)
                .commissionRate(
                        collaboratorCreationRequest
                                .getCommissionRate()
                )
                .build();

        collaborator = collaboratorRepository.save(collaborator);

        return collaboratorMapper
                .toCollaboratorResponse(collaborator);
    }

    /**
     * Updates the commission rate for a collaborator.
     *
     * @param id the collaborator's ID
     * @param collaboratorUpdateCommissionRequest the request containing the new commission rate
     * @return the updated collaborator response
     * @throws CustomException if the update fails
     */
    @PreAuthorize("hasRole('Admin')")
    public CollaboratorResponse updateCommissionRateCollaborator(
            String id,
            CollaboratorUpdateCommissionRequest collaboratorUpdateCommissionRequest) {
        log.info("In Method updateCommissionRateCollaborator");

        Collaborator collaborator = findCollaboratorById(id);

        collaboratorMapper.updateCollaborator(
                collaborator,
                collaboratorUpdateCommissionRequest
        );

        try {
            collaborator = collaboratorRepository.save(collaborator);
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
            throw new CustomException(
                    ErrorCode.CANNOT_UPDATE_COLLABORATOR
            );
        }

        return collaboratorMapper
                .toCollaboratorResponse(collaborator);
    }

    /**
     * Retrieves a collaborator by their user ID.
     *
     * @param userId the user's ID
     * @return the collaborator response
     * @throws CustomException if the collaborator is not found
     */
    @PreAuthorize("hasRole('Admin') or hasRole('Collaborator')")
    public CollaboratorResponse getCollaboratorByUserId(String userId) {
        log.info("In Method getCollaboratorByUserId");

        Collaborator collaborator = findCollaboratorByUserId(userId);

        return collaboratorMapper
                .toCollaboratorResponse(collaborator);
    }

    /**
     * Finds a collaborator by their ID.
     *
     * @param collaboratorId the collaborator's ID
     * @return the collaborator entity
     * @throws CustomException if the collaborator is not found
     */
    public Collaborator findCollaboratorById(String collaboratorId) {
        log.info("In Method findCollaboratorById");

        return collaboratorRepository
                .findById(collaboratorId)
                .orElseThrow(
                        () -> new CustomException(
                                ErrorCode.COLLABORATOR_NOT_FOUND
                        )
                );
    }

    /**
     * Finds a collaborator by their user ID.
     *
     * @param userId the user's ID
     * @return the collaborator entity
     * @throws CustomException if the collaborator is not found
     */
    public Collaborator findCollaboratorByUserId(String userId) {
        return collaboratorRepository
                .findByUser_Id(userId)
                .orElseThrow(
                        () -> new CustomException(
                                ErrorCode.COLLABORATOR_NOT_FOUND
                        )
                );
    }

    /**
     * Finds a collaborator by their referral code.
     *
     * @param referralCode the referral code
     * @return the collaborator entity
     * @throws CustomException if the referral code is invalid
     */
    public Collaborator findCollaboratorByReferralCode(String referralCode) {
        return collaboratorRepository
                .findByReferralCode(referralCode)
                .orElseThrow(
                        () -> new CustomException(
                                ErrorCode.INVALID_REFERRAL_CODE
                        )
                );
    }
}
