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
 * Service for handling cart operations for anonymous users.
 */
@Builder
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Service
public class CartAnonymousUserService {

    ProductService productService;
    CartMapper cartMapper;
    CartRepository cartRepository;
    UserService userService;
    CartHelperService cartHelperService;

    /**
     * Adds a product to the cart for an anonymous user.
     *
     * @param ipAddress   the IP address of the anonymous user
     * @param productCode the product code to add to the cart
     * @param quantity    the quantity of the product to add
     * @return a CartResponse containing the updated cart
     * @throws CustomException if the quantity is invalid or the product is out of stock
     */
    public CartResponse addToCartForAnonymous(
            String ipAddress,
            String productCode,
            int quantity) {

        log.info("In Method addToCartForAnonymous");

        if (quantity <= 0) {
            throw new CustomException(ErrorCode.INVALID_QUANTITY);
        }

        Product product = productService.findProductByCode(productCode);

        Cart cart = findCartForAnonymousUser(ipAddress);

        if (cart.getCartItems() == null) {
            cart.setCartItems(new ArrayList<>());
        }

        Optional<CartItem> existingItem = cart.getCartItems()
                .stream()
                .filter(item -> item.getProduct().getProductCode().equals(productCode))
                .findFirst();

        int newQuantity = quantity + existingItem.map(CartItem::getQuantity).orElse(0);

        if (product.getStock() < newQuantity) {
            throw new CustomException(ErrorCode.OUT_OF_STOCK);
        }

        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(newQuantity);
        } else {
            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .price(product.getPrice())
                    .build();
            cart.getCartItems().add(cartItem);
        }

        return cartMapper.toCartResponse(cartRepository.save(cart));
    }

    /**
     * Removes a product from the cart for an anonymous user.
     *
     * @param ipAddress   the IP address of the anonymous user
     * @param productCode the product code to remove from the cart
     * @param quantity    the quantity of the product to remove
     * @return a CartResponse containing the updated cart, or null if the cart becomes empty
     * @throws CustomException if the product is not found in the cart or the quantity is invalid
     */
    public CartResponse removeFromCartForAnonymous(
            String ipAddress,
            String productCode,
            int quantity) {

        log.info("In Method removeFromCartForAnonymous");

        Cart cart = findCartForAnonymousUserElseThrow(ipAddress);

        Optional<CartItem> existingItem = cart.getCartItems()
                .stream()
                .filter(item -> item.getProduct().getProductCode().equals(productCode))
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
            cart.getCartItems().remove(cartItem);
        }

        if (cart.getCartItems().isEmpty()) {
            cartRepository.delete(cart);
            return null;
        }

        return cartMapper.toCartResponse(cartRepository.save(cart));
    }

    /**
     * Retrieves the cart for an anonymous user.
     *
     * @param ipAddress the IP address of the anonymous user
     * @return a CartResponse containing the cart details
     */
    public CartResponse getCartForAnonymousUser(String ipAddress) {

        log.info("In Method getCartForAnonymousUser");

        Cart cart = findCartForAnonymousUser(ipAddress);
        return cartMapper.toCartResponse(cart);
    }

    /**
     * Retrieves a cart for an anonymous user or throws an exception if not found.
     *
     * @param ipAddress the IP address of the anonymous user
     * @return the Cart entity
     * @throws CustomException if the cart is not found
     */
    public Cart findCartForAnonymousUserElseThrow(String ipAddress) {

        log.info("In Method findCartForAnonymousUserElseThrow");

        return cartRepository
                .findByAnonymousUserIpAddress(ipAddress)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_NOT_FOUND));
    }

    /**
     * Retrieves or creates a cart for an anonymous user.
     *
     * @param ipAddress the IP address of the anonymous user
     * @return the Cart entity
     */
    public Cart findCartForAnonymousUser(String ipAddress) {

        log.info("In Method findCartForAnonymousUser");

        Optional<Cart> existingCart = cartRepository.findByAnonymousUserIpAddress(ipAddress);

        return existingCart.orElseGet(() -> cartHelperService.createCartForAnonymousUser(ipAddress));
    }
}