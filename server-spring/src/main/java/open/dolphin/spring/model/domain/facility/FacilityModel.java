package open.dolphin.spring.model.domain.facility;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;

import java.util.Date;
import javax.persistence.*;
import open.dolphin.spring.model.core.InfoModel;

/**
 * FacilityModel
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
@Table(name = "d_facility")
public class FacilityModel extends InfoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    /** Business Key */
    @Column(nullable = false, unique = true)
    private String facilityId;

    // 医療機関名
    @Column(nullable = false)
    private String facilityName;

    // 郵便番号
    @Column(nullable = false)
    private String zipCode;

    // 住所
    @Column(nullable = false)
    private String address;

    // 電話番号
    @Column(nullable = false)
    private String telephone;

    // FAX
    private String facsimile;

    // URL
    private String url;

    // システム登録日
    @Column(nullable = false)
    @Temporal(value = TemporalType.DATE)
    private Date registeredDate;

    // メンバータイプ
    @Column(nullable = false)
    private String memberType;

    // S3 アカウント
    private String s3URL;
    private String s3AccessKey;
    private String s3SecretKey;

    // // 保健医療機関コード 7桁
    // private String insuraceFacilityId;
    //
    // // JMARIコード 12桁
    // private String jmariCode;
}
