package open.dolphin.spring.model.domain.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;
import open.dolphin.spring.model.domain.user.UserModel;

import javax.persistence.*;
import java.util.Date;

/**
 * StampTreeModel
 * Userのパーソナルツリークラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Entity
@Table(name = "d_stamp_tree")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StampTreeModel extends InfoModel implements IStampTreeModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    // UserPK
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    // TreeSetの名称
    @Column(name = "tree_name", nullable = false)
    private String name;

    // OID or Public
    // OID の時は施設用
    private String publishType;

    // Treeのカテゴリ
    private String category;

    // 団体名等
    private String partyName;

    // URL
    private String url;

    // 説明
    private String description;

    // 公開した日
    @Temporal(value = TemporalType.DATE)
    private Date publishedDate;

    // 最終更新日
    @Temporal(value = TemporalType.DATE)
    private Date lastUpdated;

    // 公開しているtreeのエンティティ
    private String published;

    @Transient
    private String treeXml;

    @Column(nullable = false)
    @Lob
    private byte[] treeBytes;

    // minagawa^ 排他制御のための versionStr
    private String versionNumber;
    // minagawa$

    @Override
    public UserModel getUserModel() {
        return user;
    }

    @Override
    public void setUserModel(UserModel user) {
        this.user = user;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    // minagawa^ 排他制御
    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String num) {
        versionNumber = num;
    }
    // minagawa$

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + (int) (id ^ (id >>> 32));
        return result;
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
        final StampTreeModel other = (StampTreeModel) obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        StampTreeModel ret = new StampTreeModel();
        ret.setName("個人用");
        ret.setDescription("個人用のスタンプセットです");
        ret.setTreeXml(this.getTreeXml());
        ret.setTreeBytes(this.getTreeBytes());
        // ret.setUserModel(user);
        // ret.setPartyName(user.getFacilityModel().getFacilityName())
        // ret.setUrl(url)
        return ret;
    }
}
