package open.dolphin.spring.model.domain.appointment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;

/**
 * Appoint
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appoint extends InfoModel {

    // 予約名
    private String appName;

    // メモ
    private String memo;
}
