package com.architecture.solution.service.impl;

import com.architecture.solution.client.payment.PaymentClient;
import com.architecture.solution.client.payment.dto.request.PaymentRequest;
import com.architecture.solution.dto.flashsale.request.CreateCampaignItemRequest;
import com.architecture.solution.dto.flashsale.request.CreateCampaignRequest;
import com.architecture.solution.dto.flashsale.response.CreateCampaignResponse;
import com.architecture.solution.dto.flashsale.response.GetCampaignItemResponse;
import com.architecture.solution.dto.order.request.CreateOrderRequest;
import com.architecture.solution.dto.order.response.CreateOrderResponse;
import com.architecture.solution.entity.Campaign;
import com.architecture.solution.entity.CampaignItem;
import com.architecture.solution.entity.Order;
import com.architecture.solution.entity.OrderItem;
import com.architecture.solution.enums.OrderStatus;
import com.architecture.solution.exception.InvalidArgumentException;
import com.architecture.solution.exception.OutOfStockException;
import com.architecture.solution.exception.ResourceNotFoundException;
import com.architecture.solution.repository.*;
import com.architecture.solution.service.FlashSaleService;
import com.architecture.solution.service.SchedulerService;
import com.architecture.solution.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlashSaleServiceImpl implements FlashSaleService {
    private final CampaignItemRepository campaignItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentClient paymentClient;
    private final CampaignRepository campaignRepository;
    private final FileRepository fileRepository;
    private final SchedulerService schedulerService;

    @Override
    public List<GetCampaignItemResponse> getItemsByCampaignId(String campaignId) {
        if (!campaignRepository.existsById(campaignId)) {
            throw new ResourceNotFoundException("Campaign not found");
        }
        return campaignItemRepository.findAllByCampaignId(campaignId).stream()
                .map(GetCampaignItemResponse::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        String customerId = SecurityUtils.getUserId();
        final BigDecimal[] totalAmount = {BigDecimal.ZERO};
        List<OrderItem> orderItems = new ArrayList<>();
        Instant now = Instant.now();
        request.getItems().forEach(item -> {
            CampaignItem campaignItem = campaignItemRepository.findById(item.getCampaignItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Campaign item not found"));
            Campaign campaign = campaignRepository.findById(campaignItem.getCampaignId())
                    .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
            if (now.isBefore(campaign.getStartAt()) || now.isAfter(campaign.getEndAt())) {
                throw new InvalidArgumentException("Campaign is not active");
            }
            Long stock = campaignItem.getStock();
            Integer quantity = item.getQuantity();
            if (stock < quantity) {
                throw new OutOfStockException("Not enough stock");
            }
            campaignItem.setStock(stock - quantity);
            totalAmount[0] = totalAmount[0].add(campaignItem.getPrice().multiply(BigDecimal.valueOf(quantity)));
            orderItems.add(OrderItem.builder()
                    .campaignItemId(item.getCampaignItemId())
                    .quantity(item.getQuantity())
                    .build());
        });
        Order newOrder = Order
                .builder()
                .customerId(customerId)
                .totalAmount(totalAmount[0])
                .status(OrderStatus.PENDING)
                .build();
        Order savedOrder = orderRepository.save(newOrder);
        orderItems.forEach(item -> item.setOrderId(savedOrder.getId()));
        orderItemRepository.saveAll(orderItems);
        paymentClient.processPayment(PaymentRequest.builder()
                .customerId(customerId)
                .totalAmount(totalAmount[0])
                .build());
        return CreateOrderResponse
                .builder()
                .id(savedOrder.getId())
                .totalAmount(savedOrder.getTotalAmount())
                .status(savedOrder.getStatus())
                .items(request.getItems())
                .build();
    }

    @Override
    @Transactional
    public CreateCampaignResponse createCampaign(CreateCampaignRequest request) {
        if (!request.getEndAt().isAfter(request.getStartAt())) {
            throw new InvalidArgumentException("end_at must be after start_at");
        }
        String userId = SecurityUtils.getUserId();
        request.getItems().stream()
                .map(CreateCampaignItemRequest::getThumbnailId)
                .filter(StringUtils::hasText)
                .forEach(thumbnailId -> fileRepository.findById(thumbnailId)
                        .filter(file -> userId.equals(file.getOwnerId()))
                        .orElseThrow(() -> new ResourceNotFoundException("Thumbnail not found: " + thumbnailId)));
        Campaign campaign = campaignRepository.save(Campaign.builder()
                .name(request.getName())
                .description(request.getDescription())
                .endAt(request.getEndAt())
                .startAt(request.getStartAt())
                .sellerId(userId).build());

        List<CampaignItem> campaignItems = request.getItems().stream().map(
                item -> CampaignItem.builder()
                        .name(item.getName())
                        .price(item.getPrice())
                        .thumbnailId(item.getThumbnailId())
                        .campaignId(campaign.getId())
                        .stock(item.getStock())
                        .build()).toList();

        campaignItemRepository.saveAll(campaignItems);
        schedulerService.scheduleOneTimeTask(request.getStartAt());
        return CreateCampaignResponse.builder()
                .id(campaign.getId())
                .name(campaign.getName())
                .startedAt(campaign.getStartAt())
                .endedAt(campaign.getEndAt()).build();
    }
}
