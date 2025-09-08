package open.dolphin.spring.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;

import javax.persistence.Embeddable;

/**
 * SimpleAddressModel
 *
 * @author kazm
 *
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimpleAddressModel extends InfoModel {

    private String zipCode;

    private String address;
}
