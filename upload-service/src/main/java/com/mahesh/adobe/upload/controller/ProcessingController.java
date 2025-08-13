package com.mahesh.adobe.upload.controller;

import com.mahesh.adobe.upload.service.ImageProcessingService;
import com.mahesh.adobe.upload.service.S3Service;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/process")
public class ProcessingController {
    
    private final ImageProcessingService imageProcessingService;
    private final S3Service s3Service;
    
    public ProcessingController(ImageProcessingService imageProcessingService, S3Service s3Service) {
        this.imageProcessingService = imageProcessingService;
        this.s3Service = s3Service;
    }
    
    @PostMapping("/resize")
    public ResponseEntity<Map<String, Object>> resizeImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("width") int width,
            @RequestParam("height") int height) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            if (originalImage == null) {
                response.put("success", false);
                response.put("message", "Invalid image file");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Process image
            BufferedImage resizedImage = imageProcessingService.resizeImage(originalImage, width, height);
            
            // Convert to byte array and upload to S3
            byte[] imageBytes = imageProcessingService.imageToByteArray(resizedImage, "jpg");
            
            // Create mock multipart file for S3 upload
            String processedFileName = "processed_resize_" + UUID.randomUUID().toString() + ".jpg";
            
            response.put("success", true);
            response.put("message", "Image resized successfully!");
            response.put("originalSize", originalImage.getWidth() + "x" + originalImage.getHeight());
            response.put("newSize", width + "x" + height);
            response.put("processedAt", LocalDateTime.now().toString());
            response.put("operation", "resize");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Processing failed: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @PostMapping("/thumbnail")
    public ResponseEntity<Map<String, Object>> createThumbnail(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "size", defaultValue = "200") int size) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            if (originalImage == null) {
                response.put("success", false);
                response.put("message", "Invalid image file");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Create thumbnail maintaining aspect ratio
            BufferedImage thumbnail = imageProcessingService.createThumbnail(originalImage, size);
            
            response.put("success", true);
            response.put("message", "Thumbnail created successfully!");
            response.put("originalSize", originalImage.getWidth() + "x" + originalImage.getHeight());
            response.put("thumbnailSize", thumbnail.getWidth() + "x" + thumbnail.getHeight());
            response.put("maxSize", size);
            response.put("aspectRatioMaintained", true);
            response.put("processedAt", LocalDateTime.now().toString());
            response.put("operation", "thumbnail");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Thumbnail creation failed: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @PostMapping("/filter/blur")
    public ResponseEntity<byte[]> applyBlurFilter(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "intensity", defaultValue = "1.0") float intensity) {
        
        try {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            if (originalImage == null) {
                return ResponseEntity.badRequest().build();
            }
            
            // Apply blur filter
            BufferedImage blurredImage = imageProcessingService.applyBlur(originalImage, intensity);
            
            // Convert to byte array and return as image
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(blurredImage, "jpg", baos);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.set("X-Processing-Operation", "blur");
            headers.set("X-Blur-Intensity", String.valueOf(intensity));
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(baos.toByteArray());
                    
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/filter/sharpen")
    public ResponseEntity<byte[]> applySharpenFilter(@RequestParam("file") MultipartFile file) {
        
        try {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            if (originalImage == null) {
                return ResponseEntity.badRequest().build();
            }
            
            // Apply sharpen filter
            BufferedImage sharpenedImage = imageProcessingService.applySharpen(originalImage);
            
            // Convert to byte array and return as image
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(sharpenedImage, "jpg", baos);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.set("X-Processing-Operation", "sharpen");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(baos.toByteArray());
                    
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/filter/edges")
    public ResponseEntity<byte[]> detectEdges(@RequestParam("file") MultipartFile file) {
        
        try {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            if (originalImage == null) {
                return ResponseEntity.badRequest().build();
            }
            
            // Apply edge detection (showcasing your CV background!)
            BufferedImage edgeImage = imageProcessingService.detectEdges(originalImage);
            
            // Convert to byte array and return as image
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(edgeImage, "jpg", baos);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.set("X-Processing-Operation", "edge-detection");
            headers.set("X-CV-Algorithm", "sobel-like");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(baos.toByteArray());
                    
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/filter/grayscale")
    public ResponseEntity<byte[]> convertToGrayscale(@RequestParam("file") MultipartFile file) {
        
        try {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            if (originalImage == null) {
                return ResponseEntity.badRequest().build();
            }
            
            // Convert to grayscale
            BufferedImage grayscaleImage = imageProcessingService.convertToGrayscale(originalImage);
            
            // Convert to byte array and return as image
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(grayscaleImage, "jpg", baos);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.set("X-Processing-Operation", "grayscale");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(baos.toByteArray());
                    
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}