package com.architecture.solution.controller;

import com.architecture.solution.controller.docs.FlashSaleApi;
import com.architecture.solution.dto.common.ApiResponse;
import com.architecture.solution.dto.flashsale.request.CreateCampaignRequest;
import com.architecture.solution.dto.flashsale.response.CreateCampaignResponse;
import com.architecture.solution.dto.flashsale.response.GetCampaignItemResponse;
import com.architecture.solution.dto.order.request.CreateOrderRequest;
import com.architecture.solution.dto.order.response.CreateOrderResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/flash-sales")
public class FlashSaleController implements FlashSaleApi {
    @Override
    @PostMapping("/campaign/create")
    public ResponseEntity<ApiResponse<CreateCampaignResponse>> createCampaign(
            @Valid @RequestBody CreateCampaignRequest request
    ) {
        return null;
    }

    @Override
    @GetMapping("/campaign/{campaignId}/products")
    public ResponseEntity<ApiResponse<List<GetCampaignItemResponse>>> getProductsByCampaignId(
            @PathVariable String campaignId
    ) {
        return null;
    }

    @Override
    @PostMapping("/campaign/{campaignId}/order")
    public ResponseEntity<ApiResponse<CreateOrderResponse>> createOrder(
            @Valid @RequestBody CreateOrderRequest createOrderRequest,
            @PathVariable String campaignId
    ) {
        return null;
    }
}
