package open.dolphin.spring.model.domain.facility;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;

/**
 * AccessLicenseeModel
 *
 * @author Kazushi Minagawa
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessLicenseeModel extends InfoModel {

    private String code;
    private String name;
    private String type;
}
