package com.tphone.service.impl;

import com.tphone.dto.request.AddToCartRequest;
import com.tphone.dto.request.UpdateCartItemRequest;
import com.tphone.dto.response.CartItemResponse;
import com.tphone.dto.response.CartResponse;
import com.tphone.entity.Account;
import com.tphone.entity.Cart;
import com.tphone.entity.CartItem;
import com.tphone.entity.Product;
import com.tphone.enums.ProductStatus;
import com.tphone.exception.BadRequestException;
import com.tphone.exception.ResourceNotFoundException;
import com.tphone.repository.AccountRepository;
import com.tphone.repository.CartItemRepository;
import com.tphone.repository.CartRepository;
import com.tphone.repository.ProductRepository;
import com.tphone.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final AccountRepository accountRepository;

    @Override
    public CartResponse getMyCart(Long accountId) {
        Cart cart = getOrCreateCart(accountId);
        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addToCart(Long accountId, AddToCartRequest request) {
        if (request.getProductId() == null) {
            throw new BadRequestException("Product id is required");
        }

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new BadRequestException("Quantity must be greater than 0"); //
        }
        Product product = productRepository.findByIdAndDeletedAtIsNull(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found")); //khong có hoac đã xóa
        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new BadRequestException("Product is not available"); // ko dược cấp quyền
        }
        if (product.getStockQuantity() == null || product.getStockQuantity() <= 0) {
            throw new BadRequestException("Product is out of stock"); // có nhưng hết
        }


        //

        Cart cart = getOrCreateCart(accountId);

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .orElse(null);


        int requestedQty = request.getQuantity();
        if (cartItem == null) {
            if (requestedQty > product.getStockQuantity()) {
                throw new BadRequestException("Requested quantity exceeds stock");
            }

            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(requestedQty);
            cartItem.setUnitPrice(product.getPrice());
        }

        else {
            int newQty = cartItem.getQuantity() + requestedQty; //
            if (newQty > product.getStockQuantity()) { //số lượng thêm hơn số lượng produ
                throw new BadRequestException("Requested quantity exceeds stock");
            }

            cartItem.setQuantity(newQty); //cập nhật quantity
            cartItem.setUnitPrice(product.getPrice()); // capnha gia
        }

        cartItemRepository.save(cartItem);
        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse updateCartItem(Long accountId, Long cartItemId, UpdateCartItemRequest request) {
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new BadRequestException("Quantity must be greater than 0");
        }

        Cart cart = getOrCreateCart(accountId);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Cart item does not belong to current user");
        }

        Product product = cartItem.getProduct();
        if (request.getQuantity() > product.getStockQuantity()) {
            throw new BadRequestException("Requested quantity exceeds stock");
        }

        cartItem.setQuantity(request.getQuantity());
        cartItem.setUnitPrice(product.getPrice());
        cartItemRepository.save(cartItem);

        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse removeCartItem(Long accountId, Long cartItemId) {
        Cart cart = getOrCreateCart(accountId);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Cart item does not belong to current user");
        }

        cartItemRepository.delete(cartItem);
        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long accountId) {
        Cart cart = getOrCreateCart(accountId);
        cartItemRepository.deleteAllByCartId(cart.getId());
    }





    @Transactional
    protected Cart getOrCreateCart(Long accountId) {
        return cartRepository.findByAccountId(accountId)
                .orElseGet(() -> {
                    Account account = accountRepository.findById(accountId)
                            .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

                    Cart cart = new Cart();
                    cart.setAccount(account);
                    return cartRepository.save(cart);
                });
    }

    protected CartResponse buildCartResponse(Cart cart) {
        List<CartItem> cartItems = cartItemRepository.findAllByCartId(cart.getId());

        List<CartItemResponse> itemResponses = cartItems.stream()
                .map(this::toCartItemResponse)
                .toList();

        int totalItems = cartItems.stream()
                .map(CartItem::getQuantity)
                .reduce(0, Integer::sum);

        BigDecimal totalAmount = cartItems.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(
                cart.getId(),
                cart.getAccount().getId(),
                itemResponses,
                totalItems,
                totalAmount
        );
    }

    protected CartItemResponse toCartItemResponse(CartItem item) {
        BigDecimal lineTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

        return new CartItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getSlug(),
                item.getProduct().getThumbnailUrl(),
                item.getUnitPrice(),
                item.getQuantity(),
                lineTotal
        );
    }
}