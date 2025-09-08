/*
 * OrcaInputSet.java
 * Copyright (C) 2007 Digital Globe, Inc. All rights reserved.
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
package open.dolphin.spring.model.integration.orca;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;
import open.dolphin.spring.model.domain.medication.ClaimItem;

/**
 * ORCA の tbl_inputset エンティティクラス。
 *
 * @author Minagawa, Kazushi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrcaInputSet extends InfoModel {

    private String hospId;

    private String setCd;

    private String yukostYmd;

    private String yukoedYmd;

    private int setSeq;

    private String inputCd;

    private float suryo1;

    private float suryo2;

    private int kaisu;

    private String comment;

    private String atai1;

    private String atai2;

    private String atai3;

    private String atai4;

    private String termId;

    private String opId;

    private String creYmd;

    private String upYmd;

    private String upHms;

    public ClaimItem getClaimItem() {
        ClaimItem ret = new ClaimItem();
        ret.setCode(getInputCd());
        ret.setNumber(String.valueOf(getSuryo1()));
        return ret;
    }
}
