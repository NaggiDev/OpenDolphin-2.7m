package open.dolphin.spring.model.domain.appointment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.KarteEntryBean;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Entity
@Table(name = "d_appo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentModel extends KarteEntryBean {

    public static final int TT_NONE = 0;

    public static final int TT_NEW = 1;

    public static final int TT_HAS = 2;

    public static final int TT_REPLACE = 3;

    private String patientId;

    @Transient
    private int state;

    @Column(name = "c_name", nullable = false)
    private String name;

    private String memo;

    @Column(name = "c_date", nullable = false)
    @Temporal(value = TemporalType.DATE)
    private Date date;

    /**
     * 予約日で比較する。
     */
    @Override
    public int compareTo(Object o) {
        Date s1 = this.date;
        Date s2 = ((AppointmentModel) o).getDate();
        return s1.compareTo(s2);
    }

    @Override
    public String toString() {
        return open.dolphin.spring.model.core.ModelUtils.getDateAsString(getDate());
    }
}
