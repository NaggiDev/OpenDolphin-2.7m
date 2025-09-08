package open.dolphin.spring.model.domain.communication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;
import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Entity
@Table(name = "d_letter_text")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LetterText extends InfoModel {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Lob
    private String textValue;

    @ManyToOne
    @JoinColumn(name = "module_id", nullable = false)
    private LetterModule module;

    public LetterText(String name, String textValue) {
        this();
        this.name = name;
        this.textValue = textValue;
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
        if (!(object instanceof LetterText)) {
            return false;
        }
        LetterText other = (LetterText) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "open.dolphin.infomodel.DocumentText[id=" + getId() + "]";
    }
}
