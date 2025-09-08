package open.dolphin.spring.model.domain.laboratory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;

import javax.persistence.*;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Entity
@Table(name = "d_nlabo_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NLaboItem extends InfoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // 患者ID fid:Pid
    @Column(nullable = false)
    private String patientId;

    // 検体採取日
    @Column(nullable = false)
    private String sampleDate;

    // Labo コード
    private String laboCode;

    // 乳ビ
    private String lipemia;

    // 溶血
    private String hemolysis;

    // 透析前後
    private String dialysis;

    // ステータス
    private String reportStatus;

    // グループコード
    @Column(nullable = false)
    private String groupCode;

    // グループ名称
    private String groupName;

    // 検査項目コード・親
    @Column(nullable = false)
    private String parentCode;

    // 検査項目コード
    @Column(nullable = false)
    private String itemCode;

    // MEDIS コード
    private String medisCode;

    // 検査項目名
    @Column(nullable = false)
    private String itemName;

    // 異常区分
    private String abnormalFlg;

    // 基準値
    private String normalValue;

    // 検査結果
    @Column(name = "c_value")
    private String value;

    // 単位
    private String unit;

    // 検査材料コード
    private String specimenCode;

    // 検査材料名
    private String specimenName;

    // コメントコード1
    private String commentCode1;

    // コメント1
    private String comment1;

    // コメントコード2
    private String commentCode2;

    // コメント2
    private String comment2;

    // Sort Key
    private String sortKey;

    @ManyToOne
    @JoinColumn(name = "laboModule_id", nullable = false)
    private NLaboModule laboModule;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof NLaboItem)) {
            return false;
        }
        NLaboItem other = (NLaboItem) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "oms.ehr.entity.AbstractEntry[id=" + id + "]";
    }
}
