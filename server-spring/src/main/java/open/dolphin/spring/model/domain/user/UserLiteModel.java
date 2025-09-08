package open.dolphin.spring.model.domain.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;

import open.dolphin.spring.model.core.InfoModel;

/**
 * UserLiteModel
 *
 * @author Minagawa,Kazushi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class UserLiteModel extends InfoModel {

    private String userId;
    private String commonName;
    private LicenseModel licenseModel;
}
