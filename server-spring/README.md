# OpenDolphin Server Spring

This module contains the Spring Boot migration of the OpenDolphin server from Jakarta EE.

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL database (for production)

## Quick Start

### 1. Build the Application

```bash
# Build the entire project first (to build common module)
mvn clean install -DskipTests

# Or build just this module
cd server-spring
mvn clean compile
```

### 2. Run the Application

```bash
# Run with default configuration
mvn spring-boot:run

# Or run the JAR directly
java -jar target/opendolphin-server-spring-2.7.2.jar
```

The application will start on `http://localhost:8080`

### 3. Test the Application

#### Health Check
```bash
curl http://localhost:8080/actuator/health
```

#### Server Info Endpoint
```bash
curl -u admin:admin http://localhost:8080/server/info
```

#### Run Tests
```bash
mvn test
```

## Configuration

### Database Configuration

Update `src/main/resources/application.yml` with your PostgreSQL settings:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/your_database
    username: your_username
    password: your_password
```

### Security

Default credentials:
- Username: `admin`
- Password: `admin`

## API Endpoints

### Server Info
- **GET** `/server/info` - Get server information
- **Auth Required**: Yes

### Actuator Endpoints (No Auth Required)
- **GET** `/actuator/health` - Health check
- **GET** `/actuator/info` - Application info
- **GET** `/actuator/metrics` - Metrics

## Development

### Project Structure

```
server-spring/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── open/dolphin/spring/
│   │   │       ├── OpenDolphinSpringApplication.java    # Main application
│   │   │       ├── config/                               # Configuration classes
│   │   │       │   └── SecurityConfig.java
│   │   │       └── controller/                           # REST controllers
│   │   │           └── ServerInfoController.java
│   │   └── resources/
│   │       └── application.yml                           # Configuration
│   └── test/
│       └── java/
│           └── open/dolphin/spring/
│               ├── OpenDolphinSpringApplicationTests.java
│               └── controller/
│                   └── ServerInfoControllerTest.java
├── pom.xml
└── README.md
```

### Adding New Controllers

1. Create a new controller class in `controller/` package
2. Use `@RestController` and `@RequestMapping` annotations
3. Add appropriate security configuration if needed

### Testing

- Unit tests: Use `@SpringBootTest` for integration tests
- Controller tests: Use `@WebMvcTest` for isolated controller testing
- Database tests: Use H2 in-memory database for fast testing

## Migration Status

This is Phase 1 of the migration. The foundation is complete and ready for:

- ✅ Spring Boot setup
- ✅ Basic security configuration
- ✅ Database configuration
- ✅ Sample REST endpoint
- ✅ Testing framework

Next phases will include migrating the actual business logic from the Jakarta EE server.

## Troubleshooting

### Common Issues

1. **Port already in use**: Change the port in `application.yml`
   ```yaml
   server:
     port: 8081
   ```

2. **Database connection failed**: Verify PostgreSQL is running and credentials are correct

3. **Security issues**: Check that endpoints are properly secured in `SecurityConfig.java`

### Logs

Application logs are available at:
- Console output during development
- `logs/spring.log` in production (configurable)

## Contributing

When adding new features:

1. Follow Spring Boot best practices
2. Add appropriate tests
3. Update this README if needed
4. Ensure compatibility with the original API
