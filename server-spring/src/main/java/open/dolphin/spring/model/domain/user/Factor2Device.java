package open.dolphin.spring.model.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 *
 * @author kazushi Minagawa
 */
@Entity
@Table(name = "d_factor2_device")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Factor2Device implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // User PK
    private long userPK;

    // Name
    private String deviceName;

    // ２段階認証を行った端末の mac addrress
    private String macAddress;

    // 最初に２段階認証した日
    private String entryDate;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Factor2Device)) {
            return false;
        }
        Factor2Device other = (Factor2Device) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(userPK).append(",").append(macAddress).append(",").append(entryDate);
        return "open.dolphin.infomodel.Factor2Device[ id=" + id + " ]";
    }
}
