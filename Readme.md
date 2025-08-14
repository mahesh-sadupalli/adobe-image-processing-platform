# Adobe Image Processing Platform
> **Enterprise-grade microservices platform for advanced image processing with computer vision**

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Containerized-blue.svg)](https://www.docker.com/)
[![AWS](https://img.shields.io/badge/AWS-S3%20Integration-yellow.svg)](https://aws.amazon.com/s3/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

**Built by [Mahesh Sadupalli](mailto:mahesh.sadupalli@gmail.com) • Portfolio project for Adobe Application**

---

##  **Project Overview**

A production-ready, cloud-native image processing platform that demonstrates enterprise-level software engineering skills. Built specifically to showcase capabilities relevant to Adobe's Cloud Platform team, this project combines advanced computer vision algorithms with modern microservices architecture.

### **Key Highlights**
- **Advanced Computer Vision**: Edge detection, image filters, smart resizing
- **Microservices Architecture**: Spring Boot with containerized deployment
- **Cloud Integration**: AWS S3 storage with LocalStack simulation
- **Production Ready**: Docker Compose, Nginx load balancer, health monitoring
- **Professional UI**: Modern web interface with real-time processing

---

## **Architecture**

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Nginx LB      │────│  Upload Service │────│      AWS S3     │
│   Port 80       │    │    Port 8080    │    │   (LocalStack)  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                        │                        │
         │              ┌─────────────────┐               │
         │──────────────│Processing APIs  │───────────────│
         │              │  Computer Vision│               │
         │              └─────────────────┘               │
         │                        │                        │
         │              ┌─────────────────┐    ┌─────────────────┐
         └──────────────│ Metadata Store  │────│   PostgreSQL    │
                        │   Port 8083     │    │ + Redis Cache   │
                        └─────────────────┘    └─────────────────┘
```

---

## **Features**

### ** Core Functionality**
- **Multi-format Image Upload**: JPEG, PNG, GIF, WebP support
- **Real-time Processing**: Instant image transformations
- **Metadata Extraction**: Dimensions, color analysis, file properties
- **Cloud Storage**: Seamless AWS S3 integration
- **Batch Operations**: Multiple image processing

### ** Computer Vision Algorithms**
- **Edge Detection**: Sobel-like algorithm implementation
- **Image Filters**: Blur, sharpen, brightness adjustment
- **Smart Resizing**: High-quality scaling with anti-aliasing
- **Thumbnail Generation**: Aspect-ratio preserving thumbnails
- **Format Conversion**: Cross-format image transformation

### ** Enterprise Features**
- **Microservices Design**: Independent, scalable services
- **Health Monitoring**: Comprehensive service health checks
- **Load Balancing**: Nginx reverse proxy configuration
- **Error Handling**: Robust exception management
- **Security**: Non-root container execution, input validation

---

## **Technology Stack**

| Layer | Technologies |
|-------|-------------|
| **Backend** | Java 17, Spring Boot 3.2, Maven |
| **Computer Vision** | Java ImageIO, BufferedImage, Custom Algorithms |
| **Cloud Services** | AWS S3, LocalStack (development) |
| **Databases** | PostgreSQL, Redis |
| **Containerization** | Docker, Docker Compose |
| **Web Server** | Nginx (Load Balancer) |
| **Frontend** | HTML5, CSS3, JavaScript (Vanilla) |
| **Monitoring** | Spring Actuator, Health Checks |

---

## **Quick Start**

### **Prerequisites**
- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- Git

### **1. Clone & Setup**
```bash
git clone https://github.com/YourUsername/adobe-image-platform.git
cd adobe-image-platform
```

### **2. Build Application**
```bash
# Build JAR files
mvn clean package -DskipTests

# Start all services with Docker Compose
docker-compose up --build -d
```

### **3. Initialize AWS Services**
```bash
# Start LocalStack (separate terminal)
docker run --rm -it -p 4566:4566 localstack/localstack

# Create S3 bucket
curl -X PUT http://localhost:4566/adobe-images
```

### **4. Access Application**
- **Web Interface**: http://localhost/
- **API Documentation**: http://localhost:8080/
- **Health Status**: http://localhost/health

---

## **API Endpoints**

### **Core Upload API**
```bash
# Upload single image with metadata extraction
POST /upload/image
Content-Type: multipart/form-data

# Batch upload multiple images
POST /upload/images/batch
Content-Type: multipart/form-data
```

### **Advanced Processing APIs**
```bash
# Resize image to specific dimensions
POST /api/v1/process/resize?width=800&height=600

# Generate smart thumbnail (maintains aspect ratio)
POST /api/v1/process/thumbnail?size=200

# Apply blur filter with intensity control
POST /api/v1/process/filter/blur?intensity=2.0

# Computer vision: Edge detection
POST /api/v1/process/filter/edges

# Apply sharpen filter
POST /api/v1/process/filter/sharpen

# Convert to grayscale
POST /api/v1/process/filter/grayscale
```

---

## **Testing & Demo**

### **Upload Test Image**
```bash
curl -X POST -F "file=@sample-image.jpg" \
  http://localhost/upload/image
```

### **Process with Computer Vision**
```bash
# Edge detection (showcases CV expertise)
curl -X POST -F "file=@sample-image.jpg" \
  http://localhost/api/v1/process/filter/edges \
  --output edges-detected.jpg

# Smart thumbnail generation
curl -X POST -F "file=@sample-image.jpg" -F "size=150" \
  http://localhost/api/v1/process/thumbnail
```

### **Expected Response**
```json
{
  "success": true,
  "message": "Image uploaded and stored in S3 successfully!",
  "imageId": "uuid-here",
  "filename": "sample-image.jpg",
  "width": 1920,
  "height": 1080,
  "aspectRatio": 1.777,
  "s3Key": "images/uuid.jpg",
  "fileUrl": "http://localhost:4566/adobe-images/images/uuid.jpg",
  "analysis": {
    "totalPixels": 2073600,
    "hasAlpha": false,
    "imageType": "RGB"
  }
}
```

---

## **Web Interface Features**

### **Modern UI/UX**
- **Drag & Drop Upload**: Intuitive file selection
- **Real-time Preview**: Instant image display
- **Interactive Controls**: Sliders for filter intensity
- **Result Gallery**: Organized processed images
- **Download Links**: Direct access to results

### **Professional Design**
- **Adobe-Inspired Styling**: Clean, modern aesthetics
- **Responsive Layout**: Works on all devices
- **Loading Indicators**: User feedback during processing
- **Error Handling**: Graceful failure messages

---

## **Docker Deployment**

### **Services Overview**
```yaml
services:
  nginx:         # Load balancer (Port 80)
  upload-service: # Main application (Port 8080)
  postgres:      # Database (Port 5432)
  redis:         # Cache (Port 6379)
  localstack:    # AWS simulation (Port 4566)
```

### **Production Configuration**
- **Multi-stage builds** for optimized image sizes
- **Health checks** for all services
- **Non-root execution** for security
- **Resource limits** and restart policies
- **Volume persistence** for data

### **Monitoring & Observability**
```bash
# Check all services
docker-compose ps

# View service logs
docker-compose logs upload-service

# Monitor health
curl http://localhost/health
```

---

## **Enterprise Architecture Patterns**

### **Design Principles**
- **Microservices**: Independent, scalable services
- **Domain-Driven Design**: Clear service boundaries  
- **SOLID Principles**: Clean, maintainable code
- **Dependency Injection**: Testable architecture

### **Cloud-Native Features**
- **Containerization**: Docker multi-stage builds
- **Service Discovery**: Spring Cloud ready
- **Configuration Management**: Environment-based config
- **Circuit Breakers**: Fault tolerance ready

### **Security & Reliability**
- **Input Validation**: Comprehensive file validation
- **Error Handling**: Graceful failure handling
- **Resource Management**: Connection pooling
- **Rate Limiting**: API protection configured

---

## **Adobe-Specific Alignment**

This project directly demonstrates skills relevant to Adobe's Cloud Platform:

### **Core Competencies**
- **Java & Spring Boot**: Adobe's primary backend stack
- **Microservices Architecture**: Scalable, maintainable design
- **Computer Vision**: Relevant to Adobe's creative tools
- **Cloud Integration**: Enterprise infrastructure experience
- **Performance & Scale**: Production-grade considerations

### **Business Value**
- **Creative Workflow Integration**: Seamless image processing
- **Cloud-First Architecture**: Modern deployment patterns
- **Developer Experience**: Clean APIs and documentation
- **Operational Excellence**: Monitoring and reliability

---

## **Performance Metrics**

### **Processing Capabilities**
- **Image Sizes**: Up to 50MB per file
- **Concurrent Users**: Nginx load balancing ready
- **Processing Speed**: Sub-second for standard operations
- **Memory Efficiency**: Optimized JVM settings

### **Scalability Features**
- **Horizontal Scaling**: Service replication ready
- **Caching Strategy**: Redis integration
- **Database Optimization**: Connection pooling
- **CDN Ready**: S3 integration for asset delivery

---

## **Security Considerations**

- **Input Validation**: File type and size restrictions
- **Container Security**: Non-root user execution
- **Network Isolation**: Docker network segmentation
- **Error Handling**: No sensitive data in responses

---

## **Future Enhancements**

### **Advanced Features**
- [ ] **Machine Learning Integration**: TensorFlow image classification
- [ ] **Real-time WebSocket**: Live processing updates
- [ ] **API Gateway**: Centralized routing and authentication
- [ ] **Kubernetes Deployment**: Production orchestration
- [ ] **CI/CD Pipeline**: GitHub Actions automation

### **Monitoring & Observability**
- [ ] **Metrics Collection**: Prometheus integration
- [ ] **Distributed Tracing**: Request flow monitoring
- [ ] **Log Aggregation**: ELK stack integration
- [ ] **Performance Monitoring**: APM integration

---

## **Contributing**

This is a portfolio project showcasing enterprise Java development skills for Adobe application consideration.

### **Local Development**
```bash
# Run services individually for development
mvn spring-boot:run -pl upload-service

# Run tests
mvn test

# Build without Docker
mvn clean package
```

---

## 📝 **License**

MIT License - see [LICENSE](LICENSE) file for details.

---
