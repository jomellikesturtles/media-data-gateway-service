# Media Data Gateway Service

Gateway service for accessing media streams and metadata. This application is built using **Spring Boot** and **gRPC**.

## Prerequisites

-   **Java 21** or later
-   **Gradle** (wrapper included)

## Getting Started

### 1. Build the Project

This project uses Protocol Buffers (Protobuf). You must generate the Java sources from the `.proto` files before compiling or running the application.

```bash
./gradlew generateProto
./gradlew build
```

### 2. Run the Application

You can run the application using the Spring Boot Gradle plugin:

```bash
./gradlew bootRun
```

The application will start the gRPC server on port **9090**.

## Configuration

The application is configured via `src/main/resources/application.yaml`.

*   **gRPC Server Port:** `9090`
*   **gRPC Reflection:** Enabled (allows clients like Postman or `grpcurl` to discover services)

## API Documentation

### gRPC Services

The service definitions are located in `src/main/proto/HelloService.proto`.

#### HelloService

*   **Service Name:** `hello.HelloService`
*   **Method:** `getHello`
    *   **Input:** `HelloRequest` (`firstName`: string, `lastName`: string)
    *   **Output:** `HelloResponse` (`greeting`: string)

### Testing with gRPC Client

Since gRPC Server Reflection is enabled, you can use tools like [Postman](https://learning.postman.com/docs/sending-requests/grpc/grpc-request-interface/) or [grpcurl](https://github.com/fullstorydev/grpcurl) to test the service.

**Example using `grpcurl`:**

```bash
# List available services
grpcurl -plaintext localhost:9090 list

# Call the HelloService
grpcurl -plaintext -d '{"firstName": "John", "lastName": "Doe"}' localhost:9090 hello.HelloService/getHello
```

## Project Structure

*   `src/main/proto`: Contains `.proto` service definitions.
*   `src/main/java`: Contains the Spring Boot application and implementation logic.
    *   `com.mdb.media_data_gateway_service.helloworld`: Implementation of the gRPC `HelloService`.