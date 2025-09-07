package open.dolphin.spring.controller;

import open.dolphin.infomodel.LetterModule;
import open.dolphin.spring.service.LetterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for medical letter management.
 * Migrated from LetterResource (JAX-RS).
 */
@RestController
@RequestMapping("/letter")
public class LetterController {

    @Autowired
    private LetterService letterService;

    /**
     * Save or update a medical letter.
     * @param letter the letter module to save
     * @return the saved letter ID
     */
    @PostMapping
    public ResponseEntity<Long> saveOrUpdateLetter(@RequestBody LetterModule letter) {
        long letterId = letterService.saveOrUpdateLetter(letter);
        return ResponseEntity.ok(letterId);
    }

    /**
     * Get all letters for a specific karte.
     * @param karteId the karte ID
     * @return list of letter modules
     */
    @GetMapping("/list/{karteId}")
    public ResponseEntity<List<LetterModule>> getLetterList(@PathVariable("karteId") long karteId) {
        List<LetterModule> letters = letterService.getLetterList(karteId);
        return ResponseEntity.ok(letters);
    }

    /**
     * Get a single letter with all its components.
     * @param letterId the letter ID
     * @return the complete letter module
     */
    @GetMapping("/{letterId}")
    public ResponseEntity<LetterModule> getLetter(@PathVariable("letterId") long letterId) {
        LetterModule letter = letterService.getLetter(letterId);
        if (letter != null) {
            return ResponseEntity.ok(letter);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete a letter and all its components.
     * @param letterId the letter ID to delete
     * @return success status
     */
    @DeleteMapping("/{letterId}")
    public ResponseEntity<String> deleteLetter(@PathVariable("letterId") long letterId) {
        try {
            letterService.delete(letterId);
            return ResponseEntity.ok("Letter deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete letter: " + e.getMessage());
        }
    }

    /**
     * Get letter statistics for a karte.
     * @param karteId the karte ID
     * @return statistics map
     */
    @GetMapping("/stats/{karteId}")
    public ResponseEntity<java.util.Map<String, Object>> getLetterStats(@PathVariable("karteId") long karteId) {
        java.util.Map<String, Object> stats = letterService.getLetterStats(karteId);
        return ResponseEntity.ok(stats);
    }

    /**
     * Search letters by content (placeholder for future implementation).
     * @param karteId the karte ID
     * @param searchTerm the search term
     * @return list of matching letters
     */
    @GetMapping("/search/{karteId}")
    public ResponseEntity<List<LetterModule>> searchLetters(
            @PathVariable("karteId") long karteId,
            @RequestParam("term") String searchTerm) {

        // For now, return all letters (search functionality would need LetterText content access)
        List<LetterModule> letters = letterService.getLetterList(karteId);
        return ResponseEntity.ok(letters);
    }

    /**
     * Get recent letters for a karte.
     * @param karteId the karte ID
     * @param limit maximum number of letters to return
     * @return list of recent letters
     */
    @GetMapping("/recent/{karteId}")
    public ResponseEntity<List<LetterModule>> getRecentLetters(
            @PathVariable("karteId") long karteId,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {

        List<LetterModule> letters = letterService.getLetterList(karteId);

        // Return only the most recent letters up to the limit
        int endIndex = Math.min(limit, letters.size());
        List<LetterModule> recentLetters = letters.subList(0, endIndex);

        return ResponseEntity.ok(recentLetters);
    }

    /**
     * Get letter count for a karte.
     * @param karteId the karte ID
     * @return count of letters
     */
    @GetMapping("/count/{karteId}")
    public ResponseEntity<java.util.Map<String, Object>> getLetterCount(@PathVariable("karteId") long karteId) {
        List<LetterModule> letters = letterService.getLetterList(karteId);

        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("karteId", karteId);
        result.put("count", letters.size());

        return ResponseEntity.ok(result);
    }

    /**
     * Update an existing letter.
     * @param letterId the letter ID
     * @param letter the updated letter data
     * @return success status
     */
    @PutMapping("/{letterId}")
    public ResponseEntity<String> updateLetter(@PathVariable("letterId") long letterId, @RequestBody LetterModule letter) {
        try {
            // Set the ID from the path parameter
            letter.setId(letterId);

            // Save the updated letter
            long savedId = letterService.saveOrUpdateLetter(letter);

            if (savedId > 0) {
                return ResponseEntity.ok("Letter updated successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to update letter");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update letter: " + e.getMessage());
        }
    }

    /**
     * Create a new letter.
     * @param letter the letter to create
     * @return the created letter ID
     */
    @PostMapping("/create")
    public ResponseEntity<Long> createLetter(@RequestBody LetterModule letter) {
        try {
            // Ensure ID is not set for new letters
            letter.setId(0L);
            long letterId = letterService.saveOrUpdateLetter(letter);
            return ResponseEntity.ok(letterId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get letters by type (placeholder for future implementation).
     * @param karteId the karte ID
     * @param type the letter type
     * @return list of letters of the specified type
     */
    @GetMapping("/type/{karteId}")
    public ResponseEntity<List<LetterModule>> getLettersByType(
            @PathVariable("karteId") long karteId,
            @RequestParam("type") String type) {

        List<LetterModule> letters = letterService.getLetterList(karteId);

        // Filter by type if the property exists (placeholder implementation)
        // This would need to be implemented based on the actual LetterModule structure
        return ResponseEntity.ok(letters);
    }
}
