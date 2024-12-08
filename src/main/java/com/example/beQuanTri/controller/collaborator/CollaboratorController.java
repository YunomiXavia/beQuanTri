package com.example.beQuanTri.controller.collaborator;

import com.example.beQuanTri.dto.request.survey.SurveyResponseRequest;
import com.example.beQuanTri.dto.request.survey.SurveysHandleRequest;
import com.example.beQuanTri.dto.response.ApiResponse;
import com.example.beQuanTri.dto.response.PaginatedResponse;
import com.example.beQuanTri.dto.response.order.OrderResponse;
import com.example.beQuanTri.dto.response.survey.SurveyResponse;
import com.example.beQuanTri.entity.order.Order;
import com.example.beQuanTri.service.collaborator.CollaboratorService;
import com.example.beQuanTri.service.order.OrderService;
import com.example.beQuanTri.service.survey.SurveyService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for collaborator-related operations,
 * including managing commissions, surveys, and orders.
 */
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequiredArgsConstructor
@RequestMapping("/collaborator")
public class CollaboratorController {

    CollaboratorService collaboratorService;
    SurveyService surveyService;
    OrderService orderService;

    /**
     * Responds to a survey by a collaborator.
     *
     * @param surveyId              the ID of the survey
     * @param collaboratorId        the ID of the collaborator
     * @param surveyResponseRequest the survey response details
     * @return the updated survey response
     */
    @PostMapping("/survey/{surveyId}/{collaboratorId}")
    ApiResponse<SurveyResponse> responseSurvey(
            @PathVariable("surveyId") String surveyId,
            @PathVariable("collaboratorId") String collaboratorId,
            @RequestBody SurveyResponseRequest surveyResponseRequest) {
        return ApiResponse.<SurveyResponse>builder()
                .message("Response Survey Successfully!")
                .result(
                        surveyService.responseSurvey(
                                surveyId,
                                surveyResponseRequest,
                                collaboratorId
                        )
                )
                .build();
    }

    /**
     * Updates the status of a survey to complete after handling.
     *
     * @param surveyId the ID of the survey
     * @return the updated survey response
     */
    @PutMapping("/survey/{surveyId}/complete")
    ApiResponse<SurveyResponse> updateSurveyStatusComplete(
            @PathVariable("surveyId") String surveyId) {
        return ApiResponse.<SurveyResponse>builder()
                .message("Update Survey Status Successfully!")
                .result(
                        surveyService.updateSurveyStatusComplete(surveyId)
                )
                .build();
    }

    /**
     * Retrieves all surveys available for handling.
     *
     * @return a list of survey responses
     */
    @GetMapping("/surveys")
    ApiResponse<PaginatedResponse<Page<SurveyResponse>>> getSurveys(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<SurveyResponse> surveyPage = surveyService
                .getAllSurveys(pageable);

        PaginatedResponse<Page<SurveyResponse>> paginatedResponse = PaginatedResponse
                .<Page<SurveyResponse>>builder()
                .message("Get Surveys with Pagination Successfully")
                .data(surveyPage)
                .currentPage(surveyPage.getNumber())
                .totalPages(surveyPage.getTotalPages())
                .totalElements(surveyPage.getTotalElements())
                .last(surveyPage.isLast())
                .build();

        return ApiResponse.<PaginatedResponse<Page<SurveyResponse>>>builder()
                .message("Get All Surveys Successfully!")
                .result(paginatedResponse)
                .build();
    }

    /**
     * Handles a single survey by a collaborator.
     *
     * @param collaboratorId the ID of the collaborator
     * @param surveyId       the ID of the survey
     * @return the handled survey response
     */
    @PutMapping("/survey/{collaboratorId}/{surveyId}")
    ApiResponse<SurveyResponse> handleSurvey(
            @PathVariable("collaboratorId") String collaboratorId,
            @PathVariable("surveyId") String surveyId) {
        return ApiResponse.<SurveyResponse>builder()
                .message("Handle Survey Successfully!")
                .result(
                        surveyService.handleSurvey(
                                collaboratorId,
                                surveyId
                        )
                )
                .build();
    }

    /**
     * Handles multiple surveys by a collaborator.
     *
     * @param collaboratorId        the ID of the collaborator
     * @param surveysHandleRequest  the request containing survey IDs to handle
     * @return the handled survey responses
     */
    @PutMapping("/surveys/{collaboratorId}")
    ApiResponse<List<SurveyResponse>> handleSurveys(
            @PathVariable("collaboratorId") String collaboratorId,
            @RequestBody SurveysHandleRequest surveysHandleRequest) {
        return ApiResponse.<List<SurveyResponse>>builder()
                .message("Handle Surveys Successfully!")
                .result(
                        surveyService.handleSurveys(
                                collaboratorId,
                                surveysHandleRequest.getSurveyIds()
                        )
                )
                .build();
    }
}
