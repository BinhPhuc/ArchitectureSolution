package com.architecture.solution.service.impl;

import com.architecture.solution.dto.flashsale.response.GetCampaignItemResponse;
import com.architecture.solution.entity.CampaignItem;
import com.architecture.solution.repository.CampaignItemRepository;
import com.architecture.solution.service.FlashSaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FlashSaleServiceImpl implements FlashSaleService {
    private final CampaignItemRepository campaignItemRepository;

    @Override
    public List<GetCampaignItemResponse> getItemsByCampaignId(String campaignId) {
        return campaignItemRepository.findAllByCampaignId(campaignId).stream()
                .map(this::toResponse)
                .toList();
    }

    private GetCampaignItemResponse toResponse(CampaignItem item) {
        return GetCampaignItemResponse.builder()
                .id(item.getId())
                .campaignId(item.getCampaignId())
                .name(item.getName())
                .thumbnailId(item.getThumbnailId())
                .price(item.getPrice())
                .stock(item.getStock())
                .build();
    }
}
