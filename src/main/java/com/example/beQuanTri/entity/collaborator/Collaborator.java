package com.example.beQuanTri.entity.collaborator;

import com.example.beQuanTri.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "[collaborators]")
public class Collaborator {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id")
    User user;

    @Column(nullable = false, unique = true, length = 10)
    String referralCode;

    @Column(nullable = false)
    double commissionRate;

    int totalOrdersHandled = 0;
    int totalSurveyHandled = 0;
    double totalCommissionEarned = 0.0;

    @PrePersist
    protected void onCreate() {
        totalOrdersHandled = 0;
        totalSurveyHandled = 0;
        totalCommissionEarned = 0.0;

        if (referralCode == null || referralCode.isEmpty()) {
            referralCode = generateReferralCode();
        }
    }

    private String generateReferralCode() {
        return UUID
                .randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }
}
