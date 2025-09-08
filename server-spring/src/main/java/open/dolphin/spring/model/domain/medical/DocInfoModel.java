package open.dolphin.spring.model.domain.medical;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;
import open.dolphin.spring.model.entity.AccessRightModel;
import open.dolphin.spring.model.domain.insurance.PVTHealthInsuranceModel;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

/**
 * DocInfoModel
 *
 * 文書履歴のレコード及びCLAIM送信のコンテナとして使用するクラス。
 *
 * @author Minagawa,Kazushi. Digital Globe, Inc.
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class DocInfoModel extends InfoModel
        implements Comparable {

    // = DocumentModel.id
    @Transient
    private long docPk;

    // = 親DocumentModel.id
    @Transient
    private long parentPk;

    // 32bit GUID (MML ouput)
    @Column(nullable = false, length = 32)
    private String docId;

    // 文書種別(Dolphin固有）
    @Column(nullable = false)
    private String docType;

    // タイトル
    @Column(nullable = false)
    private String title;

    // 生成目的 MML
    @Column(nullable = false)
    private String purpose;

    // 生成目的説明 MML
    @Transient
    private String purposeDesc;

    // 生成目的コード体系 MML
    @Transient
    private String purposeCodeSys;

    // = DocumentModel.started（最初の確定日）
    @Transient
    private Date firstConfirmDate;

    // 確定日
    @Transient
    private Date confirmDate;

    // 診療科
    private String department;

    // --------------------------------------------------------------
    // 診療科説明
    // 診療科名、コード、担当医名、担当医コード、JMARIコード（カンマ連結）
    private String departmentDesc;
    // --------------------------------------------------------------

    // 診療科コード体系
    @Transient
    private String departmentCodeSys;

    // 健康保険
    private String healthInsurance;

    // 健康保険説明（名称）
    private String healthInsuranceDesc;

    // 健康保険コード体系
    @Transient
    private String healthInsuranceCodeSys;

    // 健康保険GUID （ORCAからの受付受信時に設定されている）
    private String healthInsuranceGUID;

    // 注意フラグ
    private boolean hasMark;

    // イメージ（シェーマ）フラグ
    private boolean hasImage;

    // RPフラグ
    private boolean hasRp;

    // 処置フラグ
    private boolean hasTreatment;

    // 検体検査フラグ
    private boolean hasLaboTest;

    // 文書のバージョン番号（修正時++）
    private String versionNumber;

    // バージョン説明
    @Transient
    private String versionNotes;

    // 親文書 32bit ID
    private String parentId;

    // 親文書との関係
    private String parentIdRelation;

    // 親文書との説明
    @Transient
    private String parentIdDesc;

    // 親文書との関係コード体系
    @Transient
    private String parentIdCodeSys;

    // アクセス権
    @Transient
    private Collection<AccessRightModel> accessRights;

    // ステータス = DocumentModel.status
    @Transient
    private String status;

    // この文書を表示するクラス（紹介状等で使用）
    @Transient
    private String handleClass;

    // ----------------------------------
    // Flag and param for senders
    // ----------------------------------
    // 検体検査オーダー番号
    private String labtestOrderNumber;

    // 検体検査オーダー送信フラグ
    @Transient
    private boolean sendLabtest;

    // CLAIM送信フラグ
    @Transient
    private boolean sendClaim;

    // MML送信フラグ
    @Transient
    private boolean sendMml;

    // 処方せん出力
    @Transient
    private boolean priscriptionOutput;

    // ----------------------------------
    // Claim Sender for JMS+MDB
    // ----------------------------------
    // 診断に適用した健康保険
    @Transient
    private PVTHealthInsuranceModel pVTHealthInsuranceModel;

    // 施設（病院）名
    @Transient
    private String facilityName;

    // 医療資格
    @Transient
    private String createrLisence;

    // 患者ID
    @Transient
    private String patientId;

    // 患者氏名
    @Transient
    private String patientName;

    // 患者性別
    @Transient
    private String patientGender;

    // minagawa^ 会計上送信日を変更(予定カルテ対応)
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date claimDate;
    // minagawa$
    // ----------------------------------
    // 処方せん出力に必要な情報
    // 担当医情報: Document->creatorから検索
    // 患者情報; Document->karteIdから検索
    // ----------------------------------
    // 交付日
    @Transient
    private Date issuanceDate;

    // 保険医療機関番号
    @Transient
    private String InstitutionNumber;

    // 使用期間
    @Transient
    private Date period;

    // 備考欄患者住所、氏名転記フラグ
    @Transient
    private boolean chkPatientInfo;

    // 備考欄患者麻薬施用者転記フラグ
    @Transient
    private boolean chkUseDrugInfo;

    // 備考欄に「在宅」を記載するかどうかのフラグ
    @Transient
    private boolean chkHomeMedical;

    // 一般名を使用するかどうか
    @Transient
    private boolean useGeneralName;

    // minagawa^ 入院対応
    // 外来カルテ=V, 入院カルテ=A, 在宅カルテ=H
    @Column(length = 1)
    private String admFlag;
    // minagawa$

    // ----------------------------------

    /********************************************/
    public String getDepartmentName() {
        String[] tokens = tokenizeDept(departmentDesc);
        return tokens[0];
    }

    public String getDepartmentCode() {
        String[] tokens = tokenizeDept(departmentDesc);
        if (tokens[1] != null) {
            return tokens[1];
        }
        return getDepartment();
    }

    public String getAssignedDoctorName() {
        String[] tokens = tokenizeDept(departmentDesc);
        return tokens[2];
    }

    public String getAssignedDoctorId() {
        String[] tokens = tokenizeDept(departmentDesc);
        return tokens[3];
    }

    public String getJMARICode() {
        String[] tokens = tokenizeDept(departmentDesc);
        return tokens[4];
    }

    private String[] tokenizeDept(String dept) {

        // 診療科名、コード、担当医名、担当医コード、JMARI コード
        // を格納する配列を生成する
        String[] ret = new String[5];
        Arrays.fill(ret, null);

        if (dept != null) {
            try {
                String[] params = dept.split("\\s*,\\s*");
                System.arraycopy(params, 0, ret, 0, params.length);

            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }

        return ret;
    }

    /********************************************/

    public void addAccessRight(AccessRightModel accessRight) {
        if (accessRights == null) {
            setAccessRights(new ArrayList<AccessRightModel>(3));
        }
        accessRights.add(accessRight);
    }

    public String getFirstConfirmDateTrimTime() {
        return open.dolphin.spring.model.core.ModelUtils.getDateAsString(getFirstConfirmDate());
    }

    public String getConfirmDateTrimTime() {
        return open.dolphin.spring.model.core.ModelUtils.getDateAsString(getConfirmDate());
    }

    public Boolean isHasMarkBoolean() {
        return hasMark;
    }

    public Boolean isHasImageBoolean() {
        return hasImage;
    }

    public Boolean isHasRpBoolean() {
        return hasRp;
    }

    public Boolean isHasTreatmentBoolean() {
        return hasTreatment;
    }

    public Boolean isHasLaboTestBoolean() {
        return hasLaboTest;
    }

    @Override
    public int hashCode() {
        return docId.hashCode() + 11;
    }

    @Override
    public boolean equals(Object other) {
        if (other != null && getClass() == other.getClass()) {
            return getDocId().equals(((DocInfoModel) other).getDocId());
        }
        return false;
    }

    @Override
    public int compareTo(Object other) {
        if (other != null && getClass() == other.getClass()) {
            Date val1 = getFirstConfirmDate();
            Date val2 = ((DocInfoModel) other).getFirstConfirmDate();
            int result = (val1 != null && val2 != null) ? val1.compareTo(val2) : 0;
            if (result == 0) {
                val1 = getConfirmDate();
                val2 = ((DocInfoModel) other).getConfirmDate();
                result = (val1 != null && val2 != null) ? val1.compareTo(val2) : 0;
            }
            return result;
        }
        return -1;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        DocInfoModel ret = new DocInfoModel();
        // ret.setAccessRights(this.getAccessRights());
        ret.setConfirmDate(this.getConfirmDate());
        ret.setDepartment(this.getDepartment());
        ret.setDepartmentCodeSys(this.getDepartmentCodeSys());
        ret.setDepartmentDesc(this.getDepartmentDesc());
        // ret.setDocPk(this.getDocPk());
        // ret.setDocId(this.getDocId()); //
        ret.setDocType(this.getDocType());
        ret.setFirstConfirmDate(this.getFirstConfirmDate());
        ret.setHandleClass(this.getHandleClass());
        ret.setHasImage(this.isHasImage());
        ret.setHasLaboTest(this.isHasLaboTest());
        ret.setHasMark(this.isHasMark());
        ret.setHasRp(this.isHasRp());
        ret.setHasTreatment(this.isHasTreatment());
        ret.setHealthInsurance(this.getHealthInsurance());
        ret.setHealthInsuranceCodeSys(this.getHealthInsuranceCodeSys());
        ret.setHealthInsuranceDesc(this.getHealthInsuranceDesc());
        ret.setHealthInsuranceGUID(this.getHealthInsuranceGUID());
        // ret.setParentId(this.getParentId());
        // ret.setParentIdCodeSys(this.getParentIdCodeSys());
        // ret.setParentIdDesc(this.getParentIdDesc());
        // ret.setParentIdRelation(this.getParentIdRelation());
        // ret.setParentPk(this.getParentPk()); //
        ret.setPurpose(this.getPurpose());
        ret.setPurposeCodeSys(this.getPurposeCodeSys());
        ret.setPurposeDesc(this.getPurposeDesc());
        ret.setStatus(this.getStatus());
        ret.setTitle(this.getTitle());
        ret.setVersionNotes(this.getVersionNotes());
        ret.setVersionNumber(this.getVersionNumber());
        return ret;

        // ret.setDocPk(this.getDocPk());
        // ret.setDocId(this.getDocId());
        // ret.setParentPk(this.getParentPk());
    }

    // minagawa^ 予定カルテ(予定カルテ対応)
    public boolean isScheduled() {
        boolean ret = (this.status != null &&
                this.status.equals(open.dolphin.spring.model.core.IInfoModel.STATUS_TMP) &&
                this.getFirstConfirmDate() != null &&
                this.getConfirmDate() != null &&
                this.getFirstConfirmDate().after(this.getConfirmDate()));
        return ret;
    }
    // minagawa$
}
