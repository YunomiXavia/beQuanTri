package com.example.beQuanTri.repository.cart;

import com.example.beQuanTri.entity.cart.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {
    Optional<Cart> findByUserId(String userId);
    Optional<Cart> findByAnonymousUserNameAndAnonymousUserEmailAndAnonymousUserPhoneNumber(
            String userName,
            String email,
            String phoneNumber
    );
    Optional<Cart> findByAnonymousUserIpAddress(String ipAddress);

}
