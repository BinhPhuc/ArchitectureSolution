package com.architecture.solution.service.impl;

import com.architecture.solution.client.payment.PaymentClient;
import com.architecture.solution.client.payment.dto.request.PaymentRequest;
import com.architecture.solution.dto.order.request.CreateOrderRequest;
import com.architecture.solution.entity.CampaignItem;
import com.architecture.solution.entity.Order;
import com.architecture.solution.entity.OrderItem;
import com.architecture.solution.entity.User;
import com.architecture.solution.repository.CampaignItemRepository;
import com.architecture.solution.repository.OrderItemRepository;
import com.architecture.solution.repository.OrderRepository;
import com.architecture.solution.security.CustomUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FlashSaleServiceImplTests {
    @Mock
    private CampaignItemRepository campaignItemRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private PaymentClient paymentClient;
    @InjectMocks
    private FlashSaleServiceImpl flashSaleService;

    @AfterEach
    void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createsOrderWithoutAssignedIdAndLinksItemsToSavedOrder() {
        var principal = new CustomUserDetails(User.builder().id("customer-id").build(), List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, List.of()));
        var firstItem = CampaignItem.builder().id("item-1").price(new BigDecimal("12.50")).stock(10L).build();
        var secondItem = CampaignItem.builder().id("item-2").price(new BigDecimal("7.00")).stock(5L).build();
        when(campaignItemRepository.findById("item-1")).thenReturn(Optional.of(firstItem));
        when(campaignItemRepository.findById("item-2")).thenReturn(Optional.of(secondItem));
        String generatedOrderId = UUID.randomUUID().toString();
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            assertNull(order.getId(), "A generated ID must be unset when saving a new order");
            assertEquals("customer-id", order.getCustomerId());
            assertEquals(new BigDecimal("46.00"), order.getTotalAmount());
            return Order.builder().id(generatedOrderId)
                    .customerId(order.getCustomerId()).totalAmount(order.getTotalAmount()).build();
        });
        var request = CreateOrderRequest.builder().items(List.of(
                com.architecture.solution.dto.order.common.OrderItem.builder()
                        .campaignItemId("item-1").quantity(2L).build(),
                com.architecture.solution.dto.order.common.OrderItem.builder()
                        .campaignItemId("item-2").quantity(3L).build())).build();

        var response = flashSaleService.createOrder(request);

        verify(orderRepository).save(any(Order.class));
        ArgumentCaptor<Iterable<OrderItem>> items = ArgumentCaptor.forClass(Iterable.class);
        verify(orderItemRepository).saveAll(items.capture());
        var savedItems = (List<OrderItem>) items.getValue();
        assertEquals(2, savedItems.size());
        assertEquals(generatedOrderId, savedItems.get(0).getOrderId());
        assertEquals(generatedOrderId, savedItems.get(1).getOrderId());
        assertEquals("item-1", savedItems.get(0).getCampaignItemId());
        assertEquals(2L, savedItems.get(0).getQuantity());
        assertEquals("item-2", savedItems.get(1).getCampaignItemId());
        assertEquals(3L, savedItems.get(1).getQuantity());
        assertEquals(8L, firstItem.getStock());
        assertEquals(2L, secondItem.getStock());
        var payment = ArgumentCaptor.forClass(PaymentRequest.class);
        verify(paymentClient).processPayment(payment.capture());
        assertEquals("customer-id", payment.getValue().getCustomerId());
        assertEquals(new BigDecimal("46.00"), payment.getValue().getTotalAmount());
        assertEquals(request.getItems(), response.getItems());
    }
}
