package com.architecture.solution.entity;

import com.architecture.solution.enums.ProductStatus;
import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    private String name;

    @Column(name = "seller_id", length = 36)
    private String sellerId;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @Column(precision = 19, scale = 2)
    private BigDecimal price;

    @Builder.Default
    @Column(nullable = false)
    private Long stock = 0L;

    @Column(name = "units_sold", nullable = false)
    private Long unitsSold;

    @Column(name = "user_id", length = 36)
    private String userId;

    @Column(name = "thumbnail_id", length = 36)
    private String thumbnailId;
}
