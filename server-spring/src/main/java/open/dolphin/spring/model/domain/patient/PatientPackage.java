package open.dolphin.spring.model.domain.patient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.domain.insurance.HealthInsuranceModel;
// TODO: Uncomment when AllergyModel is moved to domain/medical/diagnosis/
// import open.dolphin.spring.model.domain.medical.diagnosis.AllergyModel;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author kazushi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientPackage implements Serializable {

    private PatientModel patient;

    private List<HealthInsuranceModel> insurances;

    // TODO: Uncomment when AllergyModel is moved to domain/medical/diagnosis/
    // private List<AllergyModel> allergies;
}
