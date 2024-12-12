package com.example.beQuanTri.service.order;

import com.example.beQuanTri.constant.PredefineStatus;
import com.example.beQuanTri.constant.PredefinedOrder;
import com.example.beQuanTri.dto.response.order.OrderItemsResponse;
import com.example.beQuanTri.dto.response.order.OrderResponse;
import com.example.beQuanTri.entity.cart.Cart;
import com.example.beQuanTri.entity.collaborator.Collaborator;
import com.example.beQuanTri.entity.commission.Commission;
import com.example.beQuanTri.entity.order.Order;
import com.example.beQuanTri.entity.order.OrderItems;
import com.example.beQuanTri.entity.product.Product;
import com.example.beQuanTri.entity.status.Status;
import com.example.beQuanTri.entity.user.User;
import com.example.beQuanTri.exception.CustomException;
import com.example.beQuanTri.exception.ErrorCode;
import com.example.beQuanTri.helper.utils.DateConversionUtil;
import com.example.beQuanTri.mapper.order.OrderMapper;
import com.example.beQuanTri.repository.collaborator.CollaboratorRepository;
import com.example.beQuanTri.repository.collaborator.CommissionRepository;
import com.example.beQuanTri.repository.order.OrderRepository;
import com.example.beQuanTri.repository.product.ProductRepository;
import com.example.beQuanTri.repository.user.AnonymousUserRepository;
import com.example.beQuanTri.repository.user.UserRepository;
import com.example.beQuanTri.service.cart.CartAnonymousUserService;
import com.example.beQuanTri.service.cart.CartService;
import com.example.beQuanTri.service.collaborator.CollaboratorService;
import com.example.beQuanTri.service.commission.CommissionService;
import com.example.beQuanTri.service.product.ProductService;
import com.example.beQuanTri.service.status.StatusService;
import com.example.beQuanTri.service.user.UserService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class to handle all order-related operations, including creating,
 * processing, and managing orders and their commissions.
 */
@Builder
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Service
public class OrderService {

    // Dependencies
    CartService cartService;
    CollaboratorRepository collaboratorRepository;
    ProductRepository productRepository;
    CommissionRepository commissionRepository;
    UserRepository userRepository;
    StatusService statusService;
    ProductService productService;
    UserService userService;
    OrderRepository orderRepository;
    OrderMapper orderMapper;
    OrderAnonymousUserService orderAnonymousUserService;
    OrderHelperService orderHelperService;
    CollaboratorService collaboratorService;
    CommissionService commissionService;
    AnonymousUserRepository anonymousUserRepository;
    CartAnonymousUserService cartAnonymousUserService;

    /**
     * Processes a "Buy Now" order for a user.
     *
     * @param userId       the ID of the user making the purchase
     * @param ipAddress    the IP address of the user
     * @param productCode  the code of the product being purchased
     * @param quantity     the quantity of the product
     * @param referralCode the referral code (optional)
     * @return the response object of the created order
     * @throws CustomException if the quantity is invalid or the product is out of stock
     */
    public OrderResponse buyNow(
            String userId,
            String ipAddress,
            String productCode,
            int quantity,
            String referralCode) {

        log.info("Processing buy now for userId: {}, ipAddress: {}", userId, ipAddress);

        if (quantity <= 0) {
            throw new CustomException(ErrorCode.INVALID_QUANTITY);
        }

        // Map anonymous orders to user if applicable
        orderHelperService.mapAnonymousOrdersToUser(ipAddress, userId);

        Product product = productService.findProductByCode(productCode);

        if (product.getStock() < quantity) {
            throw new CustomException(ErrorCode.OUT_OF_STOCK);
        }

        // Deduct stock
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);

        User user = userService.findUserById(userId);

        // Create order
        Order order = Order.builder().user(user).build();
        double totalAmount = product.getPrice() * quantity;

        // Handle referral code and commission
        if (referralCode != null) {
            Collaborator collaborator = collaboratorService.findCollaboratorByReferralCode(referralCode);
            order.setCollaborator(collaborator);
            order.setReferralCodeUsed(referralCode);

            totalAmount -= totalAmount * PredefinedOrder.DISCOUNT_PERCENT;
            double commissionAmount = totalAmount * collaborator.getCommissionRate();

            Order savedOrder = orderRepository.save(order);
            orderHelperService.createCommission(savedOrder, collaborator, commissionAmount);
        } else {
            Collaborator assignedCollaborator = orderHelperService.findCollaboratorWithLeastOrders();
            order.setCollaborator(assignedCollaborator);

            double reducedCommissionRate = PredefinedOrder.REDUCED_COMMISSION_RATE;
            double commissionAmount = totalAmount * reducedCommissionRate;

            Order savedOrder = orderRepository.save(order);
            orderHelperService.createCommission(savedOrder, assignedCollaborator, commissionAmount);
        }

        // Set order details
        Date startDate = new Date();
        Date endDate = DateConversionUtil.addDays(startDate, product.getSubscriptionDuration());

        OrderItems orderItems = OrderItems.builder()
                .order(order)
                .product(product)
                .quantity(quantity)
                .price(product.getPrice())
                .expiryDate(endDate)
                .build();

        order.setOrderItems(new ArrayList<>(List.of(orderItems)));
        order.setTotalAmount(totalAmount);
        order.setStatus(statusService.findByStatusName(PredefineStatus.OPEN));
        order.setOrderDate(startDate);

        // Save and return the order response
        Order savedOrder = orderRepository.save(order);
        return wrapOrderResponseByRole(savedOrder);
    }

    /**
     * Creates an order from the user's cart.
     *
     * @param userId       the ID of the user
     * @param ipAddress    the IP address of the user
     * @param referralCode the referral code (optional)
     * @return the response object of the created order
     * @throws CustomException if the cart is empty or product stock is insufficient
     */
    public OrderResponse createOrder(String userId, String ipAddress, String referralCode) {

        log.info("In Method createOrder for user {} and IP {}", userId, ipAddress);

        // Map anonymous orders to user
        orderHelperService.mapAnonymousOrdersToUser(ipAddress, userId);

        Cart cart = cartService.getCartByUserId(userId);

        if (cart.getCartItems().isEmpty()) {
            throw new CustomException(ErrorCode.CART_EMPTY);
        }

        // Validate and update product stock
        cart.getCartItems().forEach(cartItem -> {
            Product product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new CustomException(ErrorCode.OUT_OF_STOCK);
            }
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        });

        // Create order
        Order order = Order.builder().user(cart.getUser()).build();

        List<OrderItems> orderItemsList = cart.getCartItems()
                .stream()
                .map(cartItem -> {
                    Product product = cartItem.getProduct();
                    Date startDate = new Date();
                    Date endDate = DateConversionUtil.addDays(startDate, product.getSubscriptionDuration());

                    return OrderItems.builder()
                            .order(order)
                            .product(product)
                            .quantity(cartItem.getQuantity())
                            .price(cartItem.getPrice())
                            .expiryDate(endDate)
                            .build();
                }).toList();

        order.setOrderItems(new ArrayList<>(orderItemsList));

        double totalAmount = orderItemsList.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        // Handle referral code and commission
        if (referralCode != null) {
            Collaborator collaborator = collaboratorService.findCollaboratorByReferralCode(referralCode);
            order.setReferralCodeUsed(referralCode);
            order.setCollaborator(collaborator);

            totalAmount -= totalAmount * PredefinedOrder.DISCOUNT_PERCENT;
            double commissionAmount = totalAmount * collaborator.getCommissionRate();

            Order savedOrder = orderRepository.save(order);
            orderHelperService.createCommission(savedOrder, collaborator, commissionAmount);
        } else {
            Collaborator assignedCollaborator = orderHelperService.findCollaboratorWithLeastOrders();
            order.setCollaborator(assignedCollaborator);

            double reducedCommissionRate = PredefinedOrder.REDUCED_COMMISSION_RATE;
            double commissionAmount = totalAmount * reducedCommissionRate;

            Order savedOrder = orderRepository.save(order);
            orderHelperService.createCommission(savedOrder, assignedCollaborator, commissionAmount);
        }

        // Finalize order details
        order.setTotalAmount(totalAmount);
        order.setStatus(statusService.findByStatusName(PredefineStatus.OPEN));
        order.setOrderDate(new Date());

        // Save and return the order response
        return wrapOrderResponseByRole(orderRepository.save(order));
    }

    /**
     * Retrieves the service dates for a user's orders with pagination.
     *
     * @param userId   the ID of the user
     * @param pageable the pagination information
     * @return a paginated list of service date responses for the user
     */
    @PreAuthorize("hasRole('User') or hasRole('Admin')")
    public Page<OrderItemsResponse> getUserServiceDates(String userId, Pageable pageable) {
        log.info("Fetching paginated service dates for userId: {}", userId);

        Page<Order> ordersPage = orderRepository.findByUserId(userId, pageable);
        return ordersPage.map(order -> {
            return order.getOrderItems().stream()
                    .map(orderItem -> {
                        orderItem.setExpiryDate(DateConversionUtil.addDays(orderItem.getOrder().getOrderDate(), orderItem.getProduct().getSubscriptionDuration()));
                        return orderMapper.toOrderItemsResponse(orderItem);
                    })
                    .collect(Collectors.toList());
        }).map(list -> list.stream().findFirst().orElse(null));
    }

    /**
     * Processes an order by its ID and the collaborator handling it.
     *
     * @param orderId        the ID of the order to process
     * @param collaboratorId the ID of the collaborator handling the order
     * @return the updated order response
     * @throws CustomException if the collaborator is unauthorized
     */
    @PreAuthorize("hasRole('Collaborator') or hasRole('Admin')")
    public OrderResponse processOrder(
            String orderId,
            String collaboratorId) {

        log.info("In Method processOrder");

        Order order = findOrderById(orderId);

        if (!order.getCollaborator().getId().equals(collaboratorId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Status inProgressStatus = statusService
                .findByStatusName(PredefineStatus.IN_PROGRESS);

        order.setStatus(inProgressStatus);

        Commission commission = commissionService
                .findCommissionByOrder(order);
        commission.setStatus(inProgressStatus);

        commissionRepository.save(commission);

        return wrapOrderResponseByRole(orderRepository.save(order));
    }

    /**
     * Completes an order by updating its status to "Complete".
     *
     * @param orderId        the ID of the order to complete
     * @param collaboratorId the ID of the collaborator handling the order
     * @return the updated order response
     * @throws CustomException if the collaborator is unauthorized or the order status is invalid
     */
    @PreAuthorize("hasRole('Collaborator') or hasRole('Admin')")
    public OrderResponse completeOrder(
            String orderId,
            String collaboratorId) {

        log.info("In Method completeOrder");

        Order order = findOrderById(orderId);

        if (!order.getCollaborator().getId().equals(collaboratorId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Status completedStatus = statusService
                .findByStatusName(PredefineStatus.COMPLETE);

        if (!order.getStatus().getStatusName().equals(PredefineStatus.IN_PROGRESS)) {
            throw new CustomException(ErrorCode.INVALID_STATUS);
        }

        order.getOrderItems().forEach(orderItem -> {
            Product product = orderItem.getProduct();
            Date startDate = new Date();
            Date endDate = DateConversionUtil
                    .addDays(
                            startDate,
                            product.getSubscriptionDuration()
                    );


            order.setStartDate(startDate);
            order.setEndDate(endDate);
        });

        Commission commission = commissionService.findCommissionByOrder(order);
        commission.setStatus(completedStatus);

        Collaborator collaborator = order.getCollaborator();
        collaborator.setTotalCommissionEarned(
                collaborator.getTotalCommissionEarned()
                        + commission.getCommissionAmount()
        );
        collaborator.setTotalOrdersHandled(
                collaborator.getTotalOrdersHandled() + 1
        );

        if (order.getUser() != null) {
            User user = order.getUser();
            user.setTotalSpent(user.getTotalSpent() + order.getTotalAmount());
            userRepository.save(user);
        }

        order.setStatus(completedStatus);

        commissionRepository.save(commission);
        orderRepository.save(order);
        collaboratorRepository.save(collaborator);


        return wrapOrderResponseByRole(order);
    }


    @PreAuthorize("!hasRole('Collaborator')")
    public OrderResponse cancelOrder(String orderId) {
        log.info("In Method cancelOrder");

        Order order = findOrderById(orderId);

        if (PredefineStatus.COMPLETE.equals(order.getStatus().getStatusName())) {
            throw new CustomException(ErrorCode.INVALID_STATUS);
        }

        if (PredefineStatus.IN_PROGRESS.equals(order.getStatus().getStatusName())) {
            long timeDiffInMillis = new Date().getTime() - order.getOrderDate().getTime();
            long timeDiffInHours = timeDiffInMillis / (60 * 60 * 1000);

            if (timeDiffInHours > PredefinedOrder.CANCEL_TIME_LIMIT_HOURS) {
                throw new CustomException(ErrorCode.CANCEL_TIME_LIMIT_EXCEEDED);
            }
        }

        order.setStatus(statusService.findByStatusName(PredefineStatus.CANCEL));

        order.getOrderItems().forEach(orderItem -> {
            Product product = orderItem.getProduct();
            product.setStock(product.getStock() + orderItem.getQuantity());
            productRepository.save(product);
        });

        Order savedOrder = orderRepository.save(order);

        return wrapOrderResponseByRole(savedOrder);
    }

    /**
     * Retrieves the order history for a specific collaborator with pagination.
     *
     * @param collaboratorId the ID of the collaborator
     * @param pageable       the pagination information
     * @return a paginated list of orders handled by the collaborator
     */
    @PreAuthorize("hasRole('Collaborator') or hasRole('Admin')")
    public Page<OrderResponse> getOrdersByCollaborator(
            String collaboratorId,
            Pageable pageable) {
        log.info("Fetching paginated orders handled by collaboratorId: {}", collaboratorId);

        Page<Order> ordersPage = orderRepository
                .findByCollaboratorId(collaboratorId, pageable);
        return ordersPage.map(this::wrapOrderResponseByRole);
    }

    /**
     * Retrieves the order history for a specific collaborator without pagination.
     *
     * @param collaboratorId the ID of the collaborator
     * @return a list of orders handled by the collaborator
     */
    @PreAuthorize("hasRole('Collaborator') or hasRole('Admin')")
    public List<OrderResponse> getOrdersByCollaboratorWithoutPagination(String collaboratorId) {
        log.info("Fetching paginated orders handled by collaboratorId without pagination: {}", collaboratorId);
        return orderRepository.findByCollaboratorId(collaboratorId)
                .stream()
                .map(this::wrapOrderResponseByRole)
                .toList();
    }

    /**
     * Retrieves the order history of a specific user.
     *
     * @param userId     the ID of the user
     * @param ipAddress  the IP address of the user
     * @param pageable   the pagination information
     * @return           a list of order responses representing the user's order history
     */
    @PreAuthorize("hasRole('User') or hasRole('Admin')")
    public Page<OrderResponse> getUserOrderHistory(
            String userId,
            String ipAddress,
            Pageable pageable) {
        log.info("Fetching paginated order history for userId: {}, ipAddress: {}", userId, ipAddress);

        // Map anonymous orders to user if applicable
        orderHelperService.mapAnonymousOrdersToUser(ipAddress, userId);

        Page<Order> ordersPage = orderRepository
                .findByUserId(userId, pageable);
        return ordersPage.map(this::wrapOrderResponseByRole);
    }

    /**
     * Retrieves all orders in the system for admin with pagination.
     *
     * @param pageable the pagination information
     * @return a paginated list of all order responses
     */
    @PreAuthorize("hasRole('Admin')")
    public Page<OrderResponse> getAllOrdersHistoryForAdmin(
            Pageable pageable) {
        log.info("Fetching paginated all orders history for Admin");

        Page<Order> ordersPage = orderRepository.findAll(pageable);
        return ordersPage.map(this::wrapOrderResponseByRole);
    }

    @PreAuthorize("hasRole('Admin')")
    public List<OrderResponse> getAllOrdersHistoryForAdminWithoutPagination() {
        log.info("Fetching paginated all orders history for Admin without pagination");

        return orderRepository.findAll()
                .stream()
                .map(this::wrapOrderResponseByRole)
                .toList();
    }

    /**
     * Retrieves the order history for an anonymous user.
     *
     * @param email       the email of the anonymous user
     * @param phoneNumber the phone number of the anonymous user
     * @return a list of OrderResponse representing the user's order history
     * @throws CustomException if no orders are found
     */
    public List<OrderResponse> getOrderHistoryForAnonymousUser(
            String email,
            String phoneNumber) {

        log.info("Fetching order history for anonymous user: {}, {}", email, phoneNumber);

        List<Order> orders = orderRepository
                .findByAnonymousUserEmailAndAnonymousUserPhoneNumber(
                        email,
                        phoneNumber
                );

        if (orders.isEmpty()) {
            throw new CustomException(ErrorCode.ORDER_NOT_FOUND);
        }

        return orders.stream()
                .map(this::wrapOrderResponseByRole)
                .toList();
    }

    /**
     * Finds an order by its ID.
     *
     * @param orderId the ID of the order
     * @return the order entity
     * @throws CustomException if the order is not found
     */
    public Order findOrderById(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
    }

    private OrderResponse wrapOrderResponseByRole(Order order) {
        String role = SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_Anonymous");

        return orderMapper.toOrderResponseByRole(order, role);
    }
}