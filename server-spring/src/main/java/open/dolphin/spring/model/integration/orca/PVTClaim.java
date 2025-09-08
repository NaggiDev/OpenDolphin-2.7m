/*
 * PVTClaim.java
 *
 * Created on 2001/10/10, 13:57
 *
 * Last updated on 2002/12/31
 *
 */
package open.dolphin.spring.model.integration.orca;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;

import java.util.Vector;

/**
 * Simple Claim Class used for PVT.
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 *
 *         Modified by Mirror-I corp for adding 'claimDeptName' and related
 *         function to store/get Department name
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PVTClaim extends InfoModel {

    private static final long serialVersionUID = -8573272136025043849L;
    private String claimStatus;
    private String claimRegistTime;
    private String claimAdmitFlag;
    private String claimDeptName;
    private String claimDeptCode;
    private String assignedDoctorId;
    private String assignedDoctorName;
    private Vector claimAppName;
    private String claimAppMemo;
    private String claimItemCode;
    private String claimItemName;
    private String insuranceUid;
    private String jmariCode;

    @SuppressWarnings("unchecked")
    public void addClaimAppName(String val) {
        if (claimAppName == null) {
            claimAppName = new Vector(3);
        }
        claimAppName.add(val);
    }

    @Override
    public String toString() {

        StringBuilder buf = new StringBuilder();

        if (claimStatus != null) {
            buf.append("ClaimStatus: ");
            buf.append(claimStatus);
            buf.append("\n");
        }

        if (claimRegistTime != null) {
            buf.append("ClaimRegistTime: ");
            buf.append(claimRegistTime);
            buf.append("\n");
        }

        if (claimAdmitFlag != null) {
            buf.append("ClaimAdmitFlag: ");
            buf.append(claimAdmitFlag);
            buf.append("\n");
        }

        // Mirror-I start
        if (claimDeptName != null) {
            buf.append("claimDeptName: ");
            buf.append(claimDeptName);
            buf.append("\n");
        }
        // Mirror-I end

        if (claimAppName != null) {
            int len = claimAppName.size();
            for (int i = 0; i < len; i++) {
                buf.append("ClaimAppName: ");
                buf.append((String) claimAppName.get(i));
                buf.append("\n");
            }
        }

        if (claimAppMemo != null) {
            buf.append("ClaimAppointMemo: ");
            buf.append(claimAppMemo);
            buf.append("\n");
        }

        if (claimItemCode != null) {
            buf.append("ClaimItemCode: ");
            buf.append(claimItemCode);
            buf.append("\n");
        }

        if (claimItemName != null) {
            buf.append("ClaimItemName: ");
            buf.append(claimItemName);
            buf.append("\n");
        }

        return buf.toString();
    }
}
