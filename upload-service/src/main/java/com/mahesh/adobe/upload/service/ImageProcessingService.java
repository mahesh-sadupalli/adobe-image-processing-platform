package com.mahesh.adobe.upload.service;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ImageProcessingService {
    
    /**
     * Resize image to specified dimensions
     */
    public BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image scaledImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImage.createGraphics();
        
        // High quality rendering
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.drawImage(scaledImage, 0, 0, null);
        g2d.dispose();
        
        return resizedImage;
    }
    
    /**
     * Create thumbnail maintaining aspect ratio
     */
    public BufferedImage createThumbnail(BufferedImage originalImage, int maxSize) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        
        // Calculate new dimensions maintaining aspect ratio
        double aspectRatio = (double) width / height;
        int newWidth, newHeight;
        
        if (width > height) {
            newWidth = maxSize;
            newHeight = (int) (maxSize / aspectRatio);
        } else {
            newHeight = maxSize;
            newWidth = (int) (maxSize * aspectRatio);
        }
        
        return resizeImage(originalImage, newWidth, newHeight);
    }
    
    /**
     * Apply blur filter
     */
    public BufferedImage applyBlur(BufferedImage originalImage, float intensity) {
        // Create blur kernel
        int kernelSize = Math.max(3, (int) (intensity * 10));
        if (kernelSize % 2 == 0) kernelSize++; // Ensure odd size
        
        float weight = 1.0f / (kernelSize * kernelSize);
        float[] blurKernel = new float[kernelSize * kernelSize];
        for (int i = 0; i < blurKernel.length; i++) {
            blurKernel[i] = weight;
        }
        
        Kernel kernel = new Kernel(kernelSize, kernelSize, blurKernel);
        ConvolveOp blurOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        
        return blurOp.filter(originalImage, null);
    }
    
    /**
     * Apply sharpen filter
     */
    public BufferedImage applySharpen(BufferedImage originalImage) {
        // Sharpen kernel
        float[] sharpenKernel = {
            0.0f, -1.0f, 0.0f,
            -1.0f, 5.0f, -1.0f,
            0.0f, -1.0f, 0.0f
        };
        
        Kernel kernel = new Kernel(3, 3, sharpenKernel);
        ConvolveOp sharpenOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        
        return sharpenOp.filter(originalImage, null);
    }
    
    /**
     * Adjust brightness
     */
    public BufferedImage adjustBrightness(BufferedImage originalImage, float factor) {
        BufferedImage brightenedImage = new BufferedImage(
            originalImage.getWidth(), 
            originalImage.getHeight(), 
            originalImage.getType()
        );
        
        Graphics2D g2d = brightenedImage.createGraphics();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, factor));
        g2d.drawImage(originalImage, 0, 0, null);
        g2d.dispose();
        
        return brightenedImage;
    }
    
    /**
     * Convert image to grayscale
     */
    public BufferedImage convertToGrayscale(BufferedImage originalImage) {
        BufferedImage grayscaleImage = new BufferedImage(
            originalImage.getWidth(), 
            originalImage.getHeight(), 
            BufferedImage.TYPE_BYTE_GRAY
        );
        
        Graphics2D g2d = grayscaleImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, null);
        g2d.dispose();
        
        return grayscaleImage;
    }
    
    /**
     * Simple edge detection (Sobel-like)
     */
    public BufferedImage detectEdges(BufferedImage originalImage) {
        // First convert to grayscale
        BufferedImage grayImage = convertToGrayscale(originalImage);
        
        // Edge detection kernel (simplified Sobel)
        float[] edgeKernel = {
            -1.0f, -1.0f, -1.0f,
            -1.0f,  8.0f, -1.0f,
            -1.0f, -1.0f, -1.0f
        };
        
        Kernel kernel = new Kernel(3, 3, edgeKernel);
        ConvolveOp edgeOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        
        return edgeOp.filter(grayImage, null);
    }
    
    /**
     * Convert BufferedImage to byte array
     */
    public byte[] imageToByteArray(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, format, baos);
        return baos.toByteArray();
    }
    
    /**
     * Convert byte array to BufferedImage
     */
    public BufferedImage byteArrayToImage(byte[] imageData) throws IOException {
        return ImageIO.read(new ByteArrayInputStream(imageData));
    }
}