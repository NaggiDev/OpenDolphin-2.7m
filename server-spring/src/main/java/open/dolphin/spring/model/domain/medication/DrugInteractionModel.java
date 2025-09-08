package open.dolphin.spring.model.domain.medication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;

/**
 * 薬剤相互作用のモデル
 *
 * @author masuda, Masuda Naika
 */
@Data
@NoArgsConstructor
@Builder
public class DrugInteractionModel extends InfoModel {

    private String srycd1;
    private String srycd2;
    private String sskijo;
    private String syojyoucd;

    public DrugInteractionModel(String srycd1, String srycd2, String sskijo, String syojyoucd) {
        this();
        this.srycd1 = srycd1;
        this.srycd2 = srycd2;
        this.sskijo = sskijo;
        this.syojyoucd = syojyoucd;
    }
}
