/*
 * DiagnosisCategoryModel.java
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
package open.dolphin.spring.model.domain.medical.diagnosis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;

import javax.persistence.Embeddable;

/**
 * Diagnosis のカテゴリーモデル。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnosisCategoryModel extends InfoModel {

    // カテゴリー（コード値）
    private String diagnosisCategory;

    // カテゴリー表記
    private String diagnosisCategoryDesc;

    // カテゴリー体系
    private String diagnosisCategoryCodeSys;

    @Override
    public String toString() {
        return getDiagnosisCategoryDesc();
    }
}
