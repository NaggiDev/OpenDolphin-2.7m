package open.dolphin.spring.model.converter;

import open.dolphin.spring.model.entity.IInfoModel;
import open.dolphin.spring.model.entity.RoleModel;

/**
 * RoleModel
 *
 * @author Minagawa,Kazushi
 */
public final class RoleModelConverter implements IInfoModelConverter {
    
    private RoleModel model;

    public RoleModelConverter() {
    }

    public long getId() {
        return model.getId();
    }

    public String getUserId() {
        return model.getUserId();
    }

    public String getRole() {
        return model.getRole();
    }

    @Override
    public void setModel(IInfoModel model) {
        this.model = (RoleModel)model;
    }
}
