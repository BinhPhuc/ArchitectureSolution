package com.architecture.solution.repository;

import com.architecture.solution.entity.CampaignItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CampaignItemRepository extends JpaRepository<CampaignItem, String> {
    List<CampaignItem> findAllByCampaignId(String campaignId);
}
