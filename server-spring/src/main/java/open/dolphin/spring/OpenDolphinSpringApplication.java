package open.dolphin.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for OpenDolphin server migration.
 * This is the entry point for the Spring Boot version of the server.
 */
@SpringBootApplication
public class OpenDolphinSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenDolphinSpringApplication.class, args);
    }
}
