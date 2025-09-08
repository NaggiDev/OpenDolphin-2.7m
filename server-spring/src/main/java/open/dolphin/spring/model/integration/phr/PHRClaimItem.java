package open.dolphin.spring.model.integration.phr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClaimItem 要素クラス。
 *
 * @author Kazushi Minagawa, Digital Globe,Inc.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PHRClaimItem implements java.io.Serializable {

    // 名称
    private String name;

    // コード
    private String code;

    // コード体系
    private String codeSystem;

    // 種別コード（薬剤｜手技｜材料）
    private String clsCode;

    // 種別コード体系
    private String clsCodeSystem;

    // 数量 ios=quantity od=number
    private String quantity;

    // 単位
    private String unit;

    // 数量コード
    private String numberCode;

    // 数量コード体系
    private String numberCodeSystem;

    // メモ
    private String memo;

    // 薬剤区分
    private String ykzKbn;
}
