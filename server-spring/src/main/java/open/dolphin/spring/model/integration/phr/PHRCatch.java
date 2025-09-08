package open.dolphin.spring.model.integration.phr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kazushi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PHRCatch implements java.io.Serializable {

    // Entry情報
    private String catchId;
    private String started;
    private String confirmed;
    private String status;

    // 患者情報
    private String patientNumber;
    private String patientId;
    private String patientName;
    private String patientSex;
    private String patientBirthday;
    private String patientZipCode;
    private String patientAddress;
    private String patientTelephone;

    // 医療機関情報
    private String facilityNumber;
    private String facilityId;
    private String facilityName;
    private String facilityZipCode;
    private String facilityAddress;
    private String facilityTelephone;

    // 担当医情報
    private String physicianId;
    private String physicianName;
    private String department;
    private String departmentDesc;
    private String license;

    // 処方 Reply
    private int rpRequest;
    private int rpReply;
    private String rpReplyTo;

    // PHRModule oneToMany
    private List<PHRBundle> bundles;

    public void addBundle(PHRBundle b) {
        this.bundles.add(b);
    }
}
