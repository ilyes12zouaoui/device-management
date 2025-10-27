# Device Management Spring Boot Project

This project is a Spring Boot application for managing devices, including CRUD operations, validation, and exception handling. It is designed using best practices for configuration, mapping, logging and testing.

## Main REST API Features

- Create a new device.
- get a device by id.
- Update a created device.
- Patch (partially update) a created device.
- delete a device by id
- find all device or filter by name or brand with pagination.

## Business rules

- Creation time cannot be updated after creation.
- Creation time must be between now and 6 months in the past.
- Name and brand properties cannot be updated if the device status is `IN_USE`.
- Devices with status `IN_USE` cannot be deleted.
- If a device is `IN_USE`, but in the update request you set its status to another value (e\.g\., `AVAILABLE`), you can update the name and brand in the same request.

## Production Ready Features

| Feature | Description |
| :--- | :--- |
| **Standardized Error Handling** | Implements a custom standard error response structure across all REST API exceptions, ensuring clients receive predictable and informative error payloads regardless of the underlying issue. |
| **Business Logic Validation** | Custom exceptions are utilized to enforce business rules and prevent invalid state transitions (e.g., attempting to update or delete a resource while its status is "in use"). |
| **Automatic Schema Management** | Flyway manages and **automatically migrates the database schema on startup**. This ensures version control, consistency across environments, and safe deployment. |
| **Transparent Execution Logging (AOP)** | Leverages Aspect-Oriented Programming (AOP) to provide non-intrusive logging of method execution, capturing request start and completion times. |
| **API Documentation** | All API endpoints are thoroughly documented and accessible via the OpenAPI UI (Swagger-UI), simplifying client integration and manual testing. |
| **Request Traceability** | A correlation ID is used to uniquely identify and trace individual requests through the entire application stack, significantly aiding in debugging and monitoring in distributed environments. |
| **Comprehensive Testing** | Includes a full suite of Unit and Integration Tests that cover all core business functionalities, ensuring code quality and preventing regressions. |

## Project Structure

- `src/main/java/de/ilyes/device/` - Main application source code
- `src/main/resources/application.yml` - Main configuration file
- `src/main/resources/db/migration/` - Flyway migration scripts
- `open_api.yml` - OpenAPI specification for the API
- `Dockerfile` - Docker image definition
- `docker-compose.yml` - Multi-container orchestration
- `pom.xml` - Maven build configuration

## OpenAPI Documentation

The API is fully documented using OpenAPI. The specification is available in the `open_api.yml` file.
When the application is running, you can access the interactive OpenAPI UI at:

```
http://localhost:8080/api/open-api-ui.html
```

This interface allows you to explore and test all available endpoints.

## How to Start Locally

### Prerequisites

- Java 21 or higher
- Maven 3.9+
- Docker (optional, for containerized setup)
- PostgreSQL database (required for local setup)

### Running with Maven

1. Ensure you have a PostgreSQL database running and configure the `application.properties` file to connect to it.
   - Alternatively, you can run only the `device_db` service from the `docker-compose.yml` file using the following command:
     ```sh
     docker-compose up device_db
     ```
     This will start a PostgreSQL database exposed on port `5433` locally. The application is pre-configured to connect to this database.
2. Build the project:
   ```sh
   ./mvnw clean install
   ```
3. Start the application:
   ```sh
   ./mvnw spring-boot:run
   ```
4. The application will start on `http://localhost:8080`.

### Running Tests

To execute all unit and integration tests, use the following Maven command in your terminal:

```sh
./mvnw test
```

### Running with Docker Compose

1. Build and start all services:
   ```sh
   docker-compose up --build
   ```
2. The application and its dependencies (e.g., database) will be started in containers.
3. Access the OpenAPI UI at `http://localhost:8080/api/open-api-ui.html`.