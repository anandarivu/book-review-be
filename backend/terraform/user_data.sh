#!/bin/bash
sudo apt-get update -y
sudo apt-get install -y openjdk-17-jdk
sudo apt-get install -y unzip
# Install Maven
sudo apt-get install -y maven
# Download and run the Spring Boot app (replace with your artifact)
cd /home/ubuntu
wget https://your-artifact-url/book-review-be.jar -O book-review-be.jar
nohup java -jar book-review-be.jar --server.port=8080 &
