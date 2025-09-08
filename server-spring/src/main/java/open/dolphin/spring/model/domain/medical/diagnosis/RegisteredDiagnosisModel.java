package open.dolphin.spring.model.domain.medical.diagnosis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.KarteEntryBean;
import open.dolphin.spring.model.core.ModelUtils;
import open.dolphin.spring.model.domain.patient.PatientLiteModel;
import open.dolphin.spring.model.domain.user.UserLiteModel;

import javax.persistence.*;

/**
 * 診断履歴クラス。
 *
 * @author Kazushi Minagawa, Digital Globe,Inc.
 */
@Entity
@Table(name = "d_diagnosis")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisteredDiagnosisModel extends KarteEntryBean {

    // 疾患名
    @Column(nullable = false)
    private String diagnosis;

    // 疾患コード
    private String diagnosisCode;

    // 疾患コード体系名
    private String diagnosisCodeSystem;

    // 病名分類モデル
    @Embedded
    private DiagnosisCategoryModel diagnosisCategoryModel;

    // 転帰モデル
    @Embedded
    private DiagnosisOutcomeModel diagnosisOutcomeModel;

    // 疾患の初診日
    private String firstEncounterDate;

    // 関連健康保険情報
    private String relatedHealthInsurance;

    // 患者情報
    @Transient
    private PatientLiteModel patientLiteModel;

    // 担当医情報（=ログインユーザー）
    @Transient
    private UserLiteModel userLiteModel;

    // s.oh^ 2014/03/13 傷病名削除診療科対応
    private String department;
    private String departmentDesc;
    // s.oh$

    /**
     * 有効なモデルかどうかを返す。
     *
     * @return 有効なモデルの時 true
     */
    public boolean isValidMML() {
        return getDiagnosis() != null ? true : false;
    }

    /**
     * 分類名を返す。
     *
     * @return 分類名
     */
    public String getCategory() {
        return diagnosisCategoryModel != null ? diagnosisCategoryModel.getDiagnosisCategory() : null;
    }

    /**
     * 分類名を設定する。
     *
     * @param category 分類名
     */
    public void setCategory(String category) {
        if (diagnosisCategoryModel == null) {
            diagnosisCategoryModel = new DiagnosisCategoryModel();
        }
        this.diagnosisCategoryModel.setDiagnosisCategory(category);
    }

    /**
     * 分類説明を返す。
     *
     * @return 分類説明
     */
    public String getCategoryDesc() {
        return diagnosisCategoryModel != null ? diagnosisCategoryModel.getDiagnosisCategoryDesc() : null;
    }

    /**
     * 分類説明を設定する。
     *
     * @param categoryDesc 分類説明
     */
    public void setCategoryDesc(String categoryDesc) {
        if (diagnosisCategoryModel == null) {
            diagnosisCategoryModel = new DiagnosisCategoryModel();
        }
        this.diagnosisCategoryModel.setDiagnosisCategoryDesc(categoryDesc);
    }

    /**
     * 分類体系名を返す。
     *
     * @return 分類体系名
     */
    public String getCategoryCodeSys() {
        return diagnosisCategoryModel != null ? diagnosisCategoryModel.getDiagnosisCategoryCodeSys() : null;
    }

    /**
     * 分類体系名を設定する。
     *
     * @param categoryTable 分類体系名
     */
    public void setCategoryCodeSys(String categoryTable) {
        if (diagnosisCategoryModel == null) {
            diagnosisCategoryModel = new DiagnosisCategoryModel();
        }
        this.diagnosisCategoryModel.setDiagnosisCategoryCodeSys(categoryTable);
    }

    /**
     * 疾患開始日を返す。
     *
     * @return 疾患開始日
     */
    public String getStartDate() {
        if (getStarted() != null) {
            return ModelUtils.getDateAsString(getStarted());
        }
        return null;
    }

    /**
     * 疾患開始日を設定する。
     *
     * @param startDate 疾患開始日
     */
    public void setStartDate(String startDate) {
        if (startDate != null) {
            int index = startDate.indexOf('T');
            if (index < 0) {
                startDate += "T00:00:00";
            }
            // System.out.println(startDate);
            setStarted(ModelUtils.getDateTimeAsObject(startDate));
        }
    }

    /**
     * 疾患終了日を返す。
     *
     * @return 疾患終了日
     */
    public String getEndDate() {
        if (getEnded() != null) {
            return ModelUtils.getDateAsString(getEnded());
        }
        return null;
    }

    /**
     * 疾患終了日を設定する。
     *
     * @param endDate 疾患終了日
     */
    public void setEndDate(String endDate) {
        if (endDate != null && (!endDate.equals(""))) {
            int index = endDate.indexOf('T');
            if (index < 0) {
                endDate += "T00:00:00";
            }
            setEnded(ModelUtils.getDateTimeAsObject(endDate));

        } else {
            setEnded(null);
        }
    }

    /**
     * 転帰を返す。
     *
     * @return 転帰
     */
    public String getOutcome() {
        return diagnosisOutcomeModel != null ? diagnosisOutcomeModel.getOutcome() : null;
    }

    /**
     * 転帰を設定する。
     *
     * @param outcome 転帰
     */
    public void setOutcome(String outcome) {
        if (diagnosisOutcomeModel == null) {
            diagnosisOutcomeModel = new DiagnosisOutcomeModel();
        }
        this.diagnosisOutcomeModel.setOutcome(outcome);
    }

    /**
     * 転帰説明を返す。
     *
     * @return 転帰説明
     */
    public String getOutcomeDesc() {
        return diagnosisOutcomeModel != null ? diagnosisOutcomeModel.getOutcomeDesc() : null;
    }

    /**
     * 転帰説明を設定する。
     *
     * @param outcomeDesc 転帰説明を設定
     */
    public void setOutcomeDesc(String outcomeDesc) {
        if (diagnosisOutcomeModel == null) {
            diagnosisOutcomeModel = new DiagnosisOutcomeModel();
        }
        this.diagnosisOutcomeModel.setOutcomeDesc(outcomeDesc);
    }

    /**
     * 転帰体系を返す。
     *
     * @return 転帰体系
     */
    public String getOutcomeCodeSys() {
        return diagnosisOutcomeModel != null ? diagnosisOutcomeModel.getOutcomeCodeSys() : null;
    }

    /**
     * 転帰体系を設定する。
     *
     * @param outcomeTable
     */
    public void setOutcomeCodeSys(String outcomeTable) {
        if (diagnosisOutcomeModel == null) {
            diagnosisOutcomeModel = new DiagnosisOutcomeModel();
        }
        this.diagnosisOutcomeModel.setOutcomeCodeSys(outcomeTable);
    }

    private static String[] splitDiagnosis(String diagnosis) {
        if (diagnosis == null) {
            return null;
        }
        String[] ret = null;
        try {
            ret = diagnosis.split("\\s*,\\s*");
        } catch (Exception e) {
        }
        return ret;
    }

    public String getDiagnosisName() {
        String[] splits = splitDiagnosis(this.diagnosis);
        return (splits != null && splits.length == 2 && splits[0] != null) ? splits[0] : this.diagnosis;
    }

    public String getDiagnosisAlias() {
        String[] splits = splitDiagnosis(this.diagnosis);
        return (splits != null && splits.length == 2 && splits[1] != null) ? splits[1] : null;
    }

    public String getAliasOrName() {
        String[] aliasOrName = splitDiagnosis(this.diagnosis);
        if (aliasOrName != null && aliasOrName.length == 2 && aliasOrName[1] != null) {
            return aliasOrName[1];
        }
        return this.diagnosis;
    }

    public String toClipboard() {

        StringBuilder sb = new StringBuilder();
        sb.append(this.getDiagnosis());
        if (this.getStartDate() != null) {
            sb.append(",").append("疾患開始日:").append(this.getStartDate());
        }
        if (this.getEndDate() != null) {
            sb.append(",").append("疾患終了日:").append(this.getEndDate());
        }
        return sb.toString();
    }
}
