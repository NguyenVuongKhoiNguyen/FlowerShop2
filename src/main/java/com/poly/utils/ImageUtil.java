package com.poly.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class ImageUtil {

    // Change this to your actual folder path
	private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/src/main/resources/static/images/";
	
    public static String saveImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        
        if (file.getSize() > 5 * 1024 * 1024) { // 5MB
            throw new RuntimeException("File too large");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Invalid file type");
        }
        
        try {
            // Get original filename
            String originalFilename = file.getOriginalFilename();

            // Extract extension (e.g. .jpg, .png)
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // Generate unique filename
            String newFileName = UUID.randomUUID().toString() + extension;

            // Create directory if not exists
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Save file
            File destination = new File(UPLOAD_DIR + newFileName);
            file.transferTo(destination);

            return newFileName;

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save image");
        }
    }
    public static void deleteImage(String fileName) {
        if (fileName == null || fileName.isBlank()) return;
        
        File file = new File(UPLOAD_DIR + fileName);
        if (file.exists()) {
            file.delete();
        }
    }
}
