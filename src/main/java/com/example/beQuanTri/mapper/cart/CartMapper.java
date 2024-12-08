package com.example.beQuanTri.mapper.cart;

import com.example.beQuanTri.dto.basic.product.ProductBasicInfo;
import com.example.beQuanTri.dto.basic.user.UserBasicInfo;
import com.example.beQuanTri.dto.response.cart.CartItemResponse;
import com.example.beQuanTri.dto.response.cart.CartResponse;
import com.example.beQuanTri.entity.cart.Cart;
import com.example.beQuanTri.entity.cart.CartItem;
import com.example.beQuanTri.entity.product.Product;
import com.example.beQuanTri.entity.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(target = "user", source = "user")
    @Mapping(target = "cartItems", source = "cartItems")
    CartResponse toCartResponse(Cart cart);

    @Mapping(target = "product", source = "product")
    CartItemResponse toCartItemResponse(CartItem cartItem);

    default UserBasicInfo toUserBasicInfo(User user) {
        if (user == null) return null;
        return UserBasicInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    default ProductBasicInfo toProductBasicInfo(Product product) {
        if (product == null) return null;
        return ProductBasicInfo.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .productCode(product.getProductCode())
                .build();
    }
}
