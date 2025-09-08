package open.dolphin.spring.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;

import open.dolphin.spring.model.core.InfoModel;

/**
 * AddressModel
 *
 * @author Minagawa,kazushi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class AddressModel extends InfoModel {

    private String addressType;
    private String addressTypeDesc;
    private String addressTypeCodeSys;
    private String countryCode;
    private String zipCode;
    private String address;
}
