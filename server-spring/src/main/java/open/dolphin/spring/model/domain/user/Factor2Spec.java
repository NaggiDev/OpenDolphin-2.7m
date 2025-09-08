package open.dolphin.spring.model.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author kazushi Minagawa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Factor2Spec implements java.io.Serializable {

    // User's primary key
    private long userPK;

    private String code;

    private String phoneNumber;

    private String deviceName;

    private String macAddress;

    private String entryDate;

    private String factor2Auth;

    private String backupKey;

    // 信頼デバイス追加時のfid:uid
    private String userId;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(phoneNumber).append(",").append(macAddress).append(",").append(entryDate).append(",")
                .append(factor2Auth);
        return sb.toString();
    }
}
