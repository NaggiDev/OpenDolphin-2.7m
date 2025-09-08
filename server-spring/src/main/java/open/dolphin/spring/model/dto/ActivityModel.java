package open.dolphin.spring.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 *
 * @author kazushi Minagawa. LSC
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityModel implements java.io.Serializable {

    // M=Month Y=Year T=Total
    private String flag;
    private int year;
    private int month;
    private Date fromDate;
    private Date toDate;

    private String facilityId;
    private String facilityName;
    private String facilityZip;
    private String facilityAddress;
    private String facilityTelephone;
    private String facilityFacimile;

    private long numOfUsers;

    private long numOfPatients;

    private long numOfPatientVisits;

    private long numOfKarte;

    private long numOfImages;

    private long numOfAttachments;

    private long numOfDiagnosis;

    private long numOfLetters;

    private long numOfLabTests;

    private String dbSize;

    private String bindAddress;
}
