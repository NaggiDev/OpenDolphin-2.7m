package open.dolphin.spring.model.converter;

import open.dolphin.spring.model.entity.IInfoModel;
import open.dolphin.spring.model.entity.SimpleAddressModel;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class SimpleAddressModelConverter implements IInfoModelConverter {

    private SimpleAddressModel model;

    public SimpleAddressModelConverter() {
    }

    public String getZipCode() {
        return model.getZipCode();
    }

    public String getAddress() {
        return model.getAddress();
    }

    @Override
    public void setModel(IInfoModel model) {
        this.model = (SimpleAddressModel)model;
    }
}
