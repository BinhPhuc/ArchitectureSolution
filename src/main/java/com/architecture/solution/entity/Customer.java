package com.architecture.solution.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "customers")
public class Customer extends BaseEntity {
    @Id
    @Column(name = "user_id", length = 36)
    private String userId;

    @Column(name = "phone")
    private String phone;

    @Column(name = "shipping_address")
    private String shippingAddress;
}
