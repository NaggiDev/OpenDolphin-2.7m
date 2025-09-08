/*
 * OrcaInputCd.java
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
import open.dolphin.spring.model.domain.medical.ModuleInfoBean;
import open.dolphin.spring.model.domain.medication.BundleMed;

import java.util.ArrayList;

/**
 * ORCA の tbl_inputcd エンティティクラス。
 *
 * @author Minagawa, Kazushi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrcaInputCd extends InfoModel {

    private String hospId;

    private String cdsyu;

    private String inputCd;

    private String sryKbn;

    private String sryCd;

    private int dspSeq;

    private String dspName;

    private String termId;

    private String opId;

    private String creYmd;

    private String upYmd;

    private String upHms;

    private ArrayList<OrcaInputSet> inputSet;

    public void addInputSet(OrcaInputSet set) {
        if (inputSet == null) {
            inputSet = new ArrayList<OrcaInputSet>();
        }
        inputSet.add(set);
    }

    public ModuleInfoBean getStampInfo() {
        ModuleInfoBean ret = new ModuleInfoBean();
        ret.setStampName(getDspName());
        ret.setStampRole(ROLE_ORCA_SET);
        ret.setEntity(ENTITY_MED_ORDER); // ?
        ret.setStampId(getInputCd());
        return ret;
    }

    public BundleMed getBundleMed() {

        BundleMed ret = new BundleMed();

        for (OrcaInputSet set : inputSet) {

            ret.addClaimItem(set.getClaimItem());
        }

        return ret;
    }
}
