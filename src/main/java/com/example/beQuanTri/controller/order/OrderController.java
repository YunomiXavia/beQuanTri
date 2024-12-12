package com.example.beQuanTri.controller.order;

import com.example.beQuanTri.dto.response.ApiResponse;
import com.example.beQuanTri.dto.response.PaginatedResponse;
import com.example.beQuanTri.dto.response.order.OrderItemsResponse;
import com.example.beQuanTri.dto.response.order.OrderResponse;
import com.example.beQuanTri.service.order.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for handling order-related operations,
 * including creating orders, processing orders, and retrieving order histories.
 */
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
public class OrderController {

    @Autowired
    OrderService orderService;

    /**
     * Allows a user to place an order immediately for a specific product.
     *
     * @param userId       the ID of the user
     * @param productCode    the Code of the product
     * @param quantity     the quantity of the product
     * @param referralCode an optional referral code for discounts
     * @return the order response
     */
    @PostMapping("/order/{userId}/buy-now")
    ApiResponse<OrderResponse> buyNow(
            HttpServletRequest request,
            @PathVariable("userId") String userId,
            @RequestParam("productCode") String productCode,
            @RequestParam("quantity") int quantity,
            @RequestParam(value = "referralCode", required = false) String referralCode) {
        String ipAddress = request.getRemoteAddr();

        return ApiResponse.<OrderResponse>builder()
                .message("Order placed successfully!")
                .result(
                        orderService.buyNow(
                                userId,
                                ipAddress,
                                productCode,
                                quantity,
                                referralCode
                        )
                )
                .build();
    }

    /**
     * Creates an order from the user's cart.
     *
     * @param userId       the ID of the user
     * @param referralCode an optional referral code for discounts
     * @return the created order response
     */
    @PostMapping("/order/{userId}/create")
    ApiResponse<OrderResponse> createOrder(
            HttpServletRequest request,
            @PathVariable("userId") String userId,
            @RequestParam(value = "referralCode", required = false) String referralCode) {
        String ipAddress = request.getRemoteAddr();

        return ApiResponse.<OrderResponse>builder()
                .message("Order created successfully!")
                .result(
                        orderService.createOrder(
                                userId,
                                ipAddress,
                                referralCode
                        )
                )
                .build();
    }

    /**
     * Processes an order by marking it as in progress.
     *
     * @param orderId       the ID of the order
     * @param collaboratorId the ID of the collaborator handling the order
     * @return the processed order response
     */
    @PutMapping("/order/{orderId}/process")
    ApiResponse<OrderResponse> processOrder(
            @PathVariable("orderId") String orderId,
            @RequestParam("collaboratorId") String collaboratorId) {
        return ApiResponse.<OrderResponse>builder()
                .message("Order processed successfully!")
                .result(
                        orderService.processOrder(
                                orderId,
                                collaboratorId
                        )
                )
                .build();
    }

    /**
     * Completes an order and marks it as finished.
     *
     * @param orderId       the ID of the order
     * @param collaboratorId the ID of the collaborator handling the order
     * @return the completed order response
     */
    @PutMapping("/order/{orderId}/complete")
    ApiResponse<OrderResponse> completeOrder(
            @PathVariable("orderId") String orderId,
            @RequestParam("collaboratorId") String collaboratorId) {
        return ApiResponse.<OrderResponse>builder()
                .message("Order completed successfully!")
                .result(
                        orderService.completeOrder(
                                orderId,
                                collaboratorId
                        )
                )
                .build();
    }

    /**
     * Cancels an order and marks it as cancelled.
     *
     * @param orderId the ID of the order
     * @return the cancelled order response
     */
    @PutMapping("/order/{orderId}/cancel")
    ApiResponse<OrderResponse> cancelOrder(
            @PathVariable("orderId") String orderId) {
        return ApiResponse.<OrderResponse>builder()
                .message("Order cancelled successfully!")
                .result(
                        orderService.cancelOrder(orderId)
                )
                .build();
    }

    /**
     * Retrieves the order history for a specific user with pagination.
     *
     * @param userId the ID of the user
     * @param page   the page number (default is 0)
     * @param size   the page size (default is 10)
     * @return the paginated list of user's order history
     */
    @GetMapping("/orders/user/{userId}/history")
    public ApiResponse<PaginatedResponse<Page<OrderResponse>>> getUserOrderHistory(
            HttpServletRequest request,
            @PathVariable("userId") String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String ipAddress = request.getRemoteAddr();
        Pageable pageable = PageRequest.of(page, size);

        Page<OrderResponse> userOrderHistory = orderService.getUserOrderHistory(userId, ipAddress, pageable);

        PaginatedResponse<Page<OrderResponse>> paginatedResponse = PaginatedResponse.<Page<OrderResponse>>builder()
                .message("User order history retrieved successfully with pagination!")
                .data(userOrderHistory)
                .currentPage(userOrderHistory.getNumber())
                .totalPages(userOrderHistory.getTotalPages())
                .totalElements(userOrderHistory.getTotalElements())
                .last(userOrderHistory.isLast())
                .build();

        return ApiResponse.<PaginatedResponse<Page<OrderResponse>>>builder()
                .message("User order history retrieved successfully with pagination!")
                .result(paginatedResponse)
                .build();
    }

    /**
     * Retrieves all orders handled by a collaborator with pagination.
     *
     * @param collaboratorId the ID of the collaborator
     * @param page           the page number (default is 0)
     * @param size           the page size (default is 10)
     * @return the paginated list of orders handled by the collaborator
     */
    @GetMapping("/orders/collaborator/{collaboratorId}/history")
    public ApiResponse<PaginatedResponse<Page<OrderResponse>>> getCollaboratorOrderHistory(
            @PathVariable("collaboratorId") String collaboratorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<OrderResponse> collaboratorOrderHistory = orderService.getOrdersByCollaborator(collaboratorId, pageable);

        PaginatedResponse<Page<OrderResponse>> paginatedResponse = PaginatedResponse.<Page<OrderResponse>>builder()
                .message("Orders handled by collaborator retrieved successfully with pagination!")
                .data(collaboratorOrderHistory)
                .currentPage(collaboratorOrderHistory.getNumber())
                .totalPages(collaboratorOrderHistory.getTotalPages())
                .totalElements(collaboratorOrderHistory.getTotalElements())
                .last(collaboratorOrderHistory.isLast())
                .build();

        return ApiResponse.<PaginatedResponse<Page<OrderResponse>>>builder()
                .message("Orders handled by collaborator retrieved successfully with pagination!")
                .result(paginatedResponse)
                .build();
    }

    /**
     * Retrieves all orders handled by a collaborator without pagination.
     *
     * @param collaboratorId the ID of the collaborator
     * @return the list of orders handled by the collaborator
     */
    @GetMapping("/orders/collaborator/{collaboratorId}/history/without-pagination")
    public ApiResponse<List<OrderResponse>> getCollaboratorOrderHistoryWithoutPagination(
            @PathVariable("collaboratorId") String collaboratorId) {

        return ApiResponse.<List<OrderResponse>>builder()
                .message("Orders handled by collaborator retrieved successfully with pagination!")
                .result(orderService.getOrdersByCollaboratorWithoutPagination(collaboratorId))
                .build();
    }

    /**
     * Retrieves all orders in the system for admin with pagination.
     *
     * @param page the page number (default is 0)
     * @param size the page size (default is 10)
     * @return the paginated list of all orders
     */
    @GetMapping("/orders/history")
    public ApiResponse<PaginatedResponse<Page<OrderResponse>>> getOrdersHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<OrderResponse> allOrdersHistory = orderService.getAllOrdersHistoryForAdmin(pageable);

        PaginatedResponse<Page<OrderResponse>> paginatedResponse = PaginatedResponse.<Page<OrderResponse>>builder()
                .message("All orders retrieved successfully with pagination!")
                .data(allOrdersHistory)
                .currentPage(allOrdersHistory.getNumber())
                .totalPages(allOrdersHistory.getTotalPages())
                .totalElements(allOrdersHistory.getTotalElements())
                .last(allOrdersHistory.isLast())
                .build();

        return ApiResponse.<PaginatedResponse<Page<OrderResponse>>>builder()
                .message("All orders retrieved successfully with pagination!")
                .result(paginatedResponse)
                .build();
    }

    /**
     * Retrieves all orders in the system for admin without pagination.
     *
     * @return the list of all orders
     */
    @GetMapping("/orders/history/without-pagination")
    public ApiResponse<List<OrderResponse>> getOrdersHistoryWithoutPagination() {

        return ApiResponse.<List<OrderResponse>>builder()
                .message("All orders retrieved successfully with pagination!")
                .result(orderService.getAllOrdersHistoryForAdminWithoutPagination())
                .build();
    }

    /**
     * Retrieves the service dates for a user's orders with pagination.
     *
     * @param userId the ID of the user
     * @param page   the page number (default is 0)
     * @param size   the page size (default is 10)
     * @return the paginated list of service dates for the user
     */
    @GetMapping("/revenue/user/{userId}/service-dates")
    public ApiResponse<PaginatedResponse<Page<OrderItemsResponse>>> getUserServiceDates(
            @PathVariable("userId") String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<OrderItemsResponse> serviceDates = orderService.getUserServiceDates(userId, pageable);

        PaginatedResponse<Page<OrderItemsResponse>> paginatedResponse = PaginatedResponse.<Page<OrderItemsResponse>>builder()
                .message("Service dates retrieved successfully with pagination!")
                .data(serviceDates)
                .currentPage(serviceDates.getNumber())
                .totalPages(serviceDates.getTotalPages())
                .totalElements(serviceDates.getTotalElements())
                .last(serviceDates.isLast())
                .build();

        return ApiResponse.<PaginatedResponse<Page<OrderItemsResponse>>>builder()
                .message("Service dates retrieved successfully with pagination!")
                .result(paginatedResponse)
                .build();
    }
}
