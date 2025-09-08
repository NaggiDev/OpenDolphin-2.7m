/*
 * ExtRef.java
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
package open.dolphin.spring.model.domain.medical;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

/**
 * 外部参照要素クラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class ExtRefModel extends InfoModel {

    // MIME ContentType
    @Column(nullable = false)
    private String contentType;

    // Medical Role
    @Column(nullable = false)
    private String medicalRole;

    // Medical Role コード体系
    @Transient
    private String medicalRoleTableId;

    // タイトル
    @Column(nullable = false)
    private String title;

    // href
    @Column(nullable = false)
    private String href;

    // S3
    private String bucket;

    // S3
    private String sop;

    // S3
    private String url;

    // S3
    private String facilityId;

    // -----------------------------------
    // Unitea
    // -----------------------------------
    private String imageTime;
    private String bodyPart;
    private String shutterNum;
    private String seqNum;
    private String extension;
    // -----------------------------------

    @Override
    public Object clone() throws CloneNotSupportedException {
        ExtRefModel ret = new ExtRefModel();
        ret.setBucket(this.getBucket());
        ret.setContentType(this.getContentType());
        ret.setHref(this.getHref());
        ret.setMedicalRole(this.getMedicalRole());
        ret.setMedicalRoleTableId(this.getMedicalRoleTableId());
        ret.setSop(this.getSop());
        ret.setTitle(this.getTitle());
        ret.setUrl(this.getUrl());
        return ret;
    }
}
