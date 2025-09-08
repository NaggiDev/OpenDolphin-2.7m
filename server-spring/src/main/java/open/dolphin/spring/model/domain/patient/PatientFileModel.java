package open.dolphin.spring.model.domain.patient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 *
 * @author Kazushi Minagawa.
 */
// @Entity
// @Table(name="d_patient_file")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientFileModel implements Serializable {

    private static final long serialVersionUID = 1L;

    // @Id
    // @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String docType;
    private String contentType;
    private long contentSize;
    private long lastModified;
    private String digest;
    private String memo;
    private String fileName; // original file name

    // @Transient
    private String location;

    private String extension;

    // @Lob
    private byte[] fileData;

    // @ManyToOne は止め
    private long patient_id;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PatientFileModel)) {
            return false;
        }
        PatientFileModel other = (PatientFileModel) object;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "open.dolphin.infomodel.PatientFile[ id=" + id + " ]";
    }
}
