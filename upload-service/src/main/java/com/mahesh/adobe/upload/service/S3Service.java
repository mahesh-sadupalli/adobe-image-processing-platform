package com.mahesh.adobe.upload.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3Service {
    
    private final S3Client s3Client;
    
    @Value("${aws.s3.bucket-name}")
    private String bucketName;
    
    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }
    
    public String uploadFile(MultipartFile file) throws IOException {
        // Generate unique file name
        String fileName = generateFileName(file.getOriginalFilename());
        
        // Create S3 upload request
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();
        
        // Upload file
        PutObjectResponse response = s3Client.putObject(putObjectRequest, 
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        
        return fileName; // Return the S3 key
    }
    
    public String getFileUrl(String fileName) {
        // For LocalStack, construct the URL manually
        return String.format("http://localhost:4566/%s/%s", bucketName, fileName);
    }
    
    private String generateFileName(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return "images/" + UUID.randomUUID().toString() + extension;
    }
}