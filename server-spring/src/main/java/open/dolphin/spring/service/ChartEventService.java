package open.dolphin.spring.service;

import open.dolphin.infomodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.*;
import java.util.logging.Logger;

/**
 * Spring Boot service for chart events and patient visit management.
 * Migrated from ChartEventServiceBean (Jakarta EE EJB).
 */
@Service
@Transactional
public class ChartEventService {

    private static final Logger logger = Logger.getLogger(ChartEventService.class.getName());

    @PersistenceContext
    private EntityManager em;

    private boolean DEBUG = false;

    // In Spring Boot, we'll use a simpler approach for event handling
    // For now, we'll implement the core functionality without async context handling

    public void notifyEvent(ChartEventModel evt) {
        // In Spring Boot, we could use WebSocket or Server-Sent Events
        // For now, we'll implement a basic version
        String fid = evt.getFacilityId();
        if (fid == null) {
            logger.warning("Facility id is null.");
            return;
        }

        // Log the event for debugging
        if (DEBUG) {
            logger.info("ChartEvent notified: " + evt.getEventType() + " for facility: " + fid);
        }
    }

    public String getServerUUID() {
        // In a real implementation, this would be configured
        return "spring-boot-server-" + System.currentTimeMillis();
    }

    public List<PatientVisitModel> getPvtList(String fid) {
        // This would need to be implemented with proper caching in Spring Boot
        // For now, return an empty list
        return new ArrayList<>();
    }

    public int processChartEvent(ChartEventModel evt) {
        int eventType = evt.getEventType();

        if (DEBUG) {
            logger.info("ChartEventService: " + eventType + " will be processed");
        }

        boolean sendEvent = true;
        switch(eventType) {
            case ChartEventModel.PVT_DELETE:
                processPvtDeleteEvent(evt);
                break;
            case ChartEventModel.PVT_STATE:
                sendEvent = processPvtStateEvent(evt);
                break;
            case ChartEventModel.PVT_MEMO:
                sendEvent = processPvtMemoEvent(evt);
                break;
            default:
                return 0;
        }

        // Notify clients
        if(sendEvent) {
            notifyEvent(evt);
        }

        return 1;
    }

    private void processPvtDeleteEvent(ChartEventModel evt) {
        long pvtPk = evt.getPvtPk();
        String fid = evt.getFacilityId();

        // Remove from database
        PatientVisitModel exist = em.find(PatientVisitModel.class, pvtPk);
        if (exist != null) {
            PatientModel pm = exist.getPatientModel();
            if(pm != null) {
                logger.info("processPvtDeleteEvent : pvtPk = " + pvtPk +
                           ", ptId = " + pm.getPatientId() +
                           ", pvtDate = " + exist.getPvtDate());
            }
            em.remove(exist);
        }

        // Remove from pvtList (would need proper caching implementation)
        List<PatientVisitModel> pvtList = getPvtList(fid);
        PatientVisitModel toRemove = null;
        for (PatientVisitModel model : pvtList) {
            if (model.getId() == pvtPk) {
                toRemove = model;
                break;
            }
        }
        if (toRemove != null) {
            pvtList.remove(toRemove);
        }
    }

    private boolean processPvtStateEvent(ChartEventModel evt) {
        String fid = evt.getFacilityId();
        long pvtId = evt.getPvtPk();
        int state = evt.getState();
        int byomeiCount = evt.getByomeiCount();
        int byomeiCountToday = evt.getByomeiCountToday();
        String memo = evt.getMemo();
        String ownerUUID = evt.getOwnerUUID();
        long ptPk = evt.getPtPk();

        if((state & (1 << PatientVisitModel.BIT_NOTUPDATE)) > 0) {
            return false;
        }

        List<PatientVisitModel> pvtList = getPvtList(fid);

        // Update database PatientVisitModel
        PatientVisitModel pvt = em.find(PatientVisitModel.class, pvtId);
        if (pvt != null) {
            // Complex state management logic from original
            if(state <= 1 && pvt.getState() >= 2) {
                if((state & (1 << PatientVisitModel.BIT_CANCEL)) == 0 &&
                   (pvt.getState() & (1 << PatientVisitModel.BIT_CANCEL)) > 0) {
                    int status = pvt.getState();
                    status &= ~(1 << PatientVisitModel.BIT_CANCEL);
                    pvt.setState(status);
                }else if((state & (1 << PatientVisitModel.BIT_TREATMENT)) == 0 &&
                        (pvt.getState() & (1 << PatientVisitModel.BIT_TREATMENT)) > 0) {
                    int status = pvt.getState();
                    status &= ~(1 << PatientVisitModel.BIT_TREATMENT);
                    pvt.setState(status);
                }else if((state & (1 << PatientVisitModel.BIT_GO_OUT)) == 0 &&
                        (pvt.getState() & (1 << PatientVisitModel.BIT_GO_OUT)) > 0) {
                    int status = pvt.getState();
                    status &= ~(1 << PatientVisitModel.BIT_GO_OUT);
                    pvt.setState(status);
                }else if((state & (1 << PatientVisitModel.BIT_HURRY)) == 0 &&
                        (pvt.getState() & (1 << PatientVisitModel.BIT_HURRY)) > 0) {
                    int status = pvt.getState();
                    status &= ~(1 << PatientVisitModel.BIT_HURRY);
                    pvt.setState(status);
                }
                // Set correct state for notification
                evt.setState(pvt.getState());
            }else{
                pvt.setState(state);
            }
            pvt.setByomeiCount(byomeiCount);
            pvt.setByomeiCountToday(byomeiCountToday);
            pvt.setMemo(memo);
        }

        // Update database PatientModel
        PatientModel pm = em.find(PatientModel.class, ptPk);
        if (pm != null) {
            logger.info("processPvtStateEvent : owner = " + ownerUUID +
                       ", pvtPk = " + pvtId +
                       ", ptId = " + pm.getPatientId() +
                       ", state = " + state);
            pm.setOwnerUUID(ownerUUID);
        }

        // Update pvtList
        for (PatientVisitModel model : pvtList) {
            if (model.getId() == pvtId) {
                // Apply same state logic
                if(state <= 1 && model.getState() >= 2) {
                    if((state & (1 << PatientVisitModel.BIT_CANCEL)) == 0 &&
                       (model.getState() & (1 << PatientVisitModel.BIT_CANCEL)) > 0) {
                        int status = model.getState();
                        status &= ~(1 << PatientVisitModel.BIT_CANCEL);
                        model.setState(status);
                    }else if((state & (1 << PatientVisitModel.BIT_TREATMENT)) == 0 &&
                            (model.getState() & (1 << PatientVisitModel.BIT_TREATMENT)) > 0) {
                        int status = model.getState();
                        status &= ~(1 << PatientVisitModel.BIT_TREATMENT);
                        model.setState(status);
                    }else if((state & (1 << PatientVisitModel.BIT_GO_OUT)) == 0 &&
                            (model.getState() & (1 << PatientVisitModel.BIT_GO_OUT)) > 0) {
                        int status = model.getState();
                        status &= ~(1 << PatientVisitModel.BIT_GO_OUT);
                        model.setState(status);
                    }else if((state & (1 << PatientVisitModel.BIT_HURRY)) == 0 &&
                            (model.getState() & (1 << PatientVisitModel.BIT_HURRY)) > 0) {
                        int status = model.getState();
                        status &= ~(1 << PatientVisitModel.BIT_HURRY);
                        model.setState(status);
                    }
                    // Set correct state for notification
                    evt.setState(model.getState());
                }else{
                    model.setState(state);
                }
                model.setByomeiCount(byomeiCount);
                model.setByomeiCountToday(byomeiCountToday);
                model.setMemo(memo);
                model.getPatientModel().setOwnerUUID(ownerUUID);
                break;
            }
        }

        // Update open state for all patient's visits
        for (PatientVisitModel model : pvtList) {
            if (model.getPatientModel().getId() == ptPk) {
                model.setStateBit(PatientVisitModel.BIT_OPEN, ownerUUID != null);
                model.getPatientModel().setOwnerUUID(ownerUUID);
            }
        }

        return true;
    }

    private boolean processPvtMemoEvent(ChartEventModel evt) {
        String fid = evt.getFacilityId();
        long pvtId = evt.getPvtPk();
        int state = evt.getState();
        String memo = evt.getMemo();

        if((state & (1 << PatientVisitModel.BIT_NOTUPDATE)) > 0) {
            return false;
        }

        List<PatientVisitModel> pvtList = getPvtList(fid);

        PatientVisitModel pvt = em.find(PatientVisitModel.class, pvtId);
        if(pvt != null) {
            pvt.setMemo(memo);
        }

        logger.info("processPvtMemoEvent : pvtPk = " + pvtId + ", memo = " + memo);

        for(PatientVisitModel model : pvtList) {
            if(model.getId() == pvtId) {
                model.setMemo(memo);
                break;
            }
        }
        return true;
    }

    public void start() {
        logger.info("ChartEventService: start called");
        // Initialize server UUID and PVT list would go here
        // This would need proper Spring Boot configuration
    }

    // Diagnosis count management
    public void setByomeiCount(long karteId, PatientVisitModel pvt) {
        int byomeiCount = 0;
        int byomeiCountToday = 0;
        Date pvtDate = pvt.getPvtDate() != null ?
                      java.sql.Date.valueOf(pvt.getPvtDate()) :
                      new Date();

        // Query database for diagnosis count
        final String sql = "from RegisteredDiagnosisModel r where r.karte.id = :karteId";
        List<RegisteredDiagnosisModel> rdList =
                em.createQuery(sql)
                .setParameter("karteId", karteId)
                .getResultList();

        for (RegisteredDiagnosisModel rd : rdList) {
            // Simplified date comparison logic
            if (rd.getStarted() != null && rd.getEnded() != null) {
                if (rd.getStarted().compareTo(pvtDate) <= 0 &&
                    (rd.getEnded().compareTo(pvtDate) >= 0 || rd.getEnded().equals(new Date(0)))) {
                    byomeiCount++;
                    if (rd.getStarted().equals(pvtDate)) {
                        byomeiCountToday++;
                    }
                }
            }
        }

        pvt.setByomeiCount(byomeiCount);
        pvt.setByomeiCountToday(byomeiCountToday);
    }
}
