package open.dolphin.spring.model.domain.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.Embeddable;
import open.dolphin.spring.model.core.InfoModel;

/**
 * LicenseModel
 *
 * @author Minagawa,Kazushi
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class LicenseModel extends InfoModel {

    private String license;
    private String licenseDesc;
    private String licenseCodeSys;

    @Override
    public String toString() {
        return licenseDesc;
    }
}
