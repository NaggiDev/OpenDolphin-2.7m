package open.dolphin.spring.model.domain.patient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.KarteEntryBean;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import org.hibernate.annotations.Type;

/**
 * MemoModel
 *
 * @author Minagawa, Kazushi
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "d_patient_memo")
public class PatientMemoModel extends KarteEntryBean {

    // DolphinPro と crala OpenDolphin -> @Lobアノテーションをつける
    // OpenDolphin ASP アノテーションなし
    private String memo;
    // masuda^
    @Lob
    private String memo2;

    public String getMemo() {
        return memo2 != null ? memo2 : memo;
    }

    public void setMemo(String memo) {
        this.memo2 = memo;
    }
    // masuda$
}
