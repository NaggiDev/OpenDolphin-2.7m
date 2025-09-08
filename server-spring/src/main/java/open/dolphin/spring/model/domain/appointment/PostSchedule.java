package open.dolphin.spring.model.domain.appointment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * (予定カルテ対応)
 *
 * @author kazushi Minagawa.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostSchedule {

    private long pvtPK;

    private long ptPK;

    private Date scheduleDate;

    private long phPK;

    private boolean sendClaim;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("pvtPK=").append(pvtPK).append(",");
        sb.append("pvPK=").append(ptPK).append(",");
        sb.append("scheduleDate=").append(scheduleDate).append(",");
        sb.append("phPK=").append(phPK).append(",");
        sb.append("sendClaim=").append(sendClaim);
        return sb.toString();
    }
}
