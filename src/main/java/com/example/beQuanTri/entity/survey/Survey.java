package com.example.beQuanTri.entity.survey;

import com.example.beQuanTri.entity.user.User;
import com.example.beQuanTri.entity.collaborator.Collaborator;
import com.example.beQuanTri.entity.status.Status;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "[survey]")
public class Survey {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne
    @JoinColumn(name = "collaborator_id")
    Collaborator collaborator;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;

    @Column(nullable = false, length = 500)
    String question;

    @Column(length = 500)
    String response;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    Date createdAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    Date responseAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }
}