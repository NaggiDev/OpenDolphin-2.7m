package open.dolphin.spring.model.domain.medication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;
import open.dolphin.spring.model.domain.insurance.PVTHealthInsuranceModel;

import java.util.Date;
import java.util.List;

/**
 *
 * @author kazushi Minagawa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriscriptionModel extends InfoModel {

    // 患者ID
    private String patientId;

    // 患者氏名
    private String patientName;

    // 患者カナ
    private String patientKana;

    // 患者性別(男｜女)
    private String patientSex;

    // 患者生年月日 yyyy-MM-dd
    private String patientBirthday;

    // 患者郵便番号
    private String patientZipcode;

    // 患者住所
    private String patientAddress;

    // 患者電話
    private String patientTelephone;

    // 処方リスト
    private List<BundleMed> priscriptionList;

    // 適用保険
    private PVTHealthInsuranceModel applyedInsurance;

    // 責任医師
    private String physicianName;

    // 麻薬免許
    private String drugLicenseNumber;

    // 医療機関名
    private String institutionName;

    // 医療機関郵便番号
    private String institutionZipcode;

    // 医療機関住所
    private String institutionAddress;

    // 医療機関電話番号
    private String institutionTelephone;

    // 保険医療機関番号
    private String InstitutionNumber;

    // 交付日
    private Date issuanceDate;

    // 使用期間
    private Date period;

    // 備考欄患者住所、氏名転記フラグ
    private boolean chkPatientInfo;

    // 備考欄患者麻薬施用者転記フラグ
    private boolean chkUseDrugInfo;

    // 備考欄に「在宅」を記載するかどうかのフラグ
    private boolean chkHomeMedical;

    private boolean useGeneraklName;
}
