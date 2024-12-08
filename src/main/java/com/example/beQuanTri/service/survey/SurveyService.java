package com.example.beQuanTri.service.survey;

import com.example.beQuanTri.constant.PredefineStatus;
import com.example.beQuanTri.dto.request.survey.SurveyRequest;
import com.example.beQuanTri.dto.request.survey.SurveyResponseRequest;
import com.example.beQuanTri.dto.response.survey.SurveyResponse;
import com.example.beQuanTri.entity.collaborator.Collaborator;
import com.example.beQuanTri.entity.status.Status;
import com.example.beQuanTri.entity.survey.Survey;
import com.example.beQuanTri.entity.user.User;
import com.example.beQuanTri.exception.CustomException;
import com.example.beQuanTri.exception.ErrorCode;
import com.example.beQuanTri.mapper.survey.SurveyMapper;
import com.example.beQuanTri.repository.collaborator.CollaboratorRepository;
import com.example.beQuanTri.repository.survey.SurveyRepository;
import com.example.beQuanTri.service.collaborator.CollaboratorService;
import com.example.beQuanTri.service.status.StatusService;
import com.example.beQuanTri.service.user.UserService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Service class for handling survey-related operations, including submission,
 * assignment, and response handling.
 */
@Builder
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Service
public class SurveyService {

    // Dependencies
    SurveyRepository surveyRepository;
    SurveyMapper surveyMapper;
    StatusService statusService;
    UserService userService;
    CollaboratorService collaboratorService;
    CollaboratorRepository collaboratorRepository;

    /**
     * Submits a new survey by a user.
     *
     * @param userId        the ID of the user submitting the survey
     * @param surveyRequest the survey request data
     * @return the created survey response
     */
    public SurveyResponse submitSurvey(
            String userId,
            SurveyRequest surveyRequest) {
        log.info("In Method submitSurvey");

        User user = userService.findUserById(userId);

        Survey survey = surveyMapper.toSurvey(surveyRequest);

        survey.setUser(user);

        Status openStatus = statusService
                .findByStatusName(PredefineStatus.OPEN);

        survey.setStatus(openStatus);

        survey = surveyRepository.save(survey);

        return wrapSurveyResponseByRole(survey);
    }

    /**
     * Retrieves all surveys submitted by a specific user.
     *
     * @param userId   the ID of the user
     * @param pageable the pagination data
     * @return a page of survey responses
     */
    public Page<SurveyResponse> getUserSurveys(String userId, Pageable pageable) {
        log.info("In Method getUserSurveys");

        User user = userService.findUserById(userId);

        return surveyRepository
                .findByUser(user, pageable)
                .map(this::wrapSurveyResponseByRole);
    }

    @PreAuthorize("hasRole('Collaborator') or hasRole('Admin')")
    public Page<SurveyResponse> getAllSurveys(Pageable pageable) {
        log.info("In Method getAllSurveys");

        return surveyRepository
                .findAll(pageable)
                .map(this::wrapSurveyResponseByRole);
    }

    /**
     * Assigns a survey to a collaborator for handling.
     *
     * @param collaboratorId the ID of the collaborator
     * @param surveyId       the ID of the survey
     * @return the updated survey response
     */
    @PreAuthorize("hasRole('Collaborator') or hasRole('Admin')")
    public SurveyResponse handleSurvey(
            String collaboratorId,
            String surveyId) {
        log.info("In Method handleSurvey");

        Collaborator collaborator = collaboratorService
                .findCollaboratorById(collaboratorId);

        Survey survey = findSurveyById(surveyId);

        Status inProgressStatus = statusService
                .findByStatusName(PredefineStatus.IN_PROGRESS);

        survey.setCollaborator(collaborator);
        survey.setStatus(inProgressStatus);

        return wrapSurveyResponseByRole(surveyRepository.save(survey));
    }

    /**
     * Assigns multiple surveys to a collaborator for handling.
     *
     * @param collaboratorId the ID of the collaborator
     * @param surveyIds      the list of survey IDs to handle
     * @return a list of updated survey responses
     */
    @PreAuthorize("hasRole('Collaborator') or hasRole('Admin')")
    public List<SurveyResponse> handleSurveys(
            String collaboratorId,
            List<String> surveyIds) {
        log.info("In Method handleSurveys");

        Collaborator collaborator = collaboratorService
                .findCollaboratorById(collaboratorId);

        List<Survey> surveys = surveyRepository
                .findAllById(surveyIds);

        Status inProgressStatus = statusService
                .findByStatusName(PredefineStatus.IN_PROGRESS);

        if (surveys.size() != surveyIds.size()) {
            throw new CustomException(ErrorCode.SURVEY_FORM_NOT_FOUND);
        }

        for (Survey survey : surveys) {
            survey.setCollaborator(collaborator);
            survey.setStatus(inProgressStatus);
        }

        return surveyRepository
                .saveAll(surveys)
                .stream()
                .map(this::wrapSurveyResponseByRole)
                .toList();
    }

    /**
     * Submits a response to a survey.
     *
     * @param surveyId             the ID of the survey
     * @param surveyResponseRequest the response data
     * @param collaboratorId       the ID of the collaborator handling the survey
     * @return the updated survey response
     */
    @PreAuthorize("hasRole('Collaborator') or hasRole('Admin')")
    public SurveyResponse responseSurvey(
            String surveyId,
            SurveyResponseRequest surveyResponseRequest,
            String collaboratorId) {
        log.info("In Method responseSurvey");

        Survey survey = findSurveyById(surveyId);

        Collaborator collaborator = collaboratorService
                .findCollaboratorById(collaboratorId);

        survey.setCollaborator(collaborator);
        survey.setResponse(surveyResponseRequest.getResponse());
        survey.setResponseAt(new Date());

        return surveyMapper.toSurveyResponse(
                surveyRepository.save(survey)
        );
    }

    /**
     * Updates the status of a survey to "Complete".
     *
     * @param surveyId the ID of the survey
     * @return the updated survey response
     */
    @PreAuthorize("hasRole('Collaborator') or hasRole('Admin')")
    public SurveyResponse updateSurveyStatusComplete(String surveyId) {
        log.info("In Method updateSurveyStatusComplete");

        Survey survey = findSurveyById(surveyId);

        Status completeStatus = statusService
                .findByStatusName(PredefineStatus.COMPLETE);

        Collaborator collaborator = survey.getCollaborator();
        collaborator.setTotalOrdersHandled(collaborator.getTotalOrdersHandled() + 1);
        collaboratorRepository.save(collaborator);

        survey.setStatus(completeStatus);

        return surveyMapper.toSurveyResponse(
                surveyRepository.save(survey)
        );
    }

    /**
     * Finds a survey by its ID.
     *
     * @param surveyId the ID of the survey
     * @return the Survey entity
     * @throws CustomException if the survey is not found
     */
    public Survey findSurveyById(String surveyId) {
        log.info("In Method findSurveyById");

        return surveyRepository
                .findById(surveyId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.SURVEY_FORM_NOT_FOUND)
                );
    }

    private SurveyResponse wrapSurveyResponseByRole(Survey survey) {
        String role = SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_Anonymous");

        return surveyMapper.toSurveyResponseByRole(survey, role);
    }
}