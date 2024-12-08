package com.example.beQuanTri.service.order;

import com.example.beQuanTri.constant.PredefineStatus;
import com.example.beQuanTri.constant.PredefinedOrder;
import com.example.beQuanTri.dto.response.order.OrderResponse;
import com.example.beQuanTri.entity.cart.Cart;
import com.example.beQuanTri.entity.collaborator.Collaborator;
import com.example.beQuanTri.entity.order.Order;
import com.example.beQuanTri.entity.order.OrderItems;
import com.example.beQuanTri.entity.product.Product;
import com.example.beQuanTri.entity.user.AnonymousUser;
import com.example.beQuanTri.exception.CustomException;
import com.example.beQuanTri.exception.ErrorCode;
import com.example.beQuanTri.helper.utils.DateConversionUtil;
import com.example.beQuanTri.mapper.order.OrderMapper;
import com.example.beQuanTri.repository.order.OrderRepository;
import com.example.beQuanTri.repository.product.ProductRepository;
import com.example.beQuanTri.repository.user.AnonymousUserRepository;
import com.example.beQuanTri.service.cart.CartAnonymousUserService;
import com.example.beQuanTri.service.collaborator.CollaboratorService;
import com.example.beQuanTri.service.product.ProductService;
import com.example.beQuanTri.service.status.StatusService;
import com.example.beQuanTri.service.user.UserService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Service class for handling orders placed by anonymous users.
 * Includes logic for "Buy Now" and cart-based order creation.
 */
@Builder
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Service
public class OrderAnonymousUserService {

    ProductService productService;
    ProductRepository productRepository;
    AnonymousUserRepository anonymousUserRepository;
    UserService userService;
    CollaboratorService collaboratorService;
    OrderRepository orderRepository;
    OrderHelperService orderHelperService;
    OrderMapper orderMapper;
    StatusService statusService;
    CartAnonymousUserService cartAnonymousUserService;

    /**
     * Processes a "Buy Now" order for an anonymous user.
     *
     * @param name         the name of the anonymous user
     * @param email        the email of the anonymous user
     * @param phoneNumber  the phone number of the anonymous user
     * @param ipAddress    the IP address of the anonymous user
     * @param productCode  the product code to purchase
     * @param quantity     the quantity of the product
     * @param referralCode (optional) referral code for discounts
     * @return OrderResponse containing the created order details
     * @throws CustomException if the product is out of stock or the quantity is invalid
     */
    public OrderResponse buyNowForAnonymousUser(
            String name,
            String email,
            String phoneNumber,
            String ipAddress,
            String productCode,
            int quantity,
            String referralCode) {

        log.info("Processing Buy Now for anonymous user: {}, {}, {}", name, email, phoneNumber);

        // Validate product and quantity
        if (quantity <= 0) {
            throw new CustomException(ErrorCode.INVALID_QUANTITY);
        }

        Product product = productService.findProductByCode(productCode);

        if (product.getStock() < quantity) {
            throw new CustomException(ErrorCode.OUT_OF_STOCK);
        }

        // Deduct stock
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);

        // Retrieve or create anonymous user
        AnonymousUser anonymousUser = anonymousUserRepository.findByNameAndEmailAndPhoneNumber(
                        name, email, phoneNumber)
                .orElseGet(() -> {
                    AnonymousUser newUser = new AnonymousUser();
                    newUser.setName(name);
                    newUser.setEmail(email);
                    newUser.setPhoneNumber(phoneNumber);
                    newUser.setIpAddress(ipAddress);
                    return anonymousUserRepository.save(newUser);
                });

        // Create order
        double totalAmount = product.getPrice() * quantity;
        Order order = new Order();
        order.setAnonymousUser(anonymousUser);

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

        Date startDate = new Date();
        Date endDate = DateConversionUtil.addDays(startDate, product.getSubscriptionDuration());

        // Create order item
        OrderItems orderItems = OrderItems.builder()
                .order(order)
                .product(product)
                .quantity(quantity)
                .price(product.getPrice())
                .expiryDate(endDate)
                .build();

        List<OrderItems> items = new ArrayList<>();
        items.add(orderItems);
        order.setOrderItems(items);

        order.setTotalAmount(totalAmount);
        order.setStatus(statusService.findByStatusName(PredefineStatus.OPEN));
        order.setOrderDate(startDate);

        Order savedOrder = orderRepository.save(order);

        return orderHelperService.wrapOrderResponseByRole(savedOrder);
    }

    /**
     * Creates an order for an anonymous user based on their cart.
     *
     * @param ipAddress    the IP address of the anonymous user
     * @param name         the name of the anonymous user
     * @param email        the email of the anonymous user
     * @param phoneNumber  the phone number of the anonymous user
     * @param referralCode (optional) referral code for discounts
     * @return OrderResponse containing the created order details
     * @throws CustomException if the cart is empty or any product is out of stock
     */
    public OrderResponse createOrderForAnonymousUser(
            String ipAddress,
            String name,
            String email,
            String phoneNumber,
            String referralCode) {

        log.info("In Method createOrderForAnonymousUser");

        AnonymousUser anonymousUser = userService.findAndGetAnonymousUserByNameAndEmailAndPhoneNumber(
                name, email, phoneNumber);
        anonymousUser.setIpAddress(ipAddress);

        Cart cart = cartAnonymousUserService.findCartForAnonymousUser(ipAddress);

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

        Order order = new Order();
        order.setAnonymousUser(anonymousUser);

        // Map cart items to order items
        List<OrderItems> orderItemsList = cart.getCartItems().stream()
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
                })
                .toList();

        order.setOrderItems(new ArrayList<>(orderItemsList));

        // Calculate total amount
        double totalAmount = orderItemsList.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        // Apply referral code if provided
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

        // Finalize the order details
        order.setTotalAmount(totalAmount);
        order.setStatus(statusService.findByStatusName(PredefineStatus.OPEN));
        order.setOrderDate(new Date());

        return orderMapper.toOrderResponse(orderRepository.save(order));
    }
}