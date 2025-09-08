package open.dolphin.spring.model.domain.laboratory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;

import javax.persistence.*;

/**
 * LaboItemValue
 *
 * @author Minagawa,Kazushi
 *
 */
@Entity
@Table(name = "d_labo_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaboItemValue extends InfoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name = "specimen_id", nullable = false)
    private LaboSpecimenValue laboSpecimen;

    private String itemName;

    private String itemCode;

    private String itemCodeId;

    private String acode;

    private String icode;

    private String scode;

    private String mcode;

    private String rcode;

    private String itemValue;

    private String up;

    private String low;

    private String normal;

    private String nout;

    private String unit;

    private String unitCode;

    private String unitCodeId;
}
