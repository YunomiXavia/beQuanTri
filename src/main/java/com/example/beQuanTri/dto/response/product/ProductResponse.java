package com.example.beQuanTri.dto.response.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse {
    String id;
    String productName;
    double price;
    String description;
    int stock;
    int subscriptionDuration;
    String category;
    String productCode;
    String originalImageName;
}