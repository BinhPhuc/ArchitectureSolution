package com.architecture.solution.util;

import com.architecture.solution.enums.ImageType;
import com.architecture.solution.exception.IllegalFileException;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;

public final class FileUtils {
    private FileUtils() {
    }

    public static void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty() || !StringUtils.hasText(file.getOriginalFilename())) {
            throw new IllegalFileException("Image must be a non-empty file with a filename");
        }
        ImageType imageType = Arrays.stream(ImageType.values())
                .filter(type -> type.getContentType().equals(file.getContentType()))
                .findFirst()
                .orElseThrow(() -> new IllegalFileException("Unsupported image content type"));
        try (InputStream input = file.getInputStream();
             ImageInputStream imageInput = ImageIO.createImageInputStream(input)) {
            if (imageInput == null) {
                throw new IllegalFileException("Image must be readable");
            }
            Iterator<ImageReader> readers = ImageIO.getImageReaders(imageInput);
            if (!readers.hasNext()) {
                throw new IllegalFileException("Image must be readable");
            }
            ImageReader reader = readers.next();
            try {
                if (!imageType.name().equalsIgnoreCase(reader.getFormatName())) {
                    throw new IllegalFileException("Image content does not match its content type");
                }
                reader.setInput(imageInput);
                reader.read(0);
            } finally {
                reader.dispose();
            }
        } catch (IOException e) {
            throw new IllegalFileException("Image must be readable");
        }
    }
}
