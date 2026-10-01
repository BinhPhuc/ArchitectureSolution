package com.architecture.solution.dto.file.response;

import com.architecture.solution.enums.FileStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadResponse {
    private String id;

    private String bucket;

    @JsonProperty("object_key")
    private String objectKey;

    @JsonProperty("original_filename")
    private String originalFilename;

    @JsonProperty("content_type")
    private String contentType;

    @JsonProperty("size_bytes")
    private Long sizeBytes;

    private FileStatus status;
}
