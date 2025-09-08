package open.dolphin.spring.model.domain.medication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * ClaimItem 要素クラス。
 *
 * @author Kazushi Minagawa, Digital Globe,Inc.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "d_care_plan_item")
public class CarePlanItem extends InfoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    // 名称
    private String name;

    // コード
    private String code;

    // コード体系
    private String codeSystem;

    // 種別コード（薬剤｜手技｜材料）
    private String classCode;

    // 種別コードn体系
    private String classCodeSystem;

    // 数量
    private String number;

    // 単位
    private String unit;

    // 数量コード
    private String numberCode;

    // 数量コード体系
    private String numberCodeSystem;

    // メモ
    private String memo;

    // 薬剤区分 2011-02-10 追加
    private String ykzKbn;

    @ManyToOne
    @JoinColumn(name = "carePlan_id", nullable = false)
    private CarePlanModel carePlan; // mappedBy

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
        final CarePlanItem other = (CarePlanItem) obj;
        if (id != other.id)
            return false;
        return true;
    }

    public ClaimItem toClaimItem() {

        ClaimItem result = new ClaimItem();

        result.setClassCode(this.getClassCode());
        result.setClassCodeSystem(this.getClassCodeSystem());
        result.setCode(this.getCode());
        result.setCodeSystem(this.getCodeSystem());
        result.setName(this.getName());
        result.setNumber(this.getNumber());
        result.setNumberCode(this.getNumberCode());
        result.setNumberCodeSystem(this.getNumberCodeSystem());
        result.setUnit(this.getUnit());
        result.setYkzKbn(this.getYkzKbn());
        result.setMemo(this.getMemo());

        return result;
    }
}
