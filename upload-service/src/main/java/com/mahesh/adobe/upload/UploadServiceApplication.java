package com.mahesh.adobe.upload;

import com.mahesh.adobe.upload.service.S3Service;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
@RestController
public class UploadServiceApplication {
    
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );
    
    private final S3Service s3Service;
    
    public UploadServiceApplication(S3Service s3Service) {
        this.s3Service = s3Service;
    }
    
    public static void main(String[] args) {
        SpringApplication.run(UploadServiceApplication.class, args);
    }
    
    @GetMapping("/hello")
    public String hello() {
        return "Hello from Adobe Upload Service with AWS S3!";
    }
    
    @GetMapping("/health")
    public String health() {
        return "Upload service is running with S3 integration!";
    }
    
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "Please select a file to upload");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Upload to S3
            String s3Key = s3Service.uploadFile(file);
            String fileUrl = s3Service.getFileUrl(s3Key);
            
            // Basic file info
            response.put("success", true);
            response.put("message", "File uploaded successfully to S3!");
            response.put("filename", file.getOriginalFilename());
            response.put("size", file.getSize());
            response.put("contentType", file.getContentType());
            response.put("s3Key", s3Key);
            response.put("fileUrl", fileUrl);
            response.put("uploadedAt", LocalDateTime.now().toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Upload failed: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @PostMapping("/upload/image")
    public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "Please select an image to upload");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Check if it's an image
            if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
                response.put("success", false);
                response.put("message", "Please upload a valid image file (JPEG, PNG, GIF, WebP)");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Extract image dimensions
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                response.put("success", false);
                response.put("message", "Invalid image file");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Upload to S3
            String s3Key = s3Service.uploadFile(file);
            String fileUrl = s3Service.getFileUrl(s3Key);
            
            // Create unique image ID
            String imageId = UUID.randomUUID().toString();
            
            // Return detailed image information
            response.put("success", true);
            response.put("message", "Image uploaded and stored in S3 successfully!");
            response.put("imageId", imageId);
            response.put("filename", file.getOriginalFilename());
            response.put("size", file.getSize());
            response.put("contentType", file.getContentType());
            response.put("width", image.getWidth());
            response.put("height", image.getHeight());
            response.put("aspectRatio", (double) image.getWidth() / image.getHeight());
            response.put("s3Key", s3Key);
            response.put("fileUrl", fileUrl);
            response.put("uploadedAt", LocalDateTime.now().toString());
            
            // Add computer vision analysis
            Map<String, Object> analysis = new HashMap<>();
            analysis.put("totalPixels", image.getWidth() * image.getHeight());
            analysis.put("colorModel", image.getColorModel().toString());
            analysis.put("hasAlpha", image.getColorModel().hasAlpha());
            analysis.put("imageType", getImageType(image));
            response.put("analysis", analysis);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Image processing failed: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    private String getImageType(BufferedImage image) {
        int type = image.getType();
        switch (type) {
            case BufferedImage.TYPE_INT_RGB: return "RGB";
            case BufferedImage.TYPE_INT_ARGB: return "ARGB";
            case BufferedImage.TYPE_INT_ARGB_PRE: return "ARGB_PRE";
            case BufferedImage.TYPE_BYTE_GRAY: return "GRAYSCALE";
            default: return "OTHER";
        }
    }
}