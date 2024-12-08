package com.example.beQuanTri.controller.order;

import com.example.beQuanTri.dto.response.ApiResponse;
import com.example.beQuanTri.dto.response.order.OrderResponse;
import com.example.beQuanTri.service.order.OrderAnonymousUserService;
import com.example.beQuanTri.service.order.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing orders placed by anonymous users.
 */
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/anonymous")
@Slf4j
public class AnonymousOrderController {

    OrderService orderService;
    OrderAnonymousUserService orderAnonymousUserService;

    /**
     * Allows an anonymous user to place a "Buy Now" order.
     *
     * @param request      the HTTP request to get the IP address
     * @param name         the name of the anonymous user
     * @param email        the email of the anonymous user
     * @param phoneNumber  the phone number of the anonymous user
     * @param productCode  the code of the product to order
     * @param quantity     the quantity of the product to order
     * @param referralCode (optional) referral code for the order
     * @return the response containing the order details
     */
    @PostMapping("/buy-now")
    ApiResponse<OrderResponse> buyNow(
            HttpServletRequest request,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("productCode") String productCode,
            @RequestParam("quantity") int quantity,
            @RequestParam(value = "referralCode", required = false) String referralCode) {

        String ipAddress = request.getRemoteAddr();

        OrderResponse orderResponse = orderAnonymousUserService
                .buyNowForAnonymousUser(
                        name,
                        email,
                        phoneNumber,
                        ipAddress,
                        productCode,
                        quantity,
                        referralCode
                );

        return ApiResponse.<OrderResponse>builder()
                .message("Order created successfully!")
                .result(orderResponse)
                .build();
    }

    /**
     * Creates an order for an anonymous user.
     *
     * @param request      the HTTP request to get the IP address
     * @param name         the name of the anonymous user
     * @param email        the email of the anonymous user
     * @param phoneNumber  the phone number of the anonymous user
     * @param referralCode (optional) referral code for the order
     * @return the response containing the created order details
     */
    @PostMapping("/order/create")
    ApiResponse<OrderResponse> createOrder(
            HttpServletRequest request,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam(value = "referralCode", required = false) String referralCode) {

        String ipAddress = request.getRemoteAddr();

        OrderResponse orderResponse = orderAnonymousUserService
                .createOrderForAnonymousUser(
                        ipAddress,
                        name,
                        email,
                        phoneNumber,
                        referralCode
                );

        return ApiResponse.<OrderResponse>builder()
                .message("Order created successfully!")
                .result(orderResponse)
                .build();
    }

    /**
     * Retrieves the order history of an anonymous user.
     *
     * @param email       the email of the anonymous user
     * @param phoneNumber the phone number of the anonymous user
     * @return the response containing a list of orders made by the user
     */
    @GetMapping("/order/history")
    ApiResponse<List<OrderResponse>> getOrderHistory(
            @RequestParam("email") String email,
            @RequestParam("phoneNumber") String phoneNumber) {

        List<OrderResponse> orderResponses = orderService
                .getOrderHistoryForAnonymousUser(
                        email,
                        phoneNumber
                );

        return ApiResponse.<List<OrderResponse>>builder()
                .message("Order history retrieved successfully!")
                .result(orderResponses)
                .build();
    }
}