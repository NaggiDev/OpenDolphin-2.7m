package open.dolphin.spring.model.domain.insurance;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import open.dolphin.spring.model.core.InfoModel;
import open.dolphin.spring.model.domain.patient.PatientModel;

/**
 * HealthInsuranceModel
 *
 * @author Minagawa,kazushi.
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "d_health_insurance")
public class HealthInsuranceModel extends InfoModel {

    // PK
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    // PVTHealthInsuranceのバイナリー
    @Lob
    @Column(nullable = false)
    private byte[] beanBytes;

    // 患者
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientModel patient;
}
