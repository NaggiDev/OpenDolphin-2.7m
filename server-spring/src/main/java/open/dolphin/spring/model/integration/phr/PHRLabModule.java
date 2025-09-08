package open.dolphin.spring.model.integration.phr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author kazushi Minagawa @digital-globe.co.jp
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PHRLabModule implements Serializable {

    // Module ID
    private String catchId;

    // 患者氏名
    private String patientName;

    // 患者性別
    private String patientSex;

    // 患者生年月日
    private String patientBirthday;

    // 医療機関ID
    private String facilityId;

    // 医療機関名
    private String facilityName;

    // JMARI等
    private String facilityNumber;

    // ラボコード
    private String labCenterCode;

    // 検体採取日または検査受付日時
    private String sampleDate;

    // この検査モジュールに含まれている検査項目の数
    private String numOfItems;

    // Report format
    private String reportFormat;

    // ManyToOne
    private List<PHRLabItem> testItems;

    public void addTestItem(PHRLabItem phrLabItem) {
        this.testItems.add(phrLabItem);
    }
}
