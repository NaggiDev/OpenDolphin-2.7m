package open.dolphin.spring.model.domain.patient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;

import java.util.Date;

/**
 *
 * @author Kazushi Minagawa.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KarteNumber extends InfoModel {

    // KarteBeanのPK
    private long karteNumber;

    // システム登録日
    private Date created;

    // 保健医療機関コードとJMARIコードの連結
    private String number;
}
