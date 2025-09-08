package open.dolphin.spring.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;

import open.dolphin.spring.model.core.InfoModel;

/**
 * TelephoneModel
 *
 * @author Minagawa,Kazushi
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class TelephoneModel extends InfoModel {

    private String telephoneType;
    private String telephoneTypeDesc;
    private String telephoneTypeCodeSys;
    private String country;
    private String area;
    private String city;
    private String number;
    private String extension;
    private String memo;
}
