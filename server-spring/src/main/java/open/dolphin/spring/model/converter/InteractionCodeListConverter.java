package open.dolphin.spring.model.converter;

import java.util.List;
import open.dolphin.spring.model.entity.IInfoModel;
import open.dolphin.spring.model.entity.InteractionCodeList;

/**
 *
 * @author kazushi
 */
public class InteractionCodeListConverter implements IInfoModelConverter {
    
    private InteractionCodeList model;
    
    public List<String> getCodes1() {
        return model.getCodes1();
    }
    
    public List<String> getCodes2() {
        return model.getCodes2();
    }
    
    @Override
    public void setModel(IInfoModel model) {
        this.model = (InteractionCodeList)model;
    }
}
