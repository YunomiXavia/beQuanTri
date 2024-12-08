package com.example.beQuanTri.service.image;

import com.example.beQuanTri.dto.storage.ImageInfo;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Slf4j
@Service
public class ImageService {
    @Value("${app.image.storage.path}")
    private String imageStoragePath;

    /**
     * Stores the original image and creates smaller versions in separate directories.
     *
     * @param productCode the product code for storing images
     * @param file        the uploaded image file
     * @return an ImageInfo object containing the names and URL paths of the images
     * @throws IOException if an error occurs while saving images
     */
    public ImageInfo saveProductImages(String productCode, MultipartFile file) throws IOException {
        log.info("In Method saveProductImages");

        if (productCode == null || productCode.isEmpty()) {
            throw new IllegalArgumentException("Product code must not be null or empty");
        }

        // Create the product storage directory if it does not exist
        String productDirPath = imageStoragePath + File.separator + productCode;
        File productDir = new File(productDirPath);
        if (!productDir.exists()) {
            boolean dirsCreated = productDir.mkdirs();
            if (!dirsCreated) {
                throw new IOException("Failed to create directories for product images");
            }
            log.info("Created directory: {}", productDirPath);
        }

        String originalFileName = "original_" + file.getOriginalFilename();
        String smallFileName = "small_" + file.getOriginalFilename();
        String tinyFileName = "tiny_" + file.getOriginalFilename();

        // Save the original image
        File originalFile = new File(productDirPath + File.separator + "original" + File.separator + originalFileName);
        createDirectoryIfNotExists(originalFile.getParentFile());
        file.transferTo(originalFile);
        log.info("Saved original image: {}", originalFile.getAbsolutePath());

        // Create small image
        File smallFile = new File(productDirPath + File.separator + "small" + File.separator + smallFileName);
        createDirectoryIfNotExists(smallFile.getParentFile());
        Thumbnails.of(originalFile)
                .size(200, 200)
                .toFile(smallFile);
        log.info("Saved small image: {}", smallFile.getAbsolutePath());

        // Create tiny image
        File tinyFile = new File(productDirPath + File.separator + "tiny" + File.separator + tinyFileName);
        createDirectoryIfNotExists(tinyFile.getParentFile());
        Thumbnails.of(originalFile)
                .size(50, 50)
                .toFile(tinyFile);
        log.info("Saved tiny image: {}", tinyFile.getAbsolutePath());

        // Create relative URLs for the images
        String baseUrl = "/products/" + productCode + "/";
        String originalUrl = baseUrl + "original/" + originalFileName;
        String smallUrl = baseUrl + "small/" + smallFileName;
        String tinyUrl = baseUrl + "tiny/" + tinyFileName;

        return new ImageInfo(originalFileName, smallFileName, tinyFileName, originalUrl, smallUrl, tinyUrl);
    }

    /**
     * Creates a directory if it does not exist.
     *
     * @param directory the directory to create
     * @throws IOException if the directory cannot be created
     */
    private void createDirectoryIfNotExists(File directory) throws IOException {
        if (!directory.exists()) {
            boolean dirsCreated = directory.mkdirs();
            if (!dirsCreated) {
                throw new IOException("Failed to create directory: " + directory.getAbsolutePath());
            }
            log.info("Created directory: {}", directory.getAbsolutePath());
        }
    }

    /**
     * Deletes the product's image files.
     *
     * @param productCode the product code
     * @param imageNames  the names of the image files to delete
     */
    public void deleteProductImages(String productCode, String[] imageNames) {
        log.info("In Method deleteProductImages");

        if (productCode == null || productCode.isEmpty()) {
            log.warn("Product code is null or empty. Skipping image deletion.");
            return;
        }

        String productDirPath = imageStoragePath + File.separator + productCode;
        for (String imageName : imageNames) {
            // Determine the image size based on the prefix in the file name
            String size = "";
            if (imageName.startsWith("original_")) {
                size = "original";
            } else if (imageName.startsWith("small_")) {
                size = "small";
            } else if (imageName.startsWith("tiny_")) {
                size = "tiny";
            }

            if (!size.isEmpty()) {
                File file = new File(productDirPath + File.separator + size + File.separator + imageName);
                if (file.exists()) {
                    boolean deleted = file.delete();
                    if (deleted) {
                        log.info("Deleted image: {}", file.getAbsolutePath());
                    } else {
                        log.warn("Failed to delete image: {}", file.getAbsolutePath());
                    }
                }
            }
        }
    }
}
