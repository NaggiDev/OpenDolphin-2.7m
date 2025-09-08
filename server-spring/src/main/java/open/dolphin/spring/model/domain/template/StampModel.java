package open.dolphin.spring.model.domain.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;

import javax.persistence.*;

/**
 * StampModel
 *
 * @author Minagawa,Kazushi
 */
@Entity
@Table(name = "d_stamp")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StampModel extends InfoModel {

    @Id
    private String id;

    // UserPK
    @Column(nullable = false)
    private long userId;

    @Column(nullable = false)
    private String entity;

    @Column(nullable = false)
    @Lob
    private byte[] stampBytes;

    // @Version
    // private int version;

    // public int getVersion() {
    // return version;
    // }
    //
    // public void setVersion(int version) {
    // this.version = version;
    // }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final StampModel other = (StampModel) obj;
        if (!id.equals(other.id)) {
            return false;
        }

        return true;
    }
}
