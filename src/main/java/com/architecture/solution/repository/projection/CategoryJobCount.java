package com.architecture.solution.repository.projection;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryJobCount {
    private String id;

    private String name;

    private Long jobCount;
}
