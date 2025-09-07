package open.dolphin.spring.controller;

import open.dolphin.spring.model.entity.*;
import open.dolphin.spring.service.StampService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * REST controller for stamp tree and stamp management operations.
 * Migrated from StampResource (JAX-RS).
 */
@RestController
@RequestMapping("/stamp")
public class StampController {

    @Autowired
    private StampService stampService;

    /**
     * Get user's stamp trees (personal and subscribed).
     */
    @GetMapping("/tree/{userPK}")
    public ResponseEntity<StampTreeHolder> getStampTree(@PathVariable("userPK") String userPK) {
        StampTreeHolder result = stampService.getTrees(Long.parseLong(userPK));
        return ResponseEntity.ok(result);
    }

    /**
     * Save/update personal stamp tree.
     */
    @PutMapping("/tree")
    public ResponseEntity<Long> putTree(@RequestBody StampTreeModel model) {
        long pk = stampService.putTree(model);
        return ResponseEntity.ok(pk);
    }

    /**
     * Synchronize stamp tree with versioning.
     */
    @PutMapping("/tree/sync")
    public ResponseEntity<String> syncTree(@RequestBody StampTreeModel model) {
        String pkAndVersion = stampService.syncTree(model);
        return ResponseEntity.ok(pkAndVersion);
    }

    /**
     * Force synchronize stamp tree (bypass versioning).
     */
    @PutMapping("/tree/forcesync")
    public ResponseEntity<Void> forceSyncTree(@RequestBody StampTreeModel model) {
        stampService.forceSyncTree(model);
        return ResponseEntity.ok().build();
    }

    /**
     * Update published tree.
     */
    @PutMapping("/published/tree")
    public ResponseEntity<String> putPublishedTree(@RequestBody StampTreeHolder holder) {
        String version = stampService.updatePublishedTree(holder);
        return ResponseEntity.ok(version);
    }

    /**
     * Cancel published tree.
     */
    @PutMapping("/published/cancel")
    public ResponseEntity<String> cancelPublishedTree(@RequestBody StampTreeModel model) {
        String version = stampService.cancelPublishedTree(model);
        return ResponseEntity.ok(version);
    }

    /**
     * Get published stamp trees (local and global).
     */
    @GetMapping("/published/tree")
    public ResponseEntity<List<PublishedTreeModel>> getPublishedTrees(@RequestParam("fid") String facilityId) {
        List<PublishedTreeModel> result = stampService.getPublishedTrees(facilityId);
        return ResponseEntity.ok(result);
    }

    /**
     * Subscribe to published trees.
     */
    @PutMapping("/subscribed/tree")
    public ResponseEntity<String> subscribeTrees(@RequestBody SubscribedTreeList subscriptions) {
        List<Long> result = stampService.subscribeTrees(subscriptions.getList());

        StringBuilder sb = new StringBuilder();
        for (Long id : result) {
            sb.append(id.toString());
            sb.append(",");
        }
        String pks = sb.length() > 0 ? sb.substring(0, sb.length() - 1) : "";
        return ResponseEntity.ok(pks);
    }

    /**
     * Unsubscribe from published trees.
     * Format: treeId1,userPK1,treeId2,userPK2,...
     */
    @DeleteMapping("/subscribed/tree/{idPks}")
    public ResponseEntity<Void> unsubscribeTrees(@PathVariable("idPks") String idPks) {
        String[] params = idPks.split(",");
        List<Long> list = new ArrayList<>();
        for (String param : params) {
            list.add(Long.parseLong(param));
        }

        stampService.unsubscribeTrees(list);
        return ResponseEntity.ok().build();
    }

    /**
     * Get single stamp by ID.
     */
    @GetMapping("/id/{stampId}")
    public ResponseEntity<StampModel> getStamp(@PathVariable("stampId") String stampId) {
        StampModel stamp = stampService.getStamp(stampId);
        if (stamp != null) {
            return ResponseEntity.ok(stamp);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get multiple stamps by IDs.
     * Format: id1,id2,id3,...
     */
    @GetMapping("/list/{stampIds}")
    public ResponseEntity<List<StampModel>> getStamps(@PathVariable("stampIds") String stampIds) {
        String[] params = stampIds.split(",");
        List<String> stampIdList = Arrays.asList(params);

        List<StampModel> result = stampService.getStamp(stampIdList);
        return ResponseEntity.ok(result);
    }

    /**
     * Save single stamp.
     */
    @PutMapping("/id")
    public ResponseEntity<String> putStamp(@RequestBody StampModel stamp) {
        String stampId = stampService.putStamp(stamp);
        return ResponseEntity.ok(stampId);
    }

    /**
     * Save multiple stamps.
     */
    @PutMapping("/list")
    public ResponseEntity<String> putStamps(@RequestBody StampList stampList) {
        List<String> stampIds = stampService.putStamp(stampList.getList());

        StringBuilder sb = new StringBuilder();
        for (String stampId : stampIds) {
            sb.append(stampId);
            sb.append(",");
        }
        String result = sb.length() > 0 ? sb.substring(0, sb.length() - 1) : "";
        return ResponseEntity.ok(result);
    }

    /**
     * Delete single stamp.
     */
    @DeleteMapping("/id/{stampId}")
    public ResponseEntity<Void> deleteStamp(@PathVariable("stampId") String stampId) {
        int count = stampService.removeStamp(stampId);
        if (count > 0) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete multiple stamps.
     * Format: id1,id2,id3,...
     */
    @DeleteMapping("/list/{stampIds}")
    public ResponseEntity<Void> deleteStamps(@PathVariable("stampIds") String stampIds) {
        String[] params = stampIds.split(",");
        List<String> stampIdList = Arrays.asList(params);

        stampService.removeStamp(stampIdList);
        return ResponseEntity.ok().build();
    }

    /**
     * Get stamp tree statistics for a user.
     */
    @GetMapping("/tree/stats/{userPK}")
    public ResponseEntity<Map<String, Object>> getStampTreeStats(@PathVariable("userPK") String userPK) {
        StampTreeHolder trees = stampService.getTrees(Long.parseLong(userPK));

        Map<String, Object> stats = new HashMap<>();
        stats.put("hasPersonalTree", trees.getPersonalTree() != null);
        stats.put("subscribedTreeCount", trees.getSubscribedList() != null ? trees.getSubscribedList().size() : 0);
        stats.put("userPK", userPK);

        return ResponseEntity.ok(stats);
    }

    /**
     * Get published tree statistics for a facility.
     */
    @GetMapping("/published/stats")
    public ResponseEntity<Map<String, Object>> getPublishedTreeStats(@RequestParam("fid") String facilityId) {
        List<PublishedTreeModel> publishedTrees = stampService.getPublishedTrees(facilityId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPublishedTrees", publishedTrees.size());
        stats.put("facilityId", facilityId);

        // Count local vs global trees
        long localCount = publishedTrees.stream()
                .filter(tree -> facilityId.equals(tree.getPublishType()))
                .count();
        long globalCount = publishedTrees.size() - localCount;

        stats.put("localTrees", localCount);
        stats.put("globalTrees", globalCount);

        return ResponseEntity.ok(stats);
    }
}
