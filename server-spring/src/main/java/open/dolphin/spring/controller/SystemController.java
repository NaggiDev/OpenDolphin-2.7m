package open.dolphin.spring.controller;

import open.dolphin.spring.model.entity.*;
import open.dolphin.spring.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.logging.Logger;

/**
 * REST controller for system operations and facility management.
 * Migrated from SystemResource (JAX-RS).
 */
@RestController
@RequestMapping("/dolphin")
public class SystemController {

    private static final Logger logger = Logger.getLogger(SystemController.class.getName());

    @Autowired
    private SystemService systemService;

    /**
     * Simple hello endpoint.
     */
    @GetMapping
    public ResponseEntity<String> helloDolphin() {
        return ResponseEntity.ok("Hello, Dolphin");
    }

    /**
     * Add facility admin user.
     */
    @PostMapping
    public ResponseEntity<String> addFacilityAdmin(@RequestBody UserModel user) {
        // Build role relationships
        List<RoleModel> roles = user.getRoles();
        if (roles != null) {
            for (RoleModel role : roles) {
                role.setUserModel(user);
            }
        }

        // Add facility admin
        // TODO: Handle AccountSummary return type
        // For now, return a simple success message
        try {
            // This would normally return AccountSummary
            systemService.addFacilityAdmin(user);
            return ResponseEntity.ok("Facility admin added successfully");
        } catch (Exception e) {
            logger.warning("Failed to add facility admin: " + e.getMessage());
            return ResponseEntity.badRequest().body("Failed to add facility admin: " + e.getMessage());
        }
    }

    /**
     * Get activity reports.
     * Format: year,month,count
     */
    @GetMapping("/activity/{param}")
    public ResponseEntity<List<ActivityModel>> getActivities(
            @RequestParam("fid") String facilityId,
            @PathVariable("param") String param) {

        // Parse parameters
        String[] params = param.split(",");
        int year = Integer.parseInt(params[0]);     // Starting year
        int month = Integer.parseInt(params[1]);    // Starting month
        int count = Integer.parseInt(params[2]);    // Number of past months

        ActivityModel[] array = new ActivityModel[count + 1]; // +1 for total

        // Calculate date ranges for each month
        GregorianCalendar gcFirst = new GregorianCalendar(year, month, 1);
        int numDays = gcFirst.getActualMaximum(Calendar.DAY_OF_MONTH);

        int index = array.length - 2;
        while (true) {
            GregorianCalendar gcLast = new GregorianCalendar(year, month, numDays, 23, 59, 59);
            ActivityModel am = systemService.countActivities(facilityId, gcFirst.getTime(), gcLast.getTime());
            array[index] = am;

            index--;
            if (index < 0) {
                break;
            }
            gcFirst.add(Calendar.MONTH, -1);
            year = gcFirst.get(Calendar.YEAR);
            month = gcFirst.get(Calendar.MONTH);
            numDays = gcFirst.getActualMaximum(Calendar.DAY_OF_MONTH);
        }

        // Add total activities
        ActivityModel total = systemService.countTotalActivities(facilityId);
        array[array.length - 1] = total;

        return ResponseEntity.ok(Arrays.asList(array));
    }

    /**
     * Check license for Cloud Zero.
     */
    @PostMapping("/license")
    public ResponseEntity<String> checkLicense(@RequestBody String uid) {
        try {
            Properties config = new Properties();
            StringBuilder sb = new StringBuilder();
            sb.append(System.getProperty("jboss.home.dir"));
            sb.append(File.separator);
            sb.append("license.properties");
            File f = new File(sb.toString());

            // Load license file
            try {
                FileInputStream fin = new FileInputStream(f);
                InputStreamReader isr = new InputStreamReader(fin, "UTF-8");
                config.load(isr);
                isr.close();
                fin.close();
            } catch (IOException ex) {
                logger.warning("License file read error");
                return ResponseEntity.ok("2"); // License file read error
            }

            // Check license limit
            String val = config.getProperty("license.max", "3");
            int max = Integer.parseInt(val);

            for (int i = 0; i < max; i++) {
                sb = new StringBuilder();
                sb.append("license.uid");
                sb.append(String.valueOf(i + 1));
                val = config.getProperty(sb.toString());

                if (val == null) {
                    // Add new license
                    config.setProperty(sb.toString(), uid);
                    try {
                        FileOutputStream fon = new FileOutputStream(f);
                        config.store(fon, "OpenDolphinZero License");
                        fon.close();
                    } catch (IOException ex) {
                        logger.warning("License file save error");
                        return ResponseEntity.ok("3"); // License file save error
                    }
                    logger.info("New license registered");
                    return ResponseEntity.ok("0"); // Success

                } else if (val.equals(uid)) {
                    logger.info("License already registered");
                    return ResponseEntity.ok("0"); // Already registered
                }
            }

            logger.warning("License authentication limit exceeded");
            return ResponseEntity.ok("4"); // Limit exceeded

        } catch (Exception e) {
            logger.severe("License check failed: " + e.getMessage());
            return ResponseEntity.ok("5"); // General error
        }
    }

    /**
     * Send Cloud Zero monthly activity reports.
     */
    @GetMapping("/cloudzero/sendmail")
    public ResponseEntity<Void> sendCloudZeroMail() {
        try {
            logger.info("Sending Cloud Zero mail");

            // Send monthly activities for previous month
            GregorianCalendar gc = new GregorianCalendar();
            gc.add(Calendar.MONTH, -1);
            int year = gc.get(Calendar.YEAR);
            int month = gc.get(Calendar.MONTH);

            systemService.sendMonthlyActivities(year, month);

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            logger.warning("Failed to send Cloud Zero mail: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get system information.
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getSystemInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("version", "Spring Boot Migration");
        info.put("status", "Running");
        info.put("timestamp", new Date());

        return ResponseEntity.ok(info);
    }
}
