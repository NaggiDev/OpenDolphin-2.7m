package open.dolphin.spring.model.integration.phr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;

import javax.persistence.*;

/**
 * Digital Globe OID.
 *
 * @author Minagawa,Kazushi
 *
 */
@Entity
@Table(name = "d_oid")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DgOid extends InfoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String baseOid;

    @Column(nullable = false)
    private int nextNumber;

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + (int) (id ^ (id >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final DgOid other = (DgOid) obj;
        if (id != other.id)
            return false;
        return true;
    }
}
