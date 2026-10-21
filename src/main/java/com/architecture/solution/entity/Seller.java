package com.architecture.solution.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "sellers")
public class Seller extends BaseEntity {
    @Id
    @Column(name = "user_id", length = 36)
    private String userId;

    @Column(name = "shop_name", nullable = false)
    private String shopName;
}
