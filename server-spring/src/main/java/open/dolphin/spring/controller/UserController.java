package open.dolphin.spring.controller;

import open.dolphin.spring.model.entity.*;
import open.dolphin.spring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for user management operations.
 * Migrated from UserResource (JAX-RS).
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Get user by user ID.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserModel> getUser(@PathVariable("userId") String userId) {
        UserModel user = userService.getUser(userId);
        return ResponseEntity.ok(user);
    }

    /**
     * Get all users for the current user's facility.
     */
    @GetMapping
    public ResponseEntity<List<UserModel>> getAllUsers(@RequestParam("fid") String facilityId) {
        List<UserModel> users = userService.getAllUser(facilityId);
        return ResponseEntity.ok(users);
    }

    /**
     * Add new user.
     */
    @PostMapping
    public ResponseEntity<Integer> addUser(
            @RequestParam("fid") String facilityId,
            @RequestBody UserModel user) {

        // Set facility ID
        user.getFacilityModel().setFacilityId(facilityId);

        // Build role relationships
        List<RoleModel> roles = user.getRoles();
        if (roles != null) {
            for (RoleModel role : roles) {
                role.setUserModel(user);
            }
        }

        int result = userService.addUser(user);
        return ResponseEntity.ok(result);
    }

    /**
     * Update user information.
     */
    @PutMapping
    public ResponseEntity<Integer> updateUser(@RequestBody UserModel user) {
        // Build role relationships
        List<RoleModel> roles = user.getRoles();
        if (roles != null) {
            roles.forEach(role -> role.setUserModel(user));
        }

        int result = userService.updateUser(user);
        return ResponseEntity.ok(result);
    }

    /**
     * Delete user.
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") String userId) {
        int result = userService.removeUser(userId);
        if (result > 0) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update facility information.
     */
    @PutMapping("/facility")
    public ResponseEntity<Integer> updateFacility(@RequestBody UserModel user) {
        int result = userService.updateFacility(user);
        return ResponseEntity.ok(result);
    }

    /**
     * Authenticate user (for testing purposes).
     * In production, this would be handled by Spring Security.
     */
    @PostMapping("/authenticate")
    public ResponseEntity<Boolean> authenticate(
            @RequestParam("username") String username,
            @RequestParam("password") String password) {

        boolean authenticated = userService.authenticate(username, password);
        return ResponseEntity.ok(authenticated);
    }

    /**
     * Get current authenticated user information.
     */
    @GetMapping("/current")
    public ResponseEntity<String> getCurrentUser() {
        String currentUserId = userService.getCurrentUserId();
        if (currentUserId != null) {
            return ResponseEntity.ok(currentUserId);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Check if current user has a specific role.
     */
    @GetMapping("/hasRole/{role}")
    public ResponseEntity<Boolean> hasRole(@PathVariable("role") String role) {
        boolean hasRole = userService.hasRole(role);
        return ResponseEntity.ok(hasRole);
    }
}
