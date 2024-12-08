package com.example.beQuanTri.controller.cart;

import com.example.beQuanTri.dto.response.ApiResponse;
import com.example.beQuanTri.dto.response.cart.CartResponse;
import com.example.beQuanTri.service.cart.CartAnonymousUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/anonymous/cart")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnonymousCartController {

    CartAnonymousUserService cartAnonymousUserService;

    /**
     * Add a product to the anonymous user's cart.
     *
     * @param request      HttpServletRequest of the anonymous user
     * @param productCode  the product code to add
     * @param quantity     the quantity to add
     * @return the updated cart response
     */
    @PostMapping("/add")
    ApiResponse<CartResponse> addToCart(
            HttpServletRequest request,
            @RequestParam("productCode") String productCode,
            @RequestParam("quantity") int quantity) {

        String ipAddress = request.getRemoteAddr();

        CartResponse cartResponse = cartAnonymousUserService
                .addToCartForAnonymous(
                        ipAddress,
                        productCode,
                        quantity
                );

        return ApiResponse.<CartResponse>builder()
                .message("Product added to cart successfully!")
                .result(cartResponse)
                .build();
    }

    /**
     * Remove a product from the anonymous user's cart.
     *
     * @param request     HttpServletRequest of the anonymous user
     * @param productCode the product code to remove
     * @param quantity    the quantity to remove
     * @return the updated cart response
     */
    @DeleteMapping("/remove")
    ApiResponse<CartResponse> removeFromCart(
            HttpServletRequest request,
            @RequestParam("productCode") String productCode,
            @RequestParam("quantity") int quantity) {

        String ipAddress = request.getRemoteAddr();

        CartResponse cartResponse = cartAnonymousUserService
                .removeFromCartForAnonymous(
                        ipAddress,
                        productCode,
                        quantity
                );
        return ApiResponse.<CartResponse>builder()
                .message("Product removed from cart successfully!")
                .result(cartResponse)
                .build();
    }

    /**
     * Get the cart for an anonymous user.
     *
     * @param request        HttpServletRequest of the anonymous user
     * @return the cart response
     */
    @GetMapping
    ApiResponse<CartResponse> getCart(HttpServletRequest request) {

        String ipAddress = request.getRemoteAddr();

        CartResponse cartResponse = cartAnonymousUserService
                .getCartForAnonymousUser(ipAddress);
        return ApiResponse.<CartResponse>builder()
                .message("Cart retrieved successfully!")
                .result(cartResponse)
                .build();
    }
}
