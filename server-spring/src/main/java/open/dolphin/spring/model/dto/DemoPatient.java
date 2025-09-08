package open.dolphin.spring.model.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Demo patient model for testing and demonstration purposes.
 *
 * @author kazushi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "demo_patient")
public class DemoPatient implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String kana;

    private String email;

    private String sex;

    private String age;

    private String birthday;

    private String marital;

    private String address;

    private String addressCode;

    private String telephone;

    private String mobile;

    private String carrier;


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof DemoPatient)) {
            return false;
        }
        DemoPatient other = (DemoPatient) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "open.dolphin.spring.model.dto.DemoPatient[id=" + id + "]";
    }
}
