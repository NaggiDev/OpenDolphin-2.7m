/*
 * ClaimBundle.java
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

/**
 * ClaimBundle 要素クラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimBundle extends InfoModel {

    // 診療行為名
    protected String className;

    // 診療行為コード
    protected String classCode;

    // コード体系
    protected String classCodeSystem;

    // 用法
    protected String admin;

    // 用法コード
    protected String adminCode;

    // 用法コード体系
    protected String adminCodeSystem;

    // 用法メモ
    protected String adminMemo;

    // バンドル数
    protected String bundleNumber;

    // バンドル構成品目
    protected ClaimItem[] claimItem;

    // メモ
    protected String memo;

    // 保険種別
    protected String insurance;

    public void addClaimItem(ClaimItem val) {
        if (claimItem == null) {
            claimItem = new ClaimItem[1];
            claimItem[0] = val;
            return;
        }
        int len = claimItem.length;
        ClaimItem[] dest = new ClaimItem[len + 1];
        System.arraycopy(claimItem, 0, dest, 0, len);
        claimItem = dest;
        claimItem[len] = val;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        ClaimBundle ret = new ClaimBundle();
        ret.setAdmin(this.getAdmin());
        ret.setAdminCode(this.getAdminCode());
        ret.setAdminCodeSystem(this.getAdminCodeSystem());
        ret.setAdminMemo(this.getAdminMemo());
        ret.setBundleNumber(this.getBundleNumber());
        ret.setClassCode(this.getClassCode());
        ret.setClassCodeSystem(this.getClassCodeSystem());
        ret.setClassName(this.getClassName());
        ret.setInsurance(this.getInsurance());
        ret.setMemo(this.getMemo());
        ClaimItem[] items = this.getClaimItem();
        if (items != null) {
            for (ClaimItem item : items) {
                ret.addClaimItem((ClaimItem) item.clone());
            }
        }
        return ret;
    }
}
