package com.example.beQuanTri.controller.survey;

import com.example.beQuanTri.dto.request.survey.SurveyRequest;
import com.example.beQuanTri.dto.response.ApiResponse;
import com.example.beQuanTri.dto.response.PaginatedResponse;
import com.example.beQuanTri.dto.response.survey.SurveyResponse;
import com.example.beQuanTri.dto.response.user.UserResponse;
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
 * Controller for handling survey-related operations, including submitting surveys
 * and retrieving user-specific surveys.
 */
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequiredArgsConstructor
public class SurveyController {

    SurveyService surveyService;

    /**
     * Submits a survey for a specific user.
     *
     * @param userId       the ID of the user submitting the survey
     * @param surveyRequest the request object containing survey details
     * @return ApiResponse containing the submitted survey response
     */
    @PostMapping("/survey/{userId}")
    ApiResponse<SurveyResponse> submitSurvey(
            @PathVariable String userId,
            @RequestBody SurveyRequest surveyRequest) {
        return ApiResponse.<SurveyResponse>builder()
                .message("Submit Survey Form Successfully!")
                .result(
                        surveyService.submitSurvey(
                                userId,
                                surveyRequest
                        )
                )
                .build();
    }

    /**
     * Retrieves all surveys for a specific user.
     *
     * @param userId the ID of the user to retrieve surveys for
     * @return ApiResponse containing the paginated list of surveys
     */
    @GetMapping("/surveys/{userId}")
    ApiResponse<PaginatedResponse<Page<SurveyResponse>>> getUserSurveys(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @PathVariable("userId") String userId) {

        Pageable pageable = PageRequest.of(page, size);
        Page<SurveyResponse> surveyPage = surveyService
                .getUserSurveys(userId, pageable);

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
                .message("Get User Surveys Successfully!")
                .result(paginatedResponse)
                .build();
    }
}
