package com.architecture.solution.service;

import com.architecture.solution.dto.flashsale.request.CreateCampaignRequest;
import com.architecture.solution.dto.flashsale.response.CreateCampaignResponse;
import com.architecture.solution.dto.flashsale.response.GetCampaignItemResponse;
import com.architecture.solution.dto.order.request.CreateOrderRequest;
import com.architecture.solution.dto.order.response.CreateOrderResponse;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface FlashSaleService {
    List<GetCampaignItemResponse> getItemsByCampaignId(String campaignId);

    CreateOrderResponse createOrder(CreateOrderRequest request);

    CreateCampaignResponse createCampaign(CreateCampaignRequest request) throws AccessDeniedException;
}
