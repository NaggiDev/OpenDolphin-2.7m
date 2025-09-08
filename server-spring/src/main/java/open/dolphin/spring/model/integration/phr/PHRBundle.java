package open.dolphin.spring.model.integration.phr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * ClaimBundle 要素クラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PHRBundle extends PHRModel {

    // Document
    private String catchId;

    // Module Entry情報
    private String bundleId;
    private String started;
    private String confirmed;
    private String status;

    // ModuleInfo
    private String ent;
    private String role;
    private int numberAsStamp;

    // 診療行為名
    private String clsName;

    // 診療行為コード
    private String clsCode;

    // コード体系
    private String clsCodeSystem;

    // 用法
    private String admin;

    // 用法コード
    private String adminCode;

    // 用法コード体系
    private String adminCodeSystem;

    // 用法メモ
    private String adminMemo;

    // バンドル数
    private String bundleNumber;

    // バンドル構成品目
    private List<PHRClaimItem> claimItems;

    // メモ
    private String memo;

    // 保険種別
    private String insurance;

    // = Entity
    private String orderName;

    public void addPHRClaimItem(PHRClaimItem phrClaimItem) {
        this.claimItems.add(phrClaimItem);
    }
}
