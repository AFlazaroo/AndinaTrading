# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

This is **AndinaTrading** (also known as **BoskTrader**), a Spring Boot microservices-based trading platform. The system consists of 13 microservices that handle different aspects of a trading application including users, portfolios, orders, contracts, reports, and more.

## Architecture

**Microservices Pattern**: The application follows a distributed microservices architecture with:
- **Service Discovery**: Eureka Server (port 8761)
- **API Gateway**: Spring Cloud Gateway 
- **Configuration Server**: Spring Cloud Config Server (port 8888)
- **Individual Business Services**: 10 domain-specific microservices

### Service Map

| Service | Port | Database | Purpose |
|---------|------|----------|---------|
| microservice-eureka | 8761 | N/A | Service discovery server |
| microservice-config | 8888 | N/A | Configuration server |
| microservice-gateway | TBD | N/A | API Gateway |
| microservice-usuario | TBD | usuario | User management & authentication |
| microservice-contrato | 8093 | contrato | Contract management |
| microservice-orden | 8094 | orden | Order processing |
| microservice-portafolio | 8096 | portafolio | Portfolio management |
| microservice-consolidacion | 8097 | consolidacion | Data consolidation |
| microservice-reporte | 8098 | reporte | Report generation |
| microservice-empresa | TBD | empresa | Company management |
| microservice-transaccion | TBD | transaccion | Transaction processing |
| microservice-bolsa | TBD | bolsa | Stock exchange operations |

## Development Commands

### Running Individual Microservices
```bash
# Start a specific microservice (from root directory)
./mvnw spring-boot:run -pl microservice-<name>

# Example: Run the user service
./mvnw spring-boot:run -pl microservice-usuario
```

### Starting Infrastructure Services (Required Order)
1. **Configuration Server**: `./mvnw spring-boot:run -pl microservice-config`
2. **Service Discovery**: `./mvnw spring-boot:run -pl microservice-eureka` 
3. **API Gateway**: `./mvnw spring-boot:run -pl microservice-gateway`
4. **Business Services**: Start any combination of business microservices

### Testing
```bash
# Run tests for all modules
./mvnw test

# Run tests for specific microservice
./mvnw test -pl microservice-<name>

# Example: Test user service
./mvnw test -pl microservice-usuario
```

### Building
```bash
# Build all microservices
./mvnw clean package

# Build specific microservice
./mvnw clean package -pl microservice-<name>
```

## Technology Stack

- **Framework**: Spring Boot 3.4.4
- **Java Version**: JDK 17
- **Database**: MySQL 8.0+ (each microservice has its own database)
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Configuration Management**: Spring Cloud Config
- **Build Tool**: Maven (with wrapper)
- **Testing**: JUnit Jupiter, Mockito, Spring Boot Test
- **Security**: JWT tokens, Spring Security Crypto
- **Communication**: OpenFeign for inter-service communication
- **Additional**: Spring Boot DevTools, Lombok, Jackson

## Key Architectural Patterns

### Database Per Service
Each microservice maintains its own MySQL database with identical connection patterns:
- URL: `jdbc:mysql://localhost:3306/<service_name>`
- Credentials: `root/12345678` (development)
- DDL Strategy: `create` (development - recreates schemas)

### Service Registration & Discovery
All services register with Eureka at `http://localhost:8761/eureka/` and pull configuration from Config Server at `http://localhost:8888`.

### User Roles & Security
The system supports multiple user types:
- **Trader**: Regular trading users
- **Comisionista**: Commission agents 
- **Administrador**: System administrators
- **AreaLegal**: Legal department
- **JuntaDirectiva**: Board of directors

Authentication uses JWT tokens with OTP-based multi-factor authentication via email.

## Development Guidelines

### Service Communication
Use OpenFeign declarative clients for inter-service communication rather than RestTemplate.

### Configuration Management
External configuration is managed through Spring Cloud Config Server. Service-specific configs should be placed in the Config Server's repository.

### Database Management
In development, services use `ddl-auto: create` which recreates schemas on startup. Be cautious with data persistence during development.

### Logging
Services use SLF4J with Logback. The microservice-usuario shows examples of structured logging with LogManager.

### Validation
Use Jakarta Validation annotations (`@NotBlank`, `@Email`, etc.) for request validation with global exception handling.

## Common Troubleshooting

### Service Startup Issues
1. Ensure Config Server is running first
2. Verify Eureka Server is accessible
3. Check MySQL databases are created and accessible
4. Confirm no port conflicts between services

### Database Connection Issues
- Verify MySQL is running on localhost:3306
- Ensure databases exist for each service
- Check credentials match application.yml files

### Service Discovery Issues
- Confirm Eureka dashboard is accessible at http://localhost:8761
- Check service registration in Eureka console
- Verify network connectivity between services