package com.example.beQuanTri.dto.response.cart;

import com.example.beQuanTri.dto.basic.product.ProductBasicInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartItemResponse {
    String id;
    ProductBasicInfo product;
    int quantity;
    double price;
}
