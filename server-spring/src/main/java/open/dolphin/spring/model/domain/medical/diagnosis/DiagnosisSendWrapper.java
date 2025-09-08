package open.dolphin.spring.model.domain.medical.diagnosis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;

import java.util.List;

/**
 * 病名を送信（DB保存＆CLAIM送信）をするためのラッパークラス。
 *
 * @author kazushi Minagawa.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnosisSendWrapper extends InfoModel {

    // flag
    private boolean sendClaim;

    // 確定日
    private String confirmDate;

    // MML DocInfo用の Title
    private String title;

    // MML DocInfo用の purpose
    private String purpose;

    // MML DocInfo用の groupId
    private String groupId;

    // 患者ID
    private String patientId;

    // 患者氏名
    private String patientName;

    // 患者性別
    private String patientGender;

    // 施設名
    private String facilityName;

    // JMARI code
    private String jamariCode;

    // 診療科コード
    private String department;

    // 診療科名
    private String departmentDesc;

    // 担当医名
    private String creatorName;

    // 担当医ID
    private String creatorId;

    // 担当医医療資格
    private String creatorLicense;

    // 新規に追加された病名のリスト
    private List<RegisteredDiagnosisModel> addedDiagnosis;

    // 更新された（転帰等）病名のリスト
    private List<RegisteredDiagnosisModel> updatedDiagnosis;

    // mianagawa^ LSC 1.4 傷病名の削除 2013/06/24
    // 削除された病名リスト
    private List<RegisteredDiagnosisModel> deletedDiagnosis;
}
