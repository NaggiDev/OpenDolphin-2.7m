/*
 * PublicInsurance.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
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
package open.dolphin.spring.model.domain.insurance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;

/**
 * <!ELEMENT mmlHi:publicInsuranceItem (mmlHi:providerName?,
 * mmlHi:provider,
 * mmlHi:recipient,
 * mmlHi:startDate,
 * mmlHi:expiredDate,
 * mmlHi:paymentRatio?)>
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicInsuranceItemModel extends InfoModel {

    // 優先順位
    private String priority;

    // 公費負担名称
    private String providerName;

    // 負担者番号
    private String provider;

    // 受給者番号
    private String recipient;

    // 開始日
    private String startDate;

    // 有効期限
    private String expiredDate;

    // 負担率または負担金
    private String paymentRatio;

    // 負担率のタイプ
    private String ratioType;
}
