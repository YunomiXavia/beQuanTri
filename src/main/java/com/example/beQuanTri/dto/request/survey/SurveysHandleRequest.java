package com.example.beQuanTri.dto.request.survey;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SurveysHandleRequest {
    List<String> surveyIds;
}