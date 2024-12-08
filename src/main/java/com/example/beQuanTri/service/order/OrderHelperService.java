package com.example.beQuanTri.service.order;

import com.example.beQuanTri.constant.PredefineStatus;
import com.example.beQuanTri.dto.response.order.OrderResponse;
import com.example.beQuanTri.entity.collaborator.Collaborator;
import com.example.beQuanTri.entity.commission.Commission;
import com.example.beQuanTri.entity.order.Order;
import com.example.beQuanTri.entity.status.Status;
import com.example.beQuanTri.entity.user.User;
import com.example.beQuanTri.exception.CustomException;
import com.example.beQuanTri.exception.ErrorCode;
import com.example.beQuanTri.mapper.order.OrderMapper;
import com.example.beQuanTri.repository.collaborator.CollaboratorRepository;
import com.example.beQuanTri.repository.collaborator.CommissionRepository;
import com.example.beQuanTri.repository.order.OrderRepository;
import com.example.beQuanTri.service.status.StatusService;
import com.example.beQuanTri.service.user.UserService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * Helper service for managing and processing orders, including mapping
 * anonymous orders, creating commissions, and role-based order responses.
 */
@Builder
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Service
public class OrderHelperService {

    UserService userService;
    StatusService statusService;
    CommissionRepository commissionRepository;
    CollaboratorRepository collaboratorRepository;
    OrderRepository orderRepository;
    OrderMapper orderMapper;

    /**
     * Maps orders placed by an anonymous user (identified by IP address)
     * to a registered user (identified by user ID).
     *
     * @param ipAddress the IP address of the anonymous user
     * @param userId    the ID of the registered user
     */
    public void mapAnonymousOrdersToUser(String ipAddress, String userId) {

        log.info("Mapping anonymous orders for IP address: {} to user ID: {}", ipAddress, userId);

        User user = userService.findUserById(userId);

        List<Order> anonymousOrders = orderRepository
                .findByAnonymousUserIpAddressAndEmailOrPhoneNumber(
                        ipAddress,
                        user.getEmail(),
                        user.getPhoneNumber()
                );

        if (!anonymousOrders.isEmpty()) {
            for (Order order : anonymousOrders) {
                order.setUser(user);
                order.setAnonymousUser(null);
            }

            orderRepository.saveAll(anonymousOrders);

            log.info("Mapped {} anonymous orders to user ID: {}", anonymousOrders.size(), userId);
        } else {
            log.info("No anonymous orders found for mapping to user ID: {}", userId);
        }
    }

    /**
     * Creates a commission for a collaborator based on an order.
     *
     * @param order            the order associated with the commission
     * @param collaborator     the collaborator to receive the commission
     * @param commissionAmount the commission amount
     */
    void createCommission(Order order, Collaborator collaborator, double commissionAmount) {

        log.info("In Method createCommission");

        Status openStatus = statusService.findByStatusName(PredefineStatus.OPEN);

        Commission commission = Commission.builder()
                .collaborator(collaborator)
                .order(order)
                .commissionAmount(commissionAmount)
                .status(openStatus)
                .build();

        // Save the commission and update collaborator's total earned commission
        commissionRepository.save(commission);

        collaborator.setTotalCommissionEarned(
                collaborator.getTotalCommissionEarned() + commissionAmount
        );

        collaboratorRepository.save(collaborator);
    }

    /**
     * Wraps an order response based on the user's role.
     *
     * @param order the order to wrap
     * @return the wrapped order response tailored to the user's role
     */
    OrderResponse wrapOrderResponseByRole(Order order) {

        String role = SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_Anonymous");

        return orderMapper.toOrderResponseByRole(order, role);
    }

    /**
     * Finds the collaborator with the least number of handled orders.
     *
     * @return the collaborator with the least orders
     * @throws CustomException if no collaborators are available
     */
    Collaborator findCollaboratorWithLeastOrders() {

        log.info("In Method findCollaboratorWithLeastOrders");

        return collaboratorRepository.findAll()
                .stream()
                .min(Comparator.comparingInt(Collaborator::getTotalOrdersHandled))
                .orElseThrow(() -> new CustomException(ErrorCode.NO_AVAILABLE_COLLABORATOR));
    }
}