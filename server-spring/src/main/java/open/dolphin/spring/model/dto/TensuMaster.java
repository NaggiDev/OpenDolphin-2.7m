package open.dolphin.spring.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;
import open.dolphin.spring.model.entity.ClaimConst;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TensuMaster extends InfoModel {

    private Integer hospnum;

    private String srycd;

    private String yukostymd;

    private String yukoedymd;

    private String name;

    private String kananame;

    private String taniname;

    private String tensikibetu;

    private String ten;

    private String ykzkbn;

    private String yakkakjncd;

    private String nyugaitekkbn;

    private String routekkbn;

    private String srysyukbn;

    private String hospsrykbn;

    public String getSlot() {

        if (srycd == null) {
            return null;
        }

        String ret;

        if (srycd.startsWith(ClaimConst.SYUGI_CODE_START)) {
            ret = ClaimConst.SLOT_SYUGI;

        } else if (srycd.startsWith(ClaimConst.YAKUZAI_CODE_START)) {
            // 内用1、外用6、注射薬4
            if (ykzkbn.equals(ClaimConst.YKZ_KBN_NAIYO)) {
                ret = ClaimConst.SLOT_NAIYO_YAKU;

            } else if (ykzkbn.equals(ClaimConst.YKZ_KBN_INJECTION)) {
                ret = ClaimConst.SLOT_TYUSHYA_YAKU;

            } else if (ykzkbn.equals(ClaimConst.YKZ_KBN_GAIYO)) {
                ret = ClaimConst.SLOT_GAIYO_YAKU;

            } else {
                ret = ClaimConst.SLOT_YAKUZAI;
            }

        } else if (srycd.startsWith(ClaimConst.ZAIRYO_CODE_START)) {
            ret = ClaimConst.SLOT_ZAIRYO;

        } else if (srycd.startsWith(ClaimConst.ADMIN_CODE_START)) {
            ret = ClaimConst.SLOT_YOHO;

        } else if (srycd.startsWith(ClaimConst.RBUI_CODE_START)) {
            ret = ClaimConst.SLOT_BUI;

        } else {
            ret = ClaimConst.SLOT_OTHER;
        }

        return ret;
    }
}
