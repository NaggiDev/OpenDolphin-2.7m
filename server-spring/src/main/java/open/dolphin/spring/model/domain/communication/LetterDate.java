package open.dolphin.spring.model.domain.communication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Entity
@Table(name = "d_letter_date")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LetterDate extends InfoModel {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "c_value")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date value;

    @ManyToOne
    @JoinColumn(name = "module_id", nullable = false)
    private LetterModule module;

    public LetterDate(String name, Date value) {
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
        if (!(object instanceof LetterDate)) {
            return false;
        }
        LetterDate other = (LetterDate) object;
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
