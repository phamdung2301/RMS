# --- Stage 1: Build source code ---
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
# Download dependencies trước để tăng tốc build lần sau
RUN mvn dependency:go-offline -B
COPY src ./src
COPY sql ./sql
# Build file jar bỏ qua các bài test để tiết kiệm RAM & thời gian
RUN mvn clean package -DskipTests

# --- Stage 2: Chạy ứng dụng ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/swp-0.0.1-SNAPSHOT.jar app.jar
COPY --from=build /app/sql ./sql
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

