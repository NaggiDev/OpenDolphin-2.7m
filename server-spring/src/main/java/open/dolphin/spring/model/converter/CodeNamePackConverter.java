package open.dolphin.spring.model.converter;

import open.dolphin.spring.model.entity.CodeNamePack;
import open.dolphin.spring.model.entity.IInfoModel;

/**
 *
 * @author kazushi Minagawa.
 */
public class CodeNamePackConverter implements IInfoModelConverter {
    
    private CodeNamePack model;
    
    public String getCode() {
        return model.getCode();
    }

    public String getName() {
        return model.getName();
    }
    
    @Override
    public void setModel(IInfoModel model) {
        this.model = (CodeNamePack)model;
    }
}
