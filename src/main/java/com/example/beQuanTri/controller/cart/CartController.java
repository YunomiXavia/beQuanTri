package com.example.beQuanTri.controller.cart;

import com.example.beQuanTri.dto.response.ApiResponse;
import com.example.beQuanTri.dto.response.cart.CartResponse;
import com.example.beQuanTri.service.cart.CartService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for handling cart-related operations,
 * including adding, removing, and retrieving items in the cart.
 */
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    CartService cartService;

    /**
     * Adds a product to the user's cart.
     *
     * @param request     the HTTP request
     * @param userId      the ID of the user
     * @param productCode the code of the product to add
     * @param quantity    the quantity of the product to add
     * @return the updated cart response
     */
    @PostMapping("/{userId}/add")
    ApiResponse<CartResponse> addToCart(
            HttpServletRequest request,
            @PathVariable("userId") String userId,
            @RequestParam("productCode") String productCode,
            @RequestParam("quantity") int quantity) {
        String ipAddress = request.getRemoteAddr();

        return ApiResponse.<CartResponse>builder()
                .message("Product added to cart successfully")
                .result(
                        cartService.addToCart(
                                userId,
                                ipAddress,
                                productCode,
                                quantity
                        )
                )
                .build();
    }

    /**
     * Removes a product from the user's cart.
     *
     *
     * @param userId      the ID of the user
     * @param productCode the code of the product to remove
     * @param quantity    the quantity of the product to remove
     * @return the updated cart response or a message if the cart is empty
     */
    @DeleteMapping("/{userId}/remove")
    ApiResponse<CartResponse> removeFromCart(
            HttpServletRequest request,
            @PathVariable("userId") String userId,
            @RequestParam("productCode") String productCode,
            @RequestParam("quantity") int quantity) {
        String ipAddress = request.getRemoteAddr();

        CartResponse response = cartService.removeFromCart(
                userId,
                ipAddress,
                productCode,
                quantity
        );

        String message = (response == null)
                ? "Cart is now empty!"
                : "Product removed from cart successfully!";

        return ApiResponse.<CartResponse>builder()
                .message(message)
                .result(response)
                .build();
    }

    /**
     * Retrieves the user's current cart.
     *
     * @param userId the ID of the user
     * @return the user's cart response
     */
    @GetMapping("/{userId}")
    ApiResponse<CartResponse> getCart(
            HttpServletRequest request,
            @PathVariable("userId") String userId) {
        String ipAddress = request.getRemoteAddr();

        return ApiResponse.<CartResponse>builder()
                .message("Cart retrieved successfully!")
                .result(
                        cartService.getCart(ipAddress, userId)
                )
                .build();
    }
}
