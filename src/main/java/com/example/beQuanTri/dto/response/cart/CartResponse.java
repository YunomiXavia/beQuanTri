package com.example.beQuanTri.dto.response.cart;

import com.example.beQuanTri.dto.basic.user.UserBasicInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartResponse {
    String id;
    UserBasicInfo user;
    List<CartItemResponse> cartItems;
}
