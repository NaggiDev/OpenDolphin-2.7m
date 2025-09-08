package open.dolphin.spring.model.domain.patient;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;

import open.dolphin.spring.model.core.InfoModel;

/**
 * PatientLiteModel
 *
 * @author Minagawa, kazushi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public final class PatientLiteModel extends InfoModel {

    private String patientId;
    private String fullName;
    private String kanaName;
    private String gender;
    private String genderDesc;
    private String birthday;
}
