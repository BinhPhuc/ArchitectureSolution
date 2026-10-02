package com.architecture.solution.util;

import com.architecture.solution.exception.IllegalFileException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {
    public static void validatePdf(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalFileException("CV must be a non-empty PDF file");
        }
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            if (document.getNumberOfPages() < 1) {
                throw new IllegalFileException("CV PDF must contain at least one page");
            }
        } catch (IOException e) {
            throw new IllegalFileException("CV must be a readable PDF file");
        }
    }

    public static File convertFromMultipartToFile(MultipartFile multipartFile) throws IOException {
        File convFile = new File(multipartFile.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(multipartFile.getBytes());
        fos.close();
        return convFile;
    }
}
