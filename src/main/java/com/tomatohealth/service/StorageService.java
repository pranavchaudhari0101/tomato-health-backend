package com.tomatohealth.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tomatohealth.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Service for uploading images to Cloudinary with local storage fallback.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {

    private final Cloudinary cloudinary;

    private static final String UPLOAD_DIR = "uploads";

    /**
     * Upload an image file. Attempts Cloudinary first, falls back to local storage.
     *
     * @param file the multipart image file
     * @return URL of the uploaded image
     */
    public String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is empty or null");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("File must be an image");
        }

        // Try Cloudinary upload first
        try {
            return uploadToCloudinary(file);
        } catch (Exception e) {
            log.warn("Cloudinary upload failed, falling back to local storage: {}", e.getMessage());
            return uploadToLocal(file);
        }
    }

    /**
     * Upload file to Cloudinary.
     */
    @SuppressWarnings("unchecked")
    private String uploadToCloudinary(MultipartFile file) throws IOException {
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "tomato-health",
                        "resource_type", "image"
                ));
        String url = (String) uploadResult.get("secure_url");
        log.info("Image uploaded to Cloudinary: {}", url);
        return url;
    }

    /**
     * Upload file to local storage as a fallback.
     */
    private String uploadToLocal(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = StringUtils.cleanPath(
                    Objects.requireNonNull(file.getOriginalFilename()));
            String extension = originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
            String filename = UUID.randomUUID() + extension;

            Path targetLocation = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            String url = "/uploads/" + filename;
            log.info("Image uploaded to local storage: {}", url);
            return url;
        } catch (IOException e) {
            log.error("Failed to upload file to local storage", e);
            throw new BadRequestException("Failed to upload image: " + e.getMessage());
        }
    }
}
