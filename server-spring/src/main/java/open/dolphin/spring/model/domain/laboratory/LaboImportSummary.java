package open.dolphin.spring.model.domain.laboratory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.domain.patient.PatientModel;

import java.io.Serializable;

/**
 * LaboImportSummary
 *
 * @author Minagawa,Kazushi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaboImportSummary implements Serializable {

    private static final long serialVersionUID = 8730078673332969884L;

    private String patientId;

    private PatientModel patient;

    private String setName;

    private String specimenName;

    private String sampleTime;

    private String reportTime;

    private String reportStatus;

    private String laboratoryCenter;

    private String result;

    public String getPatientBirthday() {
        return this.getPatient().getBirthday();
    }

    public String getPatientGender() {
        return this.getPatient().getGenderDesc();
    }

    public String getPatientName() {
        return this.getPatient().getFullName();
    }

    public String getReportTime() {
        int index = reportTime.indexOf('T');
        return index > 0 ? reportTime.substring(0, index) : reportTime;
    }

    public String getSampleTime() {
        int index = sampleTime.indexOf('T');
        return index > 0 ? sampleTime.substring(0, index) : sampleTime;
    }
}
