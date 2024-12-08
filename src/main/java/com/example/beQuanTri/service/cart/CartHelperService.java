package com.example.beQuanTri.service.cart;

import com.example.beQuanTri.entity.cart.Cart;
import com.example.beQuanTri.entity.cart.CartItem;
import com.example.beQuanTri.entity.user.AnonymousUser;
import com.example.beQuanTri.entity.user.User;
import com.example.beQuanTri.repository.cart.CartRepository;
import com.example.beQuanTri.repository.user.AnonymousUserRepository;
import com.example.beQuanTri.service.user.UserService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Helper service for managing cart operations, including creating carts
 * for users and mapping anonymous carts to registered users.
 */
@Builder
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Service
public class CartHelperService {

    CartRepository cartRepository;
    AnonymousUserRepository anonymousUserRepository;
    UserService userService;

    /**
     * Maps an anonymous user's cart to a registered user.
     * If the registered user already has a cart, the items from the anonymous cart
     * are merged into it. The anonymous cart is then deleted.
     *
     * @param ipAddress the IP address of the anonymous user
     * @param userId    the ID of the registered user
     * @return the merged cart associated with the registered user
     */
    public Cart mapAnonymousCartToUser(String ipAddress, String userId) {

        log.info("In Method mapAnonymousCartToUser");

        Optional<Cart> anonymousCartOpt = cartRepository.findByAnonymousUserIpAddress(ipAddress);
        Optional<Cart> userCartOpt = cartRepository.findByUserId(userId);

        Cart userCart = userCartOpt.orElseGet(() -> createCartForUser(userId));

        if (anonymousCartOpt.isPresent()) {
            Cart anonymousCart = anonymousCartOpt.get();

            for (CartItem item : anonymousCart.getCartItems()) {
                Optional<CartItem> existingItem = userCart.getCartItems()
                        .stream()
                        .filter(userItem -> userItem.getProduct()
                                .getProductCode()
                                .equals(item.getProduct().getProductCode()))
                        .findFirst();

                if (existingItem.isPresent()) {
                    existingItem.get().setQuantity(
                            existingItem.get().getQuantity() + item.getQuantity()
                    );
                } else {
                    item.setCart(userCart);
                    userCart.getCartItems().add(item);
                }
            }

            cartRepository.delete(anonymousCart);
        }

        return userCart;
    }

    /**
     * Creates a new cart for an anonymous user based on their IP address.
     * If no anonymous user exists for the given IP, a new anonymous user is created.
     *
     * @param ipAddress the IP address of the anonymous user
     * @return the created cart associated with the anonymous user
     */
    public Cart createCartForAnonymousUser(String ipAddress) {

        log.info("In Method createCartForAnonymousUser");

        AnonymousUser anonymousUser = anonymousUserRepository
                .findByIpAddress(ipAddress)
                .orElseGet(() -> {
                    AnonymousUser newUser = AnonymousUser.builder()
                            .ipAddress(ipAddress)
                            .build();
                    return anonymousUserRepository.save(newUser);
                });

        Cart cart = Cart.builder()
                .anonymousUser(anonymousUser)
                .cartItems(new ArrayList<>())
                .build();

        return cartRepository.save(cart);
    }

    /**
     * Creates a new cart for a registered user.
     *
     * @param userId the ID of the user
     * @return the created cart entity associated with the user
     */
    public Cart createCartForUser(String userId) {

        log.info("In Method createCartForUser");

        User user = userService.findUserById(userId);

        Cart cart = Cart.builder()
                .user(user)
                .cartItems(new ArrayList<>())
                .build();

        return cartRepository.save(cart);
    }
}