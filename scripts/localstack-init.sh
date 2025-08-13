#!/bin/bash
echo "Initializing LocalStack AWS services..."

# Wait for LocalStack to be ready
until curl -s http://localhost:4566/health | grep -q '"s3": "available"'; do
    echo "Waiting for LocalStack S3 service to be ready..."
    sleep 2
done

# Create S3 bucket for image storage
echo "Creating S3 bucket: adobe-images"
awslocal s3 mb s3://adobe-images

# Create folder structure
echo "Creating S3 folder structure..."
awslocal s3api put-object --bucket adobe-images --key images/ --content-length 0
awslocal s3api put-object --bucket adobe-images --key processed/ --content-length 0

echo "LocalStack initialization completed successfully!"