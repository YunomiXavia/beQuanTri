package com.example.beQuanTri.repository.order;

import com.example.beQuanTri.entity.order.Order;
import com.example.beQuanTri.entity.order.OrderItems;
import com.example.beQuanTri.entity.status.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByUserId(String userId);
    List<Order> findByCollaboratorId(String collaboratorId);
    List<Order> findByAnonymousUserEmailAndAnonymousUserPhoneNumber(
            String email,
            String phoneNumber
    );
    List<Order> findByAnonymousUserIpAddress(String ipAddress);

    @Query("SELECT o FROM Order o WHERE o.anonymousUser.ipAddress = :ipAddress " +
            "OR o.anonymousUser.email = :email " +
            "OR o.anonymousUser.phoneNumber = :phoneNumber")
    List<Order> findByAnonymousUserIpAddressAndEmailOrPhoneNumber(
            @Param("ipAddress") String ipAddress,
            @Param("email") String email,
            @Param("phoneNumber") String phoneNumber
    );

    List<Order> findAllByOrderDateAndStatus(Date orderDate, Status status);
    List<Order> findAllByCollaboratorId(String collaboratorId);

    Page<Order> findByUserId(String userId, Pageable pageable);
    Page<Order> findByCollaboratorId(
            String collaboratorId,
            Pageable pageable
    );
    Page<Order> findByAnonymousUserEmailAndAnonymousUserPhoneNumber(
            String email,
            String phoneNumber,
            Pageable pageable
    );

}
