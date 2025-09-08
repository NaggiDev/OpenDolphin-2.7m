package open.dolphin.spring.model.domain.communication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;

import javax.persistence.*;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Entity
@Table(name = "d_letter_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LetterItem extends InfoModel {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "c_value")
    private String value;

    @ManyToOne
    @JoinColumn(name = "module_id", nullable = false)
    private LetterModule module;

    public LetterItem(String name, String value) {
        this();
        this.name = name;
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LetterItem)) {
            return false;
        }
        LetterItem other = (LetterItem) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "open.dolphin.infomodel.DocumentItem[id=" + getId() + "]";
    }
}
