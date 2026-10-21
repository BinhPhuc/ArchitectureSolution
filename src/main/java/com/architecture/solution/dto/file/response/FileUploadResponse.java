package com.architecture.solution.dto.file.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadResponse {
    private String id;

    @JsonProperty("object_key")
    private String objectKey;
}
