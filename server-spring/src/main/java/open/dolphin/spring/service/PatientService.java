package open.dolphin.spring.service;

import open.dolphin.infomodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.*;

/**
 * Spring Boot service for patient operations.
 * Migrated from PatientServiceBean (Jakarta EE EJB).
 */
@Service
@Transactional
public class PatientService {

    // cancel status=64 を where 節へ追加
    private static final String QUERY_PATIENT_BY_PVTDATE = "from PatientVisitModel p where p.facilityId = :fid and p.pvtDate like :date and p.status!=64";
    private static final String QUERY_PATIENT_BY_NAME = "from PatientModel p where p.facilityId=:fid and p.fullName like :name";
    private static final String QUERY_PATIENT_BY_KANA = "from PatientModel p where p.facilityId=:fid and p.kanaName like :name";
    private static final String QUERY_PATIENT_BY_FID_PID = "from PatientModel p where p.facilityId=:fid and p.patientId like :pid";
    private static final String QUERY_PATIENT_BY_TELEPHONE = "from PatientModel p where p.facilityId = :fid and (p.telephone like :number or p.mobilePhone like :number)";
    private static final String QUERY_PATIENT_BY_ZIPCODE = "from PatientModel p where p.facilityId = :fid and p.address.zipCode like :zipCode";
    private static final String QUERY_INSURANCE_BY_PATIENT_PK = "from HealthInsuranceModel h where h.patient.id=:pk";
    private static final String QUERY_PATIENT_BY_APPMEMO = "from PatientModel p where p.facilityId = :fid and p.appMemo like :appMemo";

    private static final String PK = "pk";
    private static final String FID = "fid";
    private static final String PID = "pid";
    private static final String NAME = "name";
    private static final String NUMBER = "number";
    private static final String ZIPCODE = "zipCode";
    private static final String DATE = "date";
    private static final String PERCENT = "%";
    private static final String APPMEMO = "appMemo";

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ChartEventService eventService;

    public List<PatientModel> getPatientsByName(String fid, String name) {
        List<PatientModel> ret = em.createQuery(QUERY_PATIENT_BY_NAME)
                .setParameter(FID, fid)
                .setParameter(NAME, name + PERCENT)
                .getResultList();

        // 後方一致検索を行う
        if (ret.isEmpty()) {
            ret = em.createQuery(QUERY_PATIENT_BY_NAME)
                .setParameter(FID, fid)
                .setParameter(NAME, PERCENT + name)
                .getResultList();
        }

        // 施設患者一括表示機能
        if (ret.isEmpty()) {
            ret = em.createQuery(QUERY_PATIENT_BY_APPMEMO)
                .setParameter(FID, fid)
                .setParameter(APPMEMO, name + PERCENT)
                .getResultList();
        }
        if (ret.isEmpty()) {
            ret = em.createQuery(QUERY_PATIENT_BY_APPMEMO)
                .setParameter(FID, fid)
                .setParameter(APPMEMO, PERCENT + name)
                .getResultList();
        }

        // 患者の健康保険を取得する
        setHealthInsurances(ret);

        // 最終受診日設定
        if (!ret.isEmpty()) {
            setPvtDate(fid, ret);
        }

        return ret;
    }

    public List<PatientModel> getPatientsByKana(String fid, String name) {
        List<PatientModel> ret = em.createQuery(QUERY_PATIENT_BY_KANA)
            .setParameter(FID, fid)
            .setParameter(NAME, name + PERCENT)
            .getResultList();

        if (ret.isEmpty()) {
            ret = em.createQuery(QUERY_PATIENT_BY_KANA)
                .setParameter(FID, fid)
                .setParameter(NAME, PERCENT + name)
                .getResultList();
        }

        // 施設患者一括表示機能
        if (ret.isEmpty()) {
            ret = em.createQuery(QUERY_PATIENT_BY_APPMEMO)
                .setParameter(FID, fid)
                .setParameter(APPMEMO, name + PERCENT)
                .getResultList();
        }
        if (ret.isEmpty()) {
            ret = em.createQuery(QUERY_PATIENT_BY_APPMEMO)
                .setParameter(FID, fid)
                .setParameter(APPMEMO, PERCENT + name)
                .getResultList();
        }

        // 患者の健康保険を取得する
        setHealthInsurances(ret);

        // 最終受診日設定
        if (!ret.isEmpty()) {
            setPvtDate(fid, ret);
        }

        return ret;
    }

    public List<PatientModel> getPatientsByDigit(String fid, String digit) {
        List<PatientModel> ret = em.createQuery(QUERY_PATIENT_BY_FID_PID)
            .setParameter(FID, fid)
            .setParameter(PID, digit + PERCENT)
            .getResultList();

        if (ret.isEmpty()) {
            ret = em.createQuery(QUERY_PATIENT_BY_TELEPHONE)
                .setParameter(FID, fid)
                .setParameter(NUMBER, digit + PERCENT)
                .getResultList();
        }

        if (ret.isEmpty()) {
            ret = em.createQuery(QUERY_PATIENT_BY_ZIPCODE)
                .setParameter(FID, fid)
                .setParameter(ZIPCODE, digit + PERCENT)
                .getResultList();
        }

        // 患者の健康保険を取得する
        setHealthInsurances(ret);

        // 最終受診日設定
        if (!ret.isEmpty()) {
            setPvtDate(fid, ret);
        }

        return ret;
    }

    public List<PatientModel> getPatientsByPvtDate(String fid, String pvtDate) {
        List<PatientVisitModel> list =
                em.createQuery(QUERY_PATIENT_BY_PVTDATE)
                  .setParameter(FID, fid)
                  .setParameter(DATE, pvtDate + PERCENT)
                  .getResultList();

        List<PatientModel> ret = new ArrayList<>();

        for (PatientVisitModel pvt : list) {
            PatientModel patient = pvt.getPatientModel();
            List<HealthInsuranceModel> insurances
                        = (List<HealthInsuranceModel>)em.createQuery(QUERY_INSURANCE_BY_PATIENT_PK)
                        .setParameter(PK, patient.getId()).getResultList();
                patient.setHealthInsurances(insurances);
            ret.add(patient);

            // 患者の健康保険を取得する
            setHealthInsurances(patient);
           patient.setPvtDate(pvt.getPvtDate());
        }
        return ret;
    }

    public PatientModel getPatientById(String fid, String pid) {
        // 患者レコードは FacilityId と patientId で複合キーになっている
        PatientModel bean
                = (PatientModel)em.createQuery(QUERY_PATIENT_BY_FID_PID)
                .setParameter(FID, fid)
                .setParameter(PID, pid)
                .getSingleResult();

        long pk = bean.getId();

        // Lazy Fetch の 基本属性を検索する
        // 患者の健康保険を取得する
        setHealthInsurances(bean);

        return bean;
    }

    public int countPatients(String facilityId) {
        Long count = (Long)em.createQuery("select count(*) from PatientModel p where p.facilityId=:fid")
                .setParameter("fid", facilityId).getSingleResult();
        return count.intValue();
    }

    public List<String> getAllPatientsWithKana(String facilityId, int firstResult, int maxResult) {
        List<String> list = em.createQuery("select p.kanaName from PatientModel p where p.facilityId=:fid order by p.kanaName")
                .setParameter("fid", facilityId)
                .setFirstResult(firstResult)
                .setMaxResults(maxResult)
                .getResultList();
        return list;
    }

    public List<PatientModel> getTmpKarte(String facilityId) {
        List<PatientModel> ret = new ArrayList<>();

        List<DocumentModel> list = (List<DocumentModel>)
        em.createQuery("from DocumentModel d where d.karte.patient.facilityId=:fid and d.status='T'")
                .setParameter("fid", facilityId)
                .getResultList();

        HashMap<String, String> map = new HashMap<>(10, 0.75f);
        for (DocumentModel dm : list) {
            if (dm.getFirstConfirmed().after(dm.getConfirmed())) {
                continue;
            }
            KarteBean kb = dm.getKarte();
            PatientModel pm = kb.getPatient();
            if (map.get(pm.getPatientId()) != null) {
                continue;
            }
            map.put(pm.getPatientId(), "pid");
            ret.add(pm);
        }

        this.setHealthInsurances(ret);

        return ret;
    }

    public long addPatient(PatientModel patient) {
        em.persist(patient);
        long pk = patient.getId();
        return pk;
    }

    public int update(PatientModel patient) {
        em.merge(patient);
        updatePvtList(patient);
        return 1;
    }

    // pvtListのPatientModelを更新し、クライアントにも通知する
    private void updatePvtList(PatientModel pm) {
        String fid = pm.getFacilityId();
        List<PatientVisitModel> pvtList = eventService.getPvtList(fid);
        for (PatientVisitModel pvt : pvtList) {
            if (pvt.getPatientModel().getId() == pm.getId()) {
                List<HealthInsuranceModel> him = pvt.getPatientModel().getHealthInsurances();
                if(pm.getHealthInsurances() == null) {
                    pm.setHealthInsurances(him);
                }
                pvt.setPatientModel(pm);
                 // クライアントに通知
                String uuid = eventService.getServerUUID();
                ChartEventModel msg = new ChartEventModel(uuid);
                msg.setPatientModel(pm);
                msg.setFacilityId(fid);
                msg.setEventType(ChartEventModel.PM_MERGE);
                eventService.notifyEvent(msg);
            }
        }
    }

    private void setPvtDate(String fid, List<PatientModel> list) {
        final String sql =
                "from PatientVisitModel p where p.facilityId = :fid and p.patient.id = :patientPk "
                + "and p.status != :status order by p.pvtDate desc";

        for (PatientModel patient : list) {
            try {
                PatientVisitModel pvt = (PatientVisitModel)
                        em.createQuery(sql)
                        .setParameter("fid", fid)
                        .setParameter("patientPk", patient.getId())
                        .setParameter("status", -1)
                        .setMaxResults(1)
                        .getSingleResult();
                patient.setPvtDate(pvt.getPvtDate());
            } catch (NoResultException e) {
                // No result found, continue
            }
        }
    }

    public List<PatientModel> getPatientList(String fid, List<String> idList) {
        final String sql
                = "from PatientModel p where p.facilityId = :fid and p.patientId in (:ids)";

        List<PatientModel> list = (List<PatientModel>)
                em.createQuery(sql)
                .setParameter("fid", fid)
                .setParameter("ids", idList)
                .getResultList();

        // 患者の健康保険を取得する。忘れがちｗ
        setHealthInsurances(list);

        return list;
    }

    protected void setHealthInsurances(Collection<PatientModel> list) {
        if (list != null && !list.isEmpty()) {
            for (PatientModel pm : list) {
                setHealthInsurances(pm);
            }
        }
    }

    protected void setHealthInsurances(PatientModel pm) {
        if (pm != null) {
            List<HealthInsuranceModel> ins = getHealthInsurances(pm.getId());
            pm.setHealthInsurances(ins);
        }
    }

    protected List<HealthInsuranceModel> getHealthInsurances(long pk) {
        List<HealthInsuranceModel> ins =
                em.createQuery(QUERY_INSURANCE_BY_PATIENT_PK)
                .setParameter(PK, pk)
                .getResultList();
        return ins;
    }

    // 検索件数が1000件超過
    public Long getPatientCount(String facilityId, String patientId) {
        Long ret = (Long)em.createQuery("select count(*) from PatientModel p where p.facilityId=:fid and p.patientId like :pid")
                .setParameter("fid", facilityId)
                .setParameter("pid", patientId + "%")
                .getSingleResult();
        return ret;
    }

    // 一括カルテPDF出力
    public List<PatientModel> getAllPatient(String fid) {
        List<PatientModel> ret = em.createQuery("from PatientModel p where p.facilityId=:fid")
            .setParameter(FID, fid)
            .getResultList();

        setHealthInsurances(ret);

        return ret;
    }

    // 患者検索(傷病名)
    public List<PatientModel> getCustom(String fid, String param) {
        List<PatientModel> ret = new ArrayList<>();

        final String DIAGNOSIS = "[D]";

        if(param.indexOf(DIAGNOSIS) == 0) {
            String val = param.substring(param.indexOf(DIAGNOSIS) + DIAGNOSIS.length());
            List<RegisteredDiagnosisModel> list = null;
            if(val.startsWith("*") && val.endsWith("*")) {
                list = (List<RegisteredDiagnosisModel>)
                       em.createQuery("from RegisteredDiagnosisModel d where d.diagnosis like :val and d.status='F'")
                         .setParameter("val", PERCENT + val + PERCENT)
                         .getResultList();
            }else if(val.startsWith("*")) {
                list = (List<RegisteredDiagnosisModel>)
                       em.createQuery("from RegisteredDiagnosisModel d where d.diagnosis like :val and d.status='F'")
                         .setParameter("val", PERCENT + val)
                         .getResultList();
            }else if(val.endsWith("*")) {
                list = (List<RegisteredDiagnosisModel>)
                       em.createQuery("from RegisteredDiagnosisModel d where d.diagnosis like :val and d.status='F'")
                         .setParameter("val", val + PERCENT)
                         .getResultList();
            }else{
                list = (List<RegisteredDiagnosisModel>)
                       em.createQuery("from RegisteredDiagnosisModel d where d.diagnosis=:val and d.status='F'")
                         .setParameter("val", val)
                         .getResultList();
            }
            HashMap<String, String> map = new HashMap<>(10, 0.75f);
            for(RegisteredDiagnosisModel rdm : list) {
                KarteBean karte = (KarteBean)em.find(KarteBean.class, rdm.getKarte().getId());
                if(karte != null && karte.getPatient() != null) {
                    if(map.get(karte.getPatient().getPatientId()) != null) {
                        continue;
                    }else{
                        map.put(karte.getPatient().getPatientId(), "pid");
                    }
                    ret.add(karte.getPatient());
                }
            }
            map.clear();
        }

        this.setHealthInsurances(ret);

        return ret;
    }
}
