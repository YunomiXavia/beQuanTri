package com.example.beQuanTri.service.cart;

import com.example.beQuanTri.dto.response.cart.CartResponse;
import com.example.beQuanTri.entity.cart.Cart;
import com.example.beQuanTri.entity.cart.CartItem;
import com.example.beQuanTri.entity.product.Product;
import com.example.beQuanTri.exception.CustomException;
import com.example.beQuanTri.exception.ErrorCode;
import com.example.beQuanTri.mapper.cart.CartMapper;
import com.example.beQuanTri.repository.cart.CartRepository;

import com.example.beQuanTri.service.product.ProductService;
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
 * Service to manage cart operations such as adding, removing products and fetching cart details.
 */
@Builder
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Service
public class CartService {

    // Dependencies
    CartRepository cartRepository;
    CartMapper cartMapper;
    ProductService productService;
    UserService userService;
    CartHelperService cartHelperService;

    /**
     * Adds a product to the user's cart.
     *
     * @param userId      the ID of the user
     * @param ipAddress   the IP address of the user
     * @param productCode the code of the product to add
     * @param quantity    the quantity of the product to add
     * @return the updated cart as a response
     * @throws CustomException if the quantity is invalid or the product is out of stock
     */
    public CartResponse addToCart(
            String userId,
            String ipAddress,
            String productCode,
            int quantity) {
        log.info("In Method addToCart for user {} and IP {}", userId, ipAddress);

        if (quantity <= 0) {
            throw new CustomException(ErrorCode.INVALID_QUANTITY);
        }

        Product product = productService
                .findProductByCode(productCode);

        Cart userCart = cartHelperService.mapAnonymousCartToUser(ipAddress, userId);

        if (userCart.getCartItems() == null) {
            userCart.setCartItems(new ArrayList<>());
        }

        Optional<CartItem> existingItem = userCart.getCartItems()
                .stream()
                .filter(item -> item
                        .getProduct()
                        .getProductCode()
                        .equals(productCode))
                .findFirst();

        int newQuantity = quantity + existingItem
                .map(CartItem::getQuantity)
                .orElse(0);

        if (product.getStock() < newQuantity) {
            throw new CustomException(ErrorCode.OUT_OF_STOCK);
        }

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(newQuantity);
        } else {
            userCart.getCartItems().add(CartItem.builder()
                    .cart(userCart)
                    .product(product)
                    .quantity(quantity)
                    .price(product.getPrice())
                    .build());
        }

        return cartMapper.toCartResponse(cartRepository.save(userCart));
    }

    /**
     * Removes a product from the user's cart.
     *
     * @param userId      the ID of the user
     * @param ipAddress   the IP address of the user
     * @param productCode the code of the product to remove
     * @param quantity    the quantity of the product to remove
     * @return the updated cart as a response or null if the cart is empty
     * @throws CustomException if the product is not found or the quantity is invalid
     */
    public CartResponse removeFromCart(
            String userId,
            String ipAddress,
            String productCode,
            int quantity) {
        log.info("In Method removeFromCart for user {} and IP {}", userId, ipAddress);

        Cart userCart = cartHelperService.mapAnonymousCartToUser(ipAddress, userId);

        if (userCart.getCartItems() == null) {
            throw new CustomException(ErrorCode.CART_EMPTY);
        }

        Optional<CartItem> existingItem = userCart
                .getCartItems()
                .stream()
                .filter(item -> item
                        .getProduct()
                        .getProductCode()
                        .equals(productCode))
                .findFirst();

        if (existingItem.isEmpty()) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND_IN_CART);
        }

        CartItem cartItem = existingItem.get();

        if (cartItem.getQuantity() < quantity) {
            throw new CustomException(ErrorCode.INVALID_QUANTITY);
        }

        cartItem.setQuantity(cartItem.getQuantity() - quantity);

        if (cartItem.getQuantity() <= 0) {
            userCart.getCartItems().remove(cartItem);
        }

        if (userCart.getCartItems().isEmpty()) {
            cartRepository.delete(userCart);
            return null;
        }

        return cartMapper.toCartResponse(cartRepository.save(userCart));
    }

    /**
     * Retrieves the cart for a user by user ID.
     *
     * @param ipAddress the IP address of the user
     * @param userId    the ID of the user
     * @return          the cart as a response
     */
    public CartResponse getCart(String ipAddress, String userId) {
        log.info("In Method getCart for user {} and IP {}", userId, ipAddress);

        return cartMapper.toCartResponse(
                cartHelperService.mapAnonymousCartToUser(ipAddress, userId)
        );
    }

    /**
     * Retrieves the cart entity for a user by user ID.
     *
     * @param userId           the ID of the user
     * @return                 the cart entity
     * @throws CustomException if the cart is not found
     */
    public Cart getCartByUserId(String userId) {
        log.info("In Method getCartByUserId");
        Cart cart = cartRepository
                .findByUserId(userId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.CART_NOT_FOUND)
                );

        if (cart.getCartItems() == null) {
            cart.setCartItems(new ArrayList<>());
        }

        return cart;
    }

    /**
     * Finds the cart for a user, creating a new one if it doesn't exist.
     *
     * @param userId the ID of the user
     * @return the cart entity
     */
    public Cart findCartByUserId(String userId) {
        log.info("In Method findCartByUserId");
        return cartRepository
                .findByUserId(userId)
                .orElseGet(
                        () -> cartHelperService.createCartForUser(userId)
                );
    }

    public Cart findCartByUserIdElseThrow(String userId){
        log.info("In Method findCartByUserIdElseThrow");
        return cartRepository
                .findByUserId(userId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.CART_NOT_FOUND)
                );
    }
}