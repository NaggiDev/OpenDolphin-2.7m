package open.dolphin.spring.model.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;

/**
 * LastDateCount30 model for tracking patient activity statistics (30-day version).
 *
 * @author kazushi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class LastDateCount30 extends InfoModel {

    // システム登録日->初診日として使用
    private Date created;

    // 病名数
    private long diagnosisCount;

    // アクティブ病名数
    private long activeDiagnosisCount;

    // カル枚数
    private long docCount;

    // 最終カルテ記録日
    private Date lastDocDate;

    // 検査数
    private long labCount;

    // 最終検査結果日
    private String lastLabDate;

    // 画像及びシェーマ数
    private long imageCount;

    // 最終画像日
    private Date lastImageDate;

    private long allergyCount;

    private Date oldestOndoDate;
}
