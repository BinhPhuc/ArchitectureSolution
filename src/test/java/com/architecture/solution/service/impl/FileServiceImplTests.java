package com.architecture.solution.service.impl;

import com.architecture.solution.constant.BucketConstant;
import com.architecture.solution.entity.File;
import com.architecture.solution.entity.Seller;
import com.architecture.solution.entity.User;
import com.architecture.solution.exception.IllegalFileException;
import com.architecture.solution.infrastructure.s3.initializer.BucketInitializer;
import com.architecture.solution.infrastructure.s3.service.S3BucketService;
import com.architecture.solution.infrastructure.s3.service.S3ObjectService;
import com.architecture.solution.repository.FileRepository;
import com.architecture.solution.repository.SellerRepository;
import com.architecture.solution.security.CustomUserDetails;
import com.architecture.solution.util.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceImplTests {
    @Mock
    private S3ObjectService s3ObjectService;
    @Mock
    private FileRepository fileRepository;
    @Mock
    private SellerRepository sellerRepository;
    @InjectMocks
    private FileServiceImpl fileService;

    @BeforeEach
    void authenticate() {
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_SELLER"));
        var principal = new CustomUserDetails(User.builder().id("seller-id").username("seller").build(), authorities);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, authorities));
    }

    @AfterEach
    void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void uploadsImageAndPersistsSellerOwnedMetadata() throws Exception {
        var file = image();
        when(sellerRepository.findById("seller-id")).thenReturn(Optional.of(Seller.builder().userId("seller-id").build()));
        when(fileRepository.save(any(File.class))).thenAnswer(invocation -> {
            File savedFile = invocation.getArgument(0);
            savedFile.setId("file-id");
            return savedFile;
        });

        var response = fileService.uploadItemImage(file);

        var metadata = ArgumentCaptor.forClass(File.class);
        verify(fileRepository).save(metadata.capture());
        assertEquals("seller-id", metadata.getValue().getOwnerId());
        assertEquals("file-id", response.getId());
        assertEquals("image/png", metadata.getValue().getContentType());
        assertEquals(file.getSize(), metadata.getValue().getSizeBytes());
        assertTrue(response.getObjectKey().startsWith("seller-id/"));
        verify(s3ObjectService).putObject(BucketConstant.ITEM_IMAGE_BUCKET, response.getObjectKey(), file);
    }

    @Test
    void rejectsMissingSellerBeforeUploading() throws Exception {
        var file = image();
        when(sellerRepository.findById("seller-id")).thenReturn(Optional.empty());
        assertThrows(AccessDeniedException.class, () -> fileService.uploadItemImage(file));
        verifyNoInteractions(s3ObjectService, fileRepository);
    }

    @Test
    void rejectsInvalidImageBeforeUploading() {
        when(sellerRepository.findById("seller-id")).thenReturn(Optional.of(Seller.builder().userId("seller-id").build()));
        var invalid = new MockMultipartFile("file", "image.png", "image/png", "invalid".getBytes());
        assertThrows(IllegalFileException.class, () -> fileService.uploadItemImage(invalid));
        verifyNoInteractions(s3ObjectService, fileRepository);
    }

    @Test
    void rejectsEmptyUnsupportedAndMismatchedImages() throws Exception {
        assertThrows(IllegalFileException.class, () -> FileUtils.validateImage(null));
        assertThrows(IllegalFileException.class, () -> FileUtils.validateImage(
                new MockMultipartFile("file", "empty.png", "image/png", new byte[0])));
        assertThrows(IllegalFileException.class, () -> FileUtils.validateImage(
                new MockMultipartFile("file", "image.svg", "image/svg+xml", "<svg/>".getBytes())));
        var mismatched = new MockMultipartFile("file", "image.jpg", "image/jpeg", image().getBytes());
        assertThrows(IllegalFileException.class, () -> FileUtils.validateImage(mismatched));
    }

    @Test
    void acceptsReadableJpegImage() throws Exception {
        var output = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB), "jpeg", output);
        var file = new MockMultipartFile("file", "item.jpg", "image/jpeg", output.toByteArray());
        assertDoesNotThrow(() -> FileUtils.validateImage(file));
    }

    @Test
    void initializesItemImageBucket() throws Exception {
        S3BucketService buckets = mock(S3BucketService.class);
        new BucketInitializer(buckets).run(null);
        verify(buckets).createBucket(BucketConstant.ITEM_IMAGE_BUCKET);
    }

    private MockMultipartFile image() throws Exception {
        var output = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB), "png", output);
        return new MockMultipartFile("file", "item.png", "image/png", output.toByteArray());
    }
}
