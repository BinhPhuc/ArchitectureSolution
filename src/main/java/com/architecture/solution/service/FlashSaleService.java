package com.architecture.solution.service;

import com.architecture.solution.dto.flashsale.response.GetCampaignItemResponse;

import java.util.List;

public interface FlashSaleService {
    List<GetCampaignItemResponse> getItemsByCampaignId(String campaignId);
}
