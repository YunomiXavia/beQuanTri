package com.example.beQuanTri.entity.commission;

import com.example.beQuanTri.entity.order.Order;
import com.example.beQuanTri.entity.status.Status;
import com.example.beQuanTri.entity.collaborator.Collaborator;
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
@Table(name = "[commissions]")
public class Commission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "collaborator_id", nullable = false)
    Collaborator collaborator;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    Order order;

    @Column(nullable = false)
    double commissionAmount;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    Date dateEarned = new Date();

    @ManyToOne
    @JoinColumn(name = "status_id")
    Status status;

    @PrePersist
    protected void onCreate() {
        dateEarned = new Date();
    }
}

