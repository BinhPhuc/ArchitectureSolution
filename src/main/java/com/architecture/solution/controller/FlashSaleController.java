package com.architecture.solution.controller;

import com.architecture.solution.controller.docs.FlashSaleApi;
import com.architecture.solution.dto.common.ApiResponse;
import com.architecture.solution.dto.flashsale.request.CreateCampaignRequest;
import com.architecture.solution.dto.flashsale.response.CreateCampaignResponse;
import com.architecture.solution.dto.flashsale.response.GetCampaignItemResponse;
import com.architecture.solution.dto.order.request.CreateOrderRequest;
import com.architecture.solution.dto.order.response.CreateOrderResponse;
import com.architecture.solution.service.FlashSaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/flash-sales")
public class FlashSaleController implements FlashSaleApi {
    private final FlashSaleService flashSaleService;

    @Override
    @PostMapping("/campaign/create")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<CreateCampaignResponse>> createCampaign(
            @Valid @RequestBody CreateCampaignRequest request) {
        return ResponseEntity.ok(ApiResponse.success(flashSaleService.createCampaign(request), "Campaign created successfully"));
    }

    @Override
    @GetMapping("/campaign/{campaignId}/items")
    public ResponseEntity<ApiResponse<List<GetCampaignItemResponse>>> getItemsByCampaignId(
            @PathVariable String campaignId
    ) {
        return ResponseEntity.ok(ApiResponse.success(flashSaleService.getItemsByCampaignId(campaignId)));
    }

    @Override
    @PostMapping("/campaign/order")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<CreateOrderResponse>> createOrder(
            @Valid @RequestBody CreateOrderRequest createOrderRequest
    ) {
        return ResponseEntity.ok(ApiResponse.success(flashSaleService.createOrder(createOrderRequest), "Order created successfully"));
    }


}
