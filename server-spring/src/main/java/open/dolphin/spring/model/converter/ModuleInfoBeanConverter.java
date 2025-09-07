package open.dolphin.spring.model.converter;

import open.dolphin.spring.model.entity.IInfoModel;
import open.dolphin.spring.model.entity.ModuleInfoBean;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class ModuleInfoBeanConverter implements IInfoModelConverter {

    private ModuleInfoBean model;

    public ModuleInfoBeanConverter() {
    }

    public String getStampName() {
        return model.getStampName();
    }

    public String getStampRole() {
        return model.getStampRole();
    }

    public int getStampNumber() {
        return model.getStampNumber();
    }

    public String getEntity() {
        return model.getEntity();
    }

    @Override
    public void setModel(IInfoModel model) {
        this.model = (ModuleInfoBean)model;
    }
}
