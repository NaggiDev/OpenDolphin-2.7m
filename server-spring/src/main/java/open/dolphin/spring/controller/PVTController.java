package open.dolphin.spring.controller;

import open.dolphin.infomodel.HealthInsuranceModel;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.spring.service.PVTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

/**
 * REST controller for patient visit operations.
 * Migrated from PVTResource (JAX-RS).
 */
@RestController
@RequestMapping("/pvt")
public class PVTController {

    @Autowired
    private PVTService pvtService;

    /**
     * Get patient visits by facility and parameters.
     * Supports both simple and complex parameter formats for backward compatibility.
     */
    @GetMapping("/{param}")
    public ResponseEntity<List<PatientVisitModel>> getPvt(
            @RequestParam("fid") String fid,
            @PathVariable("param") String param) {

        List<PatientVisitModel> result;

        String[] params = param.split(",");
        if (params.length == 4) {
            // Simple format: pvtDate,firstResult,appoDateFrom,appoDateTo
            String pvtDate = params[0];
            int firstResult = Integer.parseInt(params[1]);
            String appoDateFrom = params[2];
            String appoDateTo = params[3];
            result = pvtService.getPvt(fid, pvtDate, firstResult, appoDateFrom, appoDateTo);
        } else if (params.length == 6) {
            // Complex format: did,unassigned,pvtDate,firstResult,appoDateFrom,appoDateTo
            String did = params[0];
            String unassigned = params[1];
            String pvtDate = params[2];
            int firstResult = Integer.parseInt(params[3]);
            String appoDateFrom = params[4];
            String appoDateTo = params[5];
            result = pvtService.getPvt(fid, did, unassigned, pvtDate, firstResult, appoDateFrom, appoDateTo);
        } else {
            throw new IllegalArgumentException("Invalid parameter format");
        }

        return ResponseEntity.ok(result);
    }

    /**
     * Add new patient visit.
     */
    @PostMapping
    public ResponseEntity<Integer> addPvt(
            @RequestParam("fid") String fid,
            @RequestBody PatientVisitModel model) {

        // Set facility ID
        model.setFacilityId(fid);
        model.getPatientModel().setFacilityId(fid);

        // Set up health insurance relationships
        Collection<HealthInsuranceModel> insurances = model.getPatientModel().getHealthInsurances();
        if (insurances != null && !insurances.isEmpty()) {
            for (HealthInsuranceModel hm : insurances) {
                hm.setPatient(model.getPatientModel());
            }
        }

        int result = pvtService.addPvt(model);
        return ResponseEntity.ok(result);
    }

    /**
     * Update patient visit state.
     * Format: pvtPK,state
     */
    @PutMapping("/{param}")
    public ResponseEntity<Integer> updatePvtState(@PathVariable("param") String param) {
        String[] params = param.split(",");
        if (params.length < 2) {
            throw new IllegalArgumentException("Invalid parameter format for state update");
        }

        long pvtPK = Long.parseLong(params[0]);
        int state = Integer.parseInt(params[1]);

        int result = pvtService.updatePvtState(pvtPK, state);
        return ResponseEntity.ok(result);
    }

    /**
     * Update patient visit memo.
     * Format: pvtPK,memo (memo can be empty)
     */
    @PutMapping("/memo/{param}")
    public ResponseEntity<Integer> updateMemo(@PathVariable("param") String param) {
        String[] params = param.split(",", 2); // Split only on first comma to preserve memo content
        if (params.length < 1) {
            throw new IllegalArgumentException("Invalid parameter format for memo update");
        }

        long pvtPK = Long.parseLong(params[0]);
        String memo = (params.length > 1) ? params[1] : ""; // Handle empty memo

        int result = pvtService.updateMemo(pvtPK, memo);
        return ResponseEntity.ok(result);
    }

    /**
     * Delete patient visit.
     */
    @DeleteMapping("/{pvtPK}")
    public ResponseEntity<Void> deletePvt(
            @RequestParam("fid") String fid,
            @PathVariable("pvtPK") String pvtPKStr) {

        long pvtPK = Long.parseLong(pvtPKStr);
        int result = pvtService.removePvt(pvtPK, fid);

        if (result > 0) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
