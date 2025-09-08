package open.dolphin.spring.model.domain.laboratory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * LaboSpecimenValue
 *
 * @author Minagawa,Kazushi
 *
 */
@Entity
@Table(name = "d_labo_specimen")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaboSpecimenValue extends InfoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name = "module_id", nullable = false)
    private LaboModuleValue laboModule;

    private String specimenName;

    private String specimenCode;

    private String specimenCodeId;

    @OneToMany(mappedBy = "laboSpecimen", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    private Collection<LaboItemValue> laboItems;

    public void addLaboItem(LaboItemValue item) {
        if (laboItems == null) {
            laboItems = new ArrayList<LaboItemValue>();
        }
        laboItems.add(item);
    }
}
