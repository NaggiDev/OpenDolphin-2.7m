/*
 * MasterEntry.java
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
package open.dolphin.spring.model.domain.medical.diagnosis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * MasterEntry
 *
 * @author Minagawa,Kazushi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MasterEntry extends InfoModel {

    private static final long serialVersionUID = -6170839610525955077L;

    protected static String refDate;
    static {
        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
        refDate = f.format(gc.getTime()).toString();
    }

    protected String code;

    protected String name;

    protected String kana;

    protected String startDate;

    protected String endDate;

    protected String disUseDate;

    public int compareTo(Object obj) {

        MasterEntry other = (MasterEntry) obj;

        int myUse = getUseState(startDate, endDate);
        int otherUse = getUseState(other.getStartDate(), other.getEndDate());

        int ret = 0;

        switch (myUse) {

            case 0:
                if (otherUse == 0) {
                    ret = code.compareTo(other.getCode());
                } else if (otherUse == 1) {
                    ret = 1;
                } else if (otherUse == 2) {
                    ret = -1;
                }
                break;

            case 1:
                if (otherUse == 1) {
                    ret = code.compareTo(other.getCode());
                } else {
                    ret = -1;
                }
                break;

            case 2:
                if (otherUse == 0) {
                    ret = 1;
                } else if (otherUse == 1) {
                    ret = 1;
                } else if (otherUse == 2) {
                    ret = code.compareTo(other.getCode());
                }
                break;
        }

        return ret;
    }

    public boolean isInUse() {

        return getUseState(startDate, endDate) == 1 ? true : false;

    }

    protected int getUseState(String startDate, String endDate) {

        // 有効期限前
        if (startDate != null && refDate.compareTo(startDate) < 0) {
            return 0;

            // 有効期限後
        } else if (endDate != null && refDate.compareTo(endDate) > 0) {
            return 2;
        }

        // 有効期限内
        return 1;
    }

    @Override
    public String toString() {
        return name;
    }
}
