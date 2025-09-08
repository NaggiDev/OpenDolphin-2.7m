/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.spring.model.domain.medical.diagnosis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;

import java.util.List;

/**
 * バイタル対応
 *
 * @author Life Sciences Computing Corporation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VitalList extends InfoModel {

    private List<VitalModel> list;
}
