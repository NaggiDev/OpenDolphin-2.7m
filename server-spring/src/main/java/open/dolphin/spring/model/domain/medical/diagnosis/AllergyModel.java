package open.dolphin.spring.model.domain.medical.diagnosis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;

/**
 * AllergyModel
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllergyModel extends InfoModel implements Comparable<AllergyModel> {

    // Observation ID
    private long observationId;

    // 要因
    private String factor;

    // 反応程度
    private String severity;

    // コード体系
    private String severityTableId;

    // 同定日
    private String identifiedDate;

    // メモ
    private String memo;

    /**
     * 同定日で比較する。
     *
     * @param other 比較対象オブジェクト
     * @return 比較値
     */
    @Override
    public int compareTo(AllergyModel other) {
        if (other != null) {
            String val1 = getIdentifiedDate();
            String val2 = other.getIdentifiedDate();
            return val1.compareTo(val2);
        }
        return 1;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(this.getFactor()).append(",");
        sb.append(this.getSeverity());
        if (this.getIdentifiedDate() != null) {
            sb.append(",").append(this.getIdentifiedDate());
        }
        if (this.getMemo() != null) {
            sb.append(",").append(this.getMemo());
        }
        return sb.toString();
    }
}
