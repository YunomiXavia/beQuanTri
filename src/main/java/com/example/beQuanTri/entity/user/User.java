package com.example.beQuanTri.entity.user;

import com.example.beQuanTri.entity.role.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "[users]")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false, unique = true, length = 100)
    String username;

    @Column(nullable = false, unique = true)
    String password;

    @Column(nullable = false, unique = true)
    String email;

    @Column(nullable = false, unique = true, length = 15)
    String phoneNumber;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    Role role;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    Date dateJoined = new Date();

    double totalSpent = 0.0;

    @Column(length = 50)
    String lastName;

    @Column(length = 50)
    String firstName;

    LocalDate birthDate;

    @Column(length = 10)
    String resetToken;

    LocalDateTime tokenExpiryTime;

    @PrePersist
    protected void onCreate() {
        dateJoined = new Date();
        totalSpent = 0.0;
    }
}
