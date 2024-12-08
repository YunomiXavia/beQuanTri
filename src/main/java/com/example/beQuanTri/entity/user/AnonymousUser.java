package com.example.beQuanTri.entity.user;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "[anonymous_users]")
public class AnonymousUser {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(length = 50)
    String name;

    @Column(length = 100)
    String email;

    @Column(length = 15)
    String phoneNumber;

    @Column(length = 45)
    String ipAddress;
}
