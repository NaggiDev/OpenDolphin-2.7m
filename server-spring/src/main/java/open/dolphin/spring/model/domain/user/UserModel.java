package open.dolphin.spring.model.domain.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import open.dolphin.spring.model.core.InfoModel;
import open.dolphin.spring.model.domain.facility.FacilityModel;

/**
 * UserModel
 *
 * @author Minagawa,Kazushi
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "d_users")
public class UserModel extends InfoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    /** composite businnes key */
    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private String password;

    private String sirName;

    private String givenName;

    @Column(nullable = false)
    private String commonName;

    @Embedded
    private LicenseModel licenseModel;

    @Embedded
    private DepartmentModel departmentValue;

    @Column(nullable = false)
    private String memberType;

    private String memo;

    @Column(nullable = false)
    @Temporal(value = TemporalType.DATE)
    private Date registeredDate;

    @Column(nullable = false)
    private String email;

    @ManyToOne
    @JoinColumn(name = "facility_id", nullable = false)
    private FacilityModel facility;

    public FacilityModel getFacilityModel() {
        return facility;
    }

    public void setFacilityModel(FacilityModel facility) {
        this.facility = facility;
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<RoleModel> roles;

    private String orcaId;

    // ヒロクリニック^
    /** 麻薬施用者免許証番号 */
    // @001 2009/09/10 仕様追加：処方せん備考欄に麻薬施用者を表記する必要があるため、ユーザ登録の項目として麻薬施用者免許証番号を追加
    private String useDrugId;
    // ヒロクリニック$

    // minagawa^ ２段階認証
    private String factor2Auth;

    private String mainMobile;

    private String subMobile;

    // minagawa$

    /**
     * 施設IDを除いたIDを返す。
     *
     * @return 施設IDを除いたID
     */
    public String idAsLocal() {
        int index = userId.indexOf(COMPOSITE_KEY_MAKER);
        return userId.substring(index + 1);
    }

    /**
     * ユーザロールを追加する。
     *
     * @param value ユーザロール
     */
    public void addRole(RoleModel value) {
        if (roles == null) {
            roles = new ArrayList<>(1);
        }
        roles.add(value);
    }

    /**
     * 簡易ユーザ情報を返す。
     *
     * @return 簡易ユーザ情報
     */
    public UserLiteModel getLiteModel() {
        UserLiteModel model = new UserLiteModel();
        model.setUserId(getUserId());
        model.setCommonName(getCommonName());
        LicenseModel lm = new LicenseModel();
        lm.setLicense(getLicenseModel().getLicense());
        lm.setLicenseDesc(getLicenseModel().getLicenseDesc());
        lm.setLicenseCodeSys(getLicenseModel().getLicenseCodeSys());
        model.setLicenseModel(lm);
        return model;
    }
}
