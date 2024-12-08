package com.example.beQuanTri.dto.request.product;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductUpdateRequest {
    String productName;
    double price;
    String description;
    int stock;
    int subscriptionDuration;
    String categoryName;
    MultipartFile image;
}