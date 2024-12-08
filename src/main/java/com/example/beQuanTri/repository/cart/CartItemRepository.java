package com.example.beQuanTri.repository.cart;

import com.example.beQuanTri.entity.cart.CartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, String> {
    Page<CartItem> findByCart_UserId(String userId, Pageable pageable);
    Page<CartItem> findByCartId(String cartId, Pageable pageable);
}
