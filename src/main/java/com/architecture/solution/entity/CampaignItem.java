package com.architecture.solution.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@Table(name = "campaign_items", uniqueConstraints = {
        @UniqueConstraint(name = "uk_flash_sale_items_product", columnNames = {"campaign_id", "product_id"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CampaignItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    @Column(name = "campaign_id", length = 36)
    private String campaignId;

    @Column(name = "product_id", length = 36)
    private String productId;

    @Column(name = "price", precision = 19, scale = 2)
    private BigDecimal price;

    @Column(name = "stock", nullable = false)
    private Long stock;

    @Column(name = "sold_quantity", nullable = false)
    private Long soldQuantity;
}
