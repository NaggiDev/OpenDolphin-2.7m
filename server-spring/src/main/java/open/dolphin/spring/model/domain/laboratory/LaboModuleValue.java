package open.dolphin.spring.model.domain.laboratory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.KarteEntryBean;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * LaboModuleValue
 *
 */
@Entity
@Table(name = "d_labo_module")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaboModuleValue extends KarteEntryBean {

    // MMLのUID
    @Column(nullable = false, unique = true, length = 32)
    private String docId;

    @Transient
    private String patientId;

    @Transient
    private String patientIdType;

    @Transient
    private String patientIdTypeCodeSys;

    private String registId;

    private String sampleTime;

    private String registTime;

    private String reportTime;

    private String reportStatus;

    private String reportStatusCode;

    private String reportStatusCodeId;

    private String setName;

    private String setCode;

    private String setCodeId;

    private String clientFacility;

    private String clientFacilityCode;

    private String clientFacilityCodeId;

    private String laboratoryCenter;

    private String laboratoryCenterCode;

    private String laboratoryCenterCodeId;

    // private String confirmDate;

    @OneToMany(mappedBy = "laboModule", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    private Collection<LaboSpecimenValue> laboSpecimens;

    public void addLaboSpecimen(LaboSpecimenValue specimen) {
        if (laboSpecimens == null) {
            laboSpecimens = new ArrayList<LaboSpecimenValue>();
        }
        laboSpecimens.add(specimen);
    }

    /**
     * サンプルタイムで比較する。
     *
     * @return 比較値
     */
    public int compareTo(Object other) {
        if (other != null && getClass() == other.getClass()) {
            String sampleTime1 = getSampleTime();
            String sampleTime2 = ((LaboModuleValue) other).getSampleTime();
            if (sampleTime1 != null && sampleTime2 != null) {
                return sampleTime1.compareTo(sampleTime2);
            } else {
                String cf1 = getConfirmDate();
                String cf2 = ((LaboModuleValue) other).getConfirmDate();
                return cf1.compareTo(cf2);
            }
        }
        return -1;
    }
}
