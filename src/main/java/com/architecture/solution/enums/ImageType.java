package com.architecture.solution.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;

@Getter
@RequiredArgsConstructor
public enum ImageType {
    JPEG(MediaType.IMAGE_JPEG_VALUE),
    PNG(MediaType.IMAGE_PNG_VALUE);

    private final String contentType;
}
