package open.dolphin.spring.model.domain.medication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;
import open.dolphin.spring.model.domain.medical.ModuleModel;
import open.dolphin.spring.model.domain.medical.ModuleInfoBean;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author kazushi Minagawa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "d_care_plan")
public class CarePlanModel extends InfoModel {

    // PK
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date startDate;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date endDate;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date created;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date updated;

    private long karteId;

    private String userId;

    private String commonName;

    private String status;

    // 頻度
    private Integer frequency;

    // Entity
    private String entity;

    // 診療行為セットにユーザーがつける名前 (ex. 風邪セット etc.) == stampName
    private String stampName;

    // --------------------------------------------------------
    // 以下 Bundle 情報
    // --------------------------------------------------------

    // 診療行為名 CLAIM規格
    private String className;

    // 診療行為コード CLAIM規格
    private String classCode;

    // 診療行為コード体系 CLAIM規格
    private String classCodeSystem;

    // 用法
    private String administration;

    // 用法コード
    private String adminCode;

    // 用法コード体系
    private String adminCodeSystem;

    // 用法メモ
    private String adminMemo;

    // バンドル数
    private String bundleNumber;

    // メモ
    private String memo;

    // 保険種別
    private String insurance;

    // order name（日本語）
    private String orderName;

    @OneToMany(mappedBy = "carePlan", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<CarePlanItem> carePlanItems;

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
        final CarePlanModel other = (CarePlanModel) obj;
        if (id != other.id)
            return false;
        return true;
    }

    public ModuleModel toModleModel() {

        ModuleModel result = new ModuleModel();
        ModuleInfoBean info = result.getModuleInfoBean();

        info.setEntity(this.getEntity());
        info.setStampName(this.getStampName());
        info.setStampRole(IInfoModel.ROLE_P);

        ClaimBundle bundle = (this.getEntity().equals(IInfoModel.ENTITY_MED_ORDER))
                ? new BundleMed()
                : new BundleDolphin();
        result.setModel(bundle);

        bundle.setAdmin(this.getAdministration());
        bundle.setAdminCode(this.getAdminCode());
        bundle.setAdminCodeSystem(this.getAdminCodeSystem());
        bundle.setAdminMemo(this.getMemo());
        bundle.setBundleNumber(this.getBundleNumber());
        bundle.setClassCode(this.getClassCode());
        bundle.setClassCodeSystem(this.getClassCodeSystem());
        bundle.setClassName(this.getClassName());
        bundle.setInsurance(this.getInsurance());
        bundle.setMemo(this.getMemo());

        Iterator<CarePlanItem> iter = this.getCarePlanItems().iterator();
        List<ClaimItem> list = new ArrayList<>();
        while (iter.hasNext()) {
            CarePlanItem item = iter.next();
            list.add(item.toClaimItem());
        }

        ClaimItem[] items = list.toArray(new ClaimItem[0]);
        bundle.setClaimItem(items);

        return result;
    }
}
