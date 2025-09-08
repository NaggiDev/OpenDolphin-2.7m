package open.dolphin.spring.model.domain.appointment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;
import open.dolphin.spring.model.domain.patient.PatientModel;
import open.dolphin.spring.model.domain.patient.PatientVisitModel;

/**
 * Chart event通知用のモデル
 *
 * @author masuda, Masuda Naika
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChartEventModel extends InfoModel {

    public static final int PVT_STATE = 0;
    public static final int PVT_ADD = 1;
    public static final int PVT_DELETE = 2;
    public static final int PVT_RENEW = 3;
    public static final int PVT_MERGE = 4;
    public static final int PM_MERGE = 5;
    // s.oh^ 2014/10/14 診察終了後のメモ対応
    public static final int PVT_MEMO = 6;
    // s.oh$

    private String issuerUUID;
    private int eventType;

    private long pvtPk;
    private int state;
    private String memo;
    private int byomeiCount;
    private int byomeiCountToday;
    private String ownerUUID;
    private String facilityId;

    private PatientVisitModel pvt;

    private long ptPk;
    private PatientModel patient;

    // public static enum EVENT {PVT_STATE, PVT_ADD, PVT_DELETE, PVT_RENEW,
    // PVT_MERGE, PM_MERGE};

    public ChartEventModel(String issuerUUID) {
        this.issuerUUID = issuerUUID;
    }

    public void setParamFromPvt(PatientVisitModel pvt) {

        if (pvt == null) {
            return;
        }

        this.pvtPk = pvt.getId();
        this.state = pvt.getStatus();

        this.byomeiCount = pvt.getByomeiCount();
        this.byomeiCountToday = pvt.getByomeiCountToday();

        this.memo = pvt.getMemo();
        this.ownerUUID = pvt.getPatientModel().getOwnerUUID();
        this.facilityId = pvt.getFacilityId();
        this.ptPk = pvt.getPatientModel().getId();
    }
}
