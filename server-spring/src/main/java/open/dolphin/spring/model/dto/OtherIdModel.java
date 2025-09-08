package open.dolphin.spring.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import open.dolphin.spring.model.core.InfoModel;
import open.dolphin.spring.model.domain.patient.PatientModel;

/**
 * OtherIdModel
 *
 * @author Minagawa,Kazushi
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "d_other_id")
public class OtherIdModel extends InfoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String otherId;
    private String idType;
    private String idTypeDesc;
    private String idTypeCodeSys;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private PatientModel patient;
}
