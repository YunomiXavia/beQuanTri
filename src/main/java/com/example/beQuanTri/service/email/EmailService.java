package com.example.beQuanTri.service.email;

import com.example.beQuanTri.exception.CustomException;
import com.example.beQuanTri.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service class for handling email notifications.
 */
@Builder
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Service
public class EmailService {

    // Dependencies
    JavaMailSender mailSender;

    /**
     * Sends an email notification to the specified recipient.
     *
     * @param toEmail the recipient's email address
     * @param subject the subject of the email
     * @param content the content of the email
     * @throws CustomException if there is an error while sending the email
     */
    public void sendNotification(
            String toEmail,
            String subject,
            String content) {
        try {
            log.info("Preparing to send email to: {}", toEmail);

            // Construct the email message
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom("yunomix2834@gmail.com");
            mailMessage.setTo(toEmail);
            mailMessage.setSubject(subject);
            mailMessage.setText(content);

            // Send the email
            mailSender.send(mailMessage);

            log.info("Notification successfully sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Error sending email to {}: {}", toEmail, e.getMessage());

            throw new CustomException(
                    ErrorCode.EMAIL_SEND_FAILED
            );
        }
    }
}