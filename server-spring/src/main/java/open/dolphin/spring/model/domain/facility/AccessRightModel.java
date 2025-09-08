package open.dolphin.spring.model.domain.facility;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;

/**
 * AccessRightModel
 *
 * @author Kazushi Minagawa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessRightModel extends InfoModel {

    // 許可
    private String permission;

    // 開始日
    private String startDate;

    // 終了日
    private String endDate;

    // 医療資格コード
    private String licenseeCode;

    // 医療資格名
    private String licenseeName;

    // 医療資格コード体系
    private String licenseeCodeType;
}
