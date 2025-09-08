package open.dolphin.spring.model.integration.phr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PHRLabItem implements Serializable {

    // 患者ID fid:Pid
    private String patientId;

    // 検体採取日
    private String sampleDate;

    // Labo コード
    private String labCode;

    // 乳ビ
    private String lipemia;

    // 溶血
    private String hemolysis;

    // 透析前後
    private String dialysis;

    // ステータス
    private String reportStatus;

    // グループコード
    private String groupCode;

    // グループ名称
    private String groupName;

    // 検査項目コード・親
    private String parentCode;

    // 検査項目コード
    private String itemCode;

    // MEDIS コード
    private String medisCode;

    // 検査項目名
    private String itemName;

    // 異常区分
    private String abnormalFlg;

    // 基準値
    private String normalValue;

    // 検査結果
    private String value;

    // 単位
    private String unit;

    // 検査材料コード
    private String specimenCode;

    // 検査材料名
    private String specimenName;

    // コメントコード1
    private String commentCode1;

    // コメント1
    private String comment1;

    // コメントコード2
    private String commentCode2;

    // コメント2
    private String comment2;

    // Sort Key
    private String sortKey;

    private String module_id;
}
