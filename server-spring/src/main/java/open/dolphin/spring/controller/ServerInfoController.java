package open.dolphin.spring.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for server information.
 * This is the first migrated controller from the Jakarta EE version.
 */
@RestController
@RequestMapping("/server")
public class ServerInfoController {

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getServerInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("server", "OpenDolphin Spring Boot Server");
        info.put("version", "2.7.2-spring");
        info.put("status", "running");
        info.put("timestamp", LocalDateTime.now().toString());
        info.put("framework", "Spring Boot 3.2.0");
        info.put("java", System.getProperty("java.version"));

        return ResponseEntity.ok(info);
    }
}
