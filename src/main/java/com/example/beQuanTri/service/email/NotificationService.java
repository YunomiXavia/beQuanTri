package com.example.beQuanTri.service.email;

import com.example.beQuanTri.constant.PredefineTime;
import com.example.beQuanTri.constant.PredefinedEmail;
import com.example.beQuanTri.entity.order.Order;
import com.example.beQuanTri.entity.order.OrderItems;
import com.example.beQuanTri.entity.user.User;
import com.example.beQuanTri.repository.order.OrderRepository;
import com.example.beQuanTri.repository.user.UserRepository;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Builder
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Service
public class NotificationService {
    OrderRepository orderRepository;
    EmailService emailService;
    UserRepository userRepository;

    @Scheduled(cron = "0 0 9 * * ?") // Runs daily at 9 AM
    public void notifyExpiringServiceDates() {
        log.info("Running scheduled task to notify about expiring service dates.");

        Date currentDate = new Date();
        List<Order> orders = orderRepository.findAll();

        orders.forEach(order -> {
            order.getOrderItems().forEach(orderItem -> {
                Date expiryDate = orderItem.getExpiryDate();

                if (expiryDate != null && isExpiringSoon(expiryDate, currentDate)) {
                    sendExpiryNotification(order, orderItem, expiryDate);
                }
            });
        });
    }

    /**
     * Checks if the expiry date is within 3 days from the current date.
     *
     * @param expiryDate the expiry date
     * @param currentDate the current date
     * @return true if the service is expiring soon, false otherwise
     */
    private boolean isExpiringSoon(Date expiryDate, Date currentDate) {
        long timeDifference = expiryDate.getTime() - currentDate.getTime();
        long daysDifference = timeDifference / (PredefineTime.MILLISECONDS_IN_DAY);
        return daysDifference <= 3 && daysDifference >= 0;
    }

    /**
     * Sends email notifications to the user, collaborator, and admin about the expiring service.
     *
     * @param order      the order containing the expiring service
     * @param orderItem  the specific order item with the expiring service
     * @param expiryDate the expiry date of the service
     */
    private void sendExpiryNotification(Order order, OrderItems orderItem, Date expiryDate) {
        User user = order.getUser();
        String userEmail = user != null ? user.getEmail() : null;
        String collaboratorEmail = order.getCollaborator() != null ? order.getCollaborator().getUser().getEmail() : null;

        String subject = "Service Expiry Notification";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String content = String.format(
                "Dear %s,\n\nThe service for product '%s' (Order ID: %s) is expiring soon on %s. Please take necessary actions.\n\nThank you.",
                user != null ? user.getFirstName() : "User",
                orderItem.getProduct().getProductName(),
                order.getId(),
                dateFormat.format(expiryDate)
        );

        if (userEmail != null) {
            emailService.sendNotification(userEmail, subject, content);
        }

        if (collaboratorEmail != null) {
            emailService.sendNotification(collaboratorEmail, subject, content);
        }

        String adminEmail = PredefinedEmail.ADMIN_EMAIL;
        emailService.sendNotification(adminEmail, subject, content);

        log.info("Expiry notification sent for Order ID: {}, Product: {}", order.getId(), orderItem.getProduct().getProductName());
    }
}
