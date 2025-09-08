/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.spring.model.domain.medical.diagnosis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;

import javax.persistence.*;

/**
 * バイタル対応
 *
 * @author Life Sciences Computing Corporation.
 */
@Entity
@Table(name = "d_vital")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VitalModel extends InfoModel implements Comparable<VitalModel> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    // 施設ID:患者ID
    @Column(nullable = false)
    private String facilityPatId;

    // カルテID
    private String karteID;

    // 体温 ℃
    private String bodyTemperature;

    // 血圧（収縮） mmHg
    private String bloodPressureSystolic;

    // 血圧（拡張） mmHg
    private String bloodPressureDiastolic;

    // 心拍数 回/分
    private String pulseRate;

    // SpO2 %
    private String spo2;

    // 呼吸数 回/分
    private String respirationRate;

    // 疼痛 5段階
    private String algia;

    // 気分 5段階
    private String feel;

    // 睡眠 5段階
    private String sleep;

    // 食事 5段階
    private String meal;

    // 排泄 5段階
    private String egestion;

    // PS 5段階
    private String ps;

    // 日付(yyyy-MM-dd)
    private String vitalDate;

    // 時間(HH:mm:ss)
    private String vitalTime;

    // 身長 cm
    private String height;

    // 体重 kg
    private String weight;

    // 保存日時
    private String saveDate;

    @Override
    public int compareTo(VitalModel other) {
        if (other != null) {
            String val1 = getVitalDate() + getVitalTime();
            String val2 = other.getVitalDate() + other.getVitalTime();
            return val1.compareTo(val2);
        }
        return 1;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("日時: ").append(this.getVitalDate()).append(" ").append(this.getVitalTime()).append("\n");
        if (this.getBodyTemperature() != null) {
            sb.append("体温: ").append(this.getBodyTemperature()).append("℃").append("\n");
        }
        // sb.append("血圧（収縮）: ").append(this.getBloodPressureSystolic()).append("
        // mmHg").append("\n");
        // sb.append("血圧（拡張）: ").append(this.getBloodPressureDiastolic()).append("
        // mmHg").append("\n");
        String bloodPressureSystolic = this.getBloodPressureSystolic();
        if (bloodPressureSystolic == null)
            bloodPressureSystolic = "";
        String bloodPressureDiastolic = this.getBloodPressureDiastolic();
        if (bloodPressureDiastolic == null)
            bloodPressureDiastolic = "";
        if (!bloodPressureSystolic.equals("") || !bloodPressureDiastolic.equals("")) {
            sb.append("血圧: ").append(bloodPressureSystolic).append(" / ").append(bloodPressureDiastolic).append(" mmHg")
                    .append("\n");
        }
        if (this.getPulseRate() != null) {
            sb.append("心拍数: ").append(this.getPulseRate()).append(" 回/分").append("\n");
        }
        if (this.getSpo2() != null) {
            sb.append("SpO2: ").append(this.getSpo2()).append(" %").append("\n");
        }
        if (this.getRespirationRate() != null) {
            sb.append("呼吸数: ").append(this.getRespirationRate()).append(" 回/分").append("\n");
        }
        if (this.getHeight() != null && this.getHeight().length() > 0) {
            sb.append("身長: ").append(this.getHeight()).append(" cm").append("\n");
        }
        if (this.getWeight() != null && this.getWeight().length() > 0) {
            sb.append("体重: ").append(this.getWeight()).append(" kg").append("\n");
        }
        if (this.getAlgia() != null) {
            sb.append("疼痛: ").append(this.getAlgia()).append(" /5段階").append("\n");
        }
        if (this.getFeel() != null) {
            sb.append("気分: ").append(this.getFeel()).append(" /5段階").append("\n");
        }
        if (this.getSleep() != null) {
            sb.append("睡眠: ").append(this.getSleep()).append(" /5段階").append("\n");
        }
        if (this.getMeal() != null) {
            sb.append("食事: ").append(this.getMeal()).append(" /5段階").append("\n");
        }
        if (this.getEgestion() != null) {
            sb.append("排泄: ").append(this.getEgestion()).append(" /5段階").append("\n");
        }
        if (this.getPs() != null) {
            sb.append("PS: ").append(this.getPs()).append(" /5段階").append("\n");
        }
        return sb.toString();
    }
}
