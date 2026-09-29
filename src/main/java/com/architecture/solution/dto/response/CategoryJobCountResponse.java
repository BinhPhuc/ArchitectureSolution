package com.architecture.solution.dto.response;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryJobCountResponse {
    private String id;
    private String name;
    private Long count;
}
