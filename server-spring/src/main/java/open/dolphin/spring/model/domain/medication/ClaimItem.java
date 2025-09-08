/*
 * ClaimItem.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2003,2004 Digital Globe, Inc. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package open.dolphin.spring.model.domain.medication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;

import java.util.List;

/**
 * ClaimItem 要素クラス。
 *
 * @author Kazushi Minagawa, Digital Globe,Inc.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimItem extends InfoModel {

    private static final String DISPOSE_UNIT = "管";

    // 名称
    private String name;

    // コード
    private String code;

    // コード体系
    private String codeSystem;

    // 種別コード（薬剤｜手技｜材料）
    private String classCode;

    // 種別コードn体系
    private String classCodeSystem;

    // 数量
    private String number;

    // 単位
    private String unit;

    // 数量コード
    private String numberCode;

    // 数量コード体系
    private String numberCodeSystem;

    // メモ
    private String memo;

    // 薬剤区分 2011-02-10 追加
    private String ykzKbn;

    // G-specific
    private List<String> sstKijunCdSet;// SST基準コード
    private float suryo1;
    private float suryo2;
    private String startDate;// 開始日
    private String endDate;// 終了日

    @Override
    protected Object clone() throws CloneNotSupportedException {
        ClaimItem ret = new ClaimItem();
        ret.setClassCode(this.getClassCode());
        ret.setClassCodeSystem(this.getClassCodeSystem());
        ret.setCode(this.getCode());
        ret.setCodeSystem(this.getCodeSystem());
        ret.setMemo(this.getMemo());
        ret.setName(this.getName());
        ret.setNumber(this.getNumber());
        ret.setNumberCode(this.getNumberCode());
        ret.setNumberCodeSystem(this.getNumberCodeSystem());
        ret.setUnit(this.getUnit());
        ret.setYkzKbn(this.getYkzKbn());
        return ret;
    }

    // ---------------------------------
    // 残量廃棄
    // ---------------------------------
    public boolean getCanDispose() {

        boolean ret = false;

        if (this.getUnit() != null &&
                this.getUnit().equals(DISPOSE_UNIT) &&
                this.getNumber() != null) {
            try {
                Float qt = Float.parseFloat(this.getNumber());
                ret = (qt < 1.0) ? true : false;
            } catch (Exception e) {
            }
        }

        return ret;
    }

    // -----------------------------------------------------
    // Goody Handling
    // -----------------------------------------------------

    /**
     * カナのGetter
     *
     * @return カナ
     */
    public String getKana() {
        // CombinedStringParser line = new CombinedStringParser('|', numberCodeSystem);
        //
        // line.limit(4);
        // return line.get(2);
        return null;
    }

    /**
     * カナのSetter
     *
     * @param kana カナ
     */
    public final void setKana(String kana) {
        // if (kana != null) {
        // CombinedStringParser line = new CombinedStringParser('|', numberCodeSystem);
        //
        // line.limit(4);
        // line.set(2, kana);
        // numberCodeSystem = line.toCombinedString();
        // }
    }

    /**
     * 使用終了日のGetter
     *
     * @return 使用終了日
     */
    public String getDisUseDate() {
        // CombinedStringParser line = new CombinedStringParser('|', numberCodeSystem);
        // line.limit(4);
        // return line.get(3);
        return null;
    }

    /**
     * 使用終了日のSetter
     *
     * @param disUseDate 使用終了日
     */
    public final void setDisUseDate(String disUseDate) {
        // if (disUseDate != null) {
        // CombinedStringParser line = new CombinedStringParser('|', numberCodeSystem);
        // line.limit(4);
        // line.set(3, disUseDate);
        // numberCodeSystem = line.toCombinedString();
        // }
    }
    // -----------------------------------------------------
}
