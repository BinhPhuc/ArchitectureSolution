package com.architecture.solution.controller.docs;

import com.architecture.solution.dto.common.ApiResponse;
import com.architecture.solution.dto.flashsale.request.CreateCampaignRequest;
import com.architecture.solution.dto.flashsale.response.CreateCampaignResponse;
import com.architecture.solution.dto.flashsale.response.GetCampaignItemResponse;
import com.architecture.solution.dto.order.request.CreateOrderRequest;
import com.architecture.solution.dto.order.response.CreateOrderResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Flash Sale Controller")
public interface FlashSaleApi {
    ResponseEntity<ApiResponse<CreateCampaignResponse>> createCampaign(CreateCampaignRequest request);

    ResponseEntity<ApiResponse<List<GetCampaignItemResponse>>> getItemsByCampaignId(String campaignId);

    ResponseEntity<ApiResponse<CreateOrderResponse>> createOrder(CreateOrderRequest createOrderRequest, String campaignId);
}
