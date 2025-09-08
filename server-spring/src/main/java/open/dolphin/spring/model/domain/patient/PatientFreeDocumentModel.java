/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.spring.model.domain.patient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;

import javax.persistence.*;
import java.util.Date;

/**
 * サマリー対応
 *
 * @author S.Oh@Life Sciences Computing Corporation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "d_patient_freedocument")
public class PatientFreeDocumentModel extends InfoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String facilityPatId;

    @Column(nullable = false)
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date confirmed;

    @Lob
    private String comment;
}
