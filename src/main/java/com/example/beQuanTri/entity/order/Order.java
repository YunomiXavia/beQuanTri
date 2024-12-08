package com.example.beQuanTri.entity.order;

import com.example.beQuanTri.entity.status.Status;
import com.example.beQuanTri.entity.user.AnonymousUser;
import com.example.beQuanTri.entity.user.User;
import com.example.beQuanTri.entity.collaborator.Collaborator;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "[orders]")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    User user;

    @ManyToOne
    @JoinColumn(name = "anonymous_user_id")
    AnonymousUser anonymousUser;


    @ManyToOne
    @JoinColumn(name = "collaborator_id")
    Collaborator collaborator;

    @ManyToOne
    @JoinColumn(name = "status_id")
    Status status;

    @OneToMany(mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    List<OrderItems> orderItems;

    double totalAmount;

    @Temporal(TemporalType.TIMESTAMP)
    Date orderDate = new Date();

    @Temporal(TemporalType.DATE)
    Date startDate;

    @Temporal(TemporalType.DATE)
    Date endDate;

    @Column(length = 10)
    String referralCodeUsed;

    @PrePersist
    protected void onCreate() {
        orderDate = new Date();
    }
}
