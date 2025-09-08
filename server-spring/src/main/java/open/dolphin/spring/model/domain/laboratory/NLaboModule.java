package open.dolphin.spring.model.domain.laboratory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kazushi Minagawa @digital-globe.co.jp
 */
@Entity
@Table(name = "d_nlabo_module")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NLaboModule extends InfoModel {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // 患者ID fid:Pid
    @Column(nullable = false)
    private String patientId;

    // ラボコード
    private String laboCenterCode;

    // 患者氏名
    private String patientName;

    // 患者性別
    private String patientSex;

    // 検体採取日または検査受付日時
    private String sampleDate;

    // この検査モジュールに含まれている検査項目の数
    private String numOfItems;

    // Module Key
    private String moduleKey;

    // Report format
    private String reportFormat;

    @OneToMany(mappedBy = "laboModule", cascade = { CascadeType.ALL })
    private List<NLaboItem> items;

    @Transient
    private Boolean progressState;

    // minagawa^ 入院
    @Transient
    private String facilityId;

    @Transient
    private String facilityName;

    @Transient
    private String jmariCode;
    // minagawa$

    public void addItem(NLaboItem item) {

        if (this.items == null) {
            this.items = new ArrayList<NLaboItem>();
        }

        this.items.add(item);
    }

    /**
     * 引数で指定された検査コードを持つ NLaboItemを返す。
     *
     * @param testCode 検査コード
     * @return 該当するNLaboItem
     */
    public NLaboItem getTestItem(String testCode) {

        if (items == null || items.isEmpty()) {
            return null;
        }

        NLaboItem ret = null;

        for (NLaboItem item : items) {
            if (item.getItemCode().equals(testCode)) {
                ret = item;
                break;
            }
        }

        return ret;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof NLaboModule)) {
            return false;
        }
        NLaboModule other = (NLaboModule) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "oms.ehr.entity.LaboModule[id=" + id + "]";
    }
}
