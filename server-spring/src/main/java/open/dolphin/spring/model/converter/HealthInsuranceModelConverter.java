package open.dolphin.spring.model.converter;

import open.dolphin.spring.model.entity.HealthInsuranceModel;
import open.dolphin.spring.model.entity.IInfoModel;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class HealthInsuranceModelConverter implements IInfoModelConverter {

    private HealthInsuranceModel model;

    public HealthInsuranceModelConverter() {
    }

    public long getId() {
        return model.getId();
    }

    public byte[] getBeanBytes() {
        return model.getBeanBytes();
    }

    @Override
    public void setModel(IInfoModel model) {
        this.model = (HealthInsuranceModel)model;
    }
}
