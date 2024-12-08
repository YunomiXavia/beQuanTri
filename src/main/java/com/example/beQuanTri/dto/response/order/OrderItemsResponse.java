package com.example.beQuanTri.dto.response.order;

import com.example.beQuanTri.dto.basic.product.ProductBasicInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderItemsResponse {
    String id;
    ProductBasicInfo product;
    int quantity;
    double price;
    Date expiryDate;
}
