package open.dolphin.spring.service;

import open.dolphin.infomodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.*;
import java.util.logging.Logger;

/**
 * Spring Boot service for patient visit tracking.
 * Migrated from PVTServiceBean (Jakarta EE EJB).
 */
@Service
@Transactional
public class PVTService {

    private static final Logger logger = Logger.getLogger(PVTService.class.getName());

    private static final String QUERY_PATIENT_BY_FID_PID = "from PatientModel p where p.facilityId=:fid and p.patientId=:pid";
    private static final String QUERY_PVT_BY_FID_PID_DATE = "from PatientVisitModel p where p.facilityId=:fid and p.pvtDate like :date and p.patient.patientId=:pid";
    private static final String QUERY_PVT_BY_FID_DATE = "from PatientVisitModel p where p.facilityId=:fid and p.pvtDate like :date order by p.pvtDate";
    private static final String QUERY_PVT_BY_FID_DID_DATE = "from PatientVisitModel p where p.facilityId=:fid and p.pvtDate like :date and (doctorId=:did or doctorId=:unassigned) order by p.pvtDate";
    private static final String QUERY_INSURANCE_BY_PATIENT_ID = "from HealthInsuranceModel h where h.patient.id=:id";
    private static final String QUERY_KARTE_BY_PATIENT_ID = "from KarteBean k where k.patient.id=:id";
    private static final String QUERY_APPO_BY_KARTE_ID_DATE = "from AppointmentModel a where a.karte.id=:id and a.date=:date";
    private static final String QUERY_PVT_BY_PK = "from PatientVisitModel p where p.id=:id";
    private static final String QUERY_KARTE_ID_BY_PATIENT_ID = "select k.id from KarteBean k where k.patient.id = :id";

    private static final String FID = "fid";
    private static final String PID = "pid";
    private static final String DID = "did";
    private static final String UNASSIGNED = "unassigned";
    private static final String ID = "id";
    private static final String DATE = "date";
    private static final String PERCENT = "%";
    private static final int BIT_SAVE_CLAIM = 1;
    private static final int BIT_MODIFY_CLAIM = 2;
    private static final int BIT_CANCEL = 6;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ChartEventService eventService;

    // In Spring Boot, we'll use a simpler approach for context management
    // For now, we'll implement the core functionality

    /**
     * Add patient visit information.
     * @param pvt PatientVisitModel
     * @return number of records added
     */
    public int addPvt(PatientVisitModel pvt) {
        String fid = pvt.getFacilityId();
        PatientModel patient = pvt.getPatientModel();
        pvt.setFacilityId(fid);
        patient.setFacilityId(fid);

        // Set department information
        StringBuilder sb = new StringBuilder();
        sb.append(pvt.getDeptName()).append(",");
        sb.append(pvt.getDeptCode()).append(",");
        sb.append(pvt.getDoctorName()).append(",");
        sb.append(pvt.getDoctorId()).append(",");
        sb.append(pvt.getJmariNumber()).append(",");
        pvt.setDepartment(sb.toString());

        // Check if patient already exists
        try {
            PatientModel exist = (PatientModel)
                    em.createQuery(QUERY_PATIENT_BY_FID_PID)
                    .setParameter(FID, fid)
                    .setParameter(PID, patient.getPatientId())
                    .getSingleResult();

            logger.info("addPvt : merge patient");

            // Update health insurance information
            List<HealthInsuranceModel> old =
                    em.createQuery(QUERY_INSURANCE_BY_PATIENT_ID)
                    .setParameter(ID, exist.getId())
                    .getResultList();

            List<HealthInsuranceModel> newOne = patient.getHealthInsurances();

            if (newOne != null && !newOne.isEmpty()) {
                // Remove old insurance records
                for (HealthInsuranceModel model : old) {
                    em.remove(model);
                }

                // Add new insurance records
                for (HealthInsuranceModel model : newOne) {
                    model.setPatient(exist);
                    em.persist(model);
                }
                exist.setHealthInsurances(newOne);
            } else {
                exist.setHealthInsurances(old);
            }

            // Update patient information
            exist.setFamilyName(patient.getFamilyName());
            exist.setGivenName(patient.getGivenName());
            exist.setFullName(patient.getFullName());
            exist.setKanaFamilyName(patient.getKanaFamilyName());
            exist.setKanaGivenName(patient.getKanaGivenName());
            exist.setKanaName(patient.getKanaName());
            exist.setGender(patient.getGender());
            exist.setGenderDesc(patient.getGenderDesc());
            exist.setGenderCodeSys(patient.getGenderCodeSys());
            exist.setBirthday(patient.getBirthday());
            exist.setSimpleAddressModel(patient.getSimpleAddressModel());
            exist.setTelephone(patient.getTelephone());
            exist.setAppMemo(patient.getAppMemo());

            em.merge(exist);
            pvt.setPatientModel(exist);

        } catch (NoResultException e) {
            logger.info("addPvt : add patient");
            // New patient - persist
            em.persist(patient);

            // Create karte for new patient
            KarteBean karte = new KarteBean();
            karte.setPatientModel(patient);
            karte.setCreated(new Date());
            em.persist(karte);
        }

        // Handle PVT registration
        if (pvt.getPvtDate() == null) {
            return 0; // No visit date, just patient registration
        }

        // Handle scheduled visits (future dates)
        if (!isToday(pvt.getPvtDate())) {
            logger.info("scheduled PVT: " + pvt.getPvtDate());
            int index = pvt.getPvtDate().indexOf("T");
            String test = pvt.getPvtDate().substring(0, index);

            List<PatientVisitModel> list = em
                    .createQuery(QUERY_PVT_BY_FID_PID_DATE)
                    .setParameter(FID, fid)
                    .setParameter(DATE, test + PERCENT)
                    .setParameter(PID, patient.getPatientId())
                    .getResultList();

            if (list.isEmpty()) {
                em.persist(pvt);
            } else {
                // Update existing record
                PatientVisitModel target = list.get(0);
                target.setDepartment(pvt.getDepartment());
                target.setDeptCode(pvt.getDeptCode());
                target.setDeptName(pvt.getDeptName());
                target.setDoctorId(pvt.getDoctorId());
                target.setDoctorName(pvt.getDoctorName());
                target.setFirstInsurance(pvt.getFirstInsurance());
                target.setInsuranceUid(pvt.getInsuranceUid());
                target.setJmariNumber(pvt.getJmariNumber());
            }
            return 1;
        }

        // Handle today's visits with concurrency control
        long karteId = (Long)
                em.createQuery(QUERY_KARTE_ID_BY_PATIENT_ID)
                .setParameter(ID, pvt.getPatientModel().getId())
                .getSingleResult();

        // Check for appointments
        List<AppointmentModel> appointments =
                em.createQuery(QUERY_APPO_BY_KARTE_ID_DATE)
                .setParameter(ID, karteId)
                .setParameter(DATE, new Date()) // Today's date
                .getResultList();

        if (appointments != null && !appointments.isEmpty()) {
            AppointmentModel appo = appointments.get(0);
            pvt.setAppointment(appo.getName());
        }

        // Check for duplicate visits at same time
        List<PatientVisitModel> pvtList = eventService.getPvtList(fid);
        for (int i = 0; i < pvtList.size(); ++i) {
            PatientVisitModel test = pvtList.get(i);
            if (test.getPvtDate().equals(pvt.getPvtDate())
                    && (test.getState() & (1 << PatientVisitModel.BIT_CANCEL)) == 0) {
                // Same patient, same time, not cancelled
                if (test.getPatientId() != null && pvt.getPatientId() != null
                    && test.getPatientId().equals(pvt.getPatientId())
                    && test.getFacilityId().equals(pvt.getFacilityId())) {

                    pvt.setId(test.getId());
                    pvt.setState(test.getState());
                    pvt.getPatientModel().setOwnerUUID(test.getPatientModel().getOwnerUUID());
                    pvt.setByomeiCount(test.getByomeiCount());
                    pvt.setByomeiCountToday(test.getByomeiCountToday());

                    em.merge(pvt);
                    pvtList.set(i, pvt);

                    // Notify clients
                    String uuid = eventService.getServerUUID();
                    ChartEventModel msg = new ChartEventModel(uuid);
                    msg.setParamFromPvt(pvt);
                    msg.setPatientVisitModel(pvt);
                    msg.setEventType(ChartEventModel.PVT_MERGE);
                    eventService.notifyEvent(msg);

                    return 0;
                }
            }
        }

        // New visit - persist and add to list
        eventService.setByomeiCount(karteId, pvt);
        em.persist(pvt);
        pvtList.add(pvt);

        // Notify clients
        String uuid = eventService.getServerUUID();
        ChartEventModel msg = new ChartEventModel(uuid);
        msg.setParamFromPvt(pvt);
        msg.setPatientVisitModel(pvt);
        msg.setEventType(ChartEventModel.PVT_ADD);
        eventService.notifyEvent(msg);

        return 1;
    }

    /**
     * Check if the given date is today.
     * @param mmlDate date string in format yyyy-MM-ddTHH:mm:ss
     * @return true if date is today
     */
    private boolean isToday(String mmlDate) {
        try {
            int index = mmlDate.indexOf("T");
            String test = mmlDate.substring(0, index);
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            String today = sdf.format(new Date());
            return test.equals(today);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get patient visits by facility and date.
     * @param fid facility ID
     * @param date date string
     * @param firstResult pagination start
     * @param appoDateFrom appointment date from
     * @param appoDateTo appointment date to
     * @return list of patient visits
     */
    public List<PatientVisitModel> getPvt(String fid, String date, int firstResult,
                                         String appoDateFrom, String appoDateTo) {

        if (!date.endsWith(PERCENT)) {
            date += PERCENT;
        }

        List<PatientVisitModel> result =
                em.createQuery(QUERY_PVT_BY_FID_DATE)
                  .setParameter(FID, fid)
                  .setParameter(DATE, date + PERCENT)
                  .setFirstResult(firstResult)
                  .getResultList();

        if (result.isEmpty()) {
            return result;
        }

        int index = date.indexOf(PERCENT);
        Date theDate = ModelUtils.getDateAsObject(date.substring(0, index));
        boolean searchAppo = (appoDateFrom != null && appoDateTo != null);

        // Load patient and insurance information
        for (PatientVisitModel pvt : result) {
            PatientModel patient = pvt.getPatientModel();

            List<HealthInsuranceModel> insurances =
                    em.createQuery(QUERY_INSURANCE_BY_PATIENT_ID)
                      .setParameter(ID, patient.getId())
                      .getResultList();
            patient.setHealthInsurances(insurances);

            // Search for appointments if requested
            if (searchAppo) {
                KarteBean karte = (KarteBean)
                        em.createQuery(QUERY_KARTE_BY_PATIENT_ID)
                          .setParameter(ID, patient.getId())
                          .getSingleResult();

                long karteId = karte.getId();
                List<AppointmentModel> appointments =
                        em.createQuery(QUERY_APPO_BY_KARTE_ID_DATE)
                          .setParameter(ID, karteId)
                          .setParameter(DATE, theDate)
                          .getResultList();

                if (appointments != null && !appointments.isEmpty()) {
                    AppointmentModel appo = appointments.get(0);
                    pvt.setAppointment(appo.getName());
                }
            }
        }

        return result;
    }

    /**
     * Get patient visits by facility, doctor, and date.
     */
    public List<PatientVisitModel> getPvt(String fid, String did, String unassigned,
                                         String date, int firstResult,
                                         String appoDateFrom, String appoDateTo) {

        if (!date.endsWith(PERCENT)) {
            date += PERCENT;
        }

        List<PatientVisitModel> result =
                em.createQuery(QUERY_PVT_BY_FID_DID_DATE)
                  .setParameter(FID, fid)
                  .setParameter(DID, did)
                  .setParameter(UNASSIGNED, unassigned)
                  .setParameter(DATE, date + PERCENT)
                  .setFirstResult(firstResult)
                  .getResultList();

        if (result.isEmpty()) {
            return result;
        }

        int index = date.indexOf(PERCENT);
        Date theDate = ModelUtils.getDateAsObject(date.substring(0, index));
        boolean searchAppo = (appoDateFrom != null && appoDateTo != null);

        for (PatientVisitModel pvt : result) {
            PatientModel patient = pvt.getPatientModel();

            List<HealthInsuranceModel> insurances =
                    em.createQuery(QUERY_INSURANCE_BY_PATIENT_ID)
                      .setParameter(ID, patient.getId())
                      .getResultList();
            patient.setHealthInsurances(insurances);

            if (searchAppo) {
                KarteBean karte = (KarteBean)
                        em.createQuery(QUERY_KARTE_BY_PATIENT_ID)
                          .setParameter(ID, patient.getId())
                          .getSingleResult();

                long karteId = karte.getId();
                List<AppointmentModel> appointments =
                        em.createQuery(QUERY_APPO_BY_KARTE_ID_DATE)
                          .setParameter(ID, karteId)
                          .setParameter(DATE, theDate)
                          .getResultList();

                if (appointments != null && !appointments.isEmpty()) {
                    AppointmentModel appo = appointments.get(0);
                    pvt.setAppointment(appo.getName());
                }
            }
        }

        return result;
    }

    /**
     * Remove patient visit.
     * @param id visit ID
     * @param fid facility ID
     * @return number of records removed
     */
    public int removePvt(long id, String fid) {
        try {
            PatientVisitModel exist = em.find(PatientVisitModel.class, id);
            if (exist != null) {
                em.remove(exist);
            }

            List<PatientVisitModel> pvtList = eventService.getPvtList(fid);
            PatientVisitModel toRemove = null;
            for (PatientVisitModel model : pvtList) {
                if (model.getId() == id) {
                    toRemove = model;
                    break;
                }
            }
            if (toRemove != null) {
                pvtList.remove(toRemove);
                return 1;
            }
        } catch (Exception e) {
            logger.warning("Error removing PVT: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Remove patient visit by ID only.
     */
    public int removePvt(long id) {
        PatientVisitModel exist = em.find(PatientVisitModel.class, id);
        if (exist != null) {
            em.remove(exist);
            return 1;
        }
        return 0;
    }

    /**
     * Update patient visit state.
     * @param pk record ID
     * @param state new state
     * @return 1 if updated, 0 if not
     */
    public int updatePvtState(long pk, int state) {
        List<PatientVisitModel> list =
                em.createQuery(QUERY_PVT_BY_PK)
                  .setParameter(ID, pk)
                  .getResultList();

        if (list.isEmpty()) {
            return 0;
        }

        PatientVisitModel exist = list.get(0);

        // Handle special states (CLAIM save/modify)
        if (state == 2 || state == 4) {
            exist.setState(state);
            em.flush();
            return 1;
        }

        int curState = exist.getState();
        boolean red = ((curState & (1 << BIT_SAVE_CLAIM)) != 0);
        boolean yellow = ((curState & (1 << BIT_MODIFY_CLAIM)) != 0);
        boolean cancel = ((curState & (1 << BIT_CANCEL)) != 0);

        // Cannot change if already saved, modified, or cancelled
        if (red || yellow || cancel) {
            return 0;
        }

        exist.setState(state);
        em.flush();
        return 1;
    }

    /**
     * Update visit memo.
     * @param pk record ID
     * @param memo new memo
     * @return 1 if updated
     */
    public int updateMemo(long pk, String memo) {
        PatientVisitModel exist = em.find(PatientVisitModel.class, pk);
        if (exist != null) {
            exist.setMemo(memo);
            return 1;
        }
        return 0;
    }
}
