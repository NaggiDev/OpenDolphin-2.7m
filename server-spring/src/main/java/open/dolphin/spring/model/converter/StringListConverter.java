package open.dolphin.spring.model.converter;

import java.util.List;
import open.dolphin.spring.model.entity.IInfoModel;
import open.dolphin.spring.model.entity.StringList;

/**
 *
 * @author kazushi
 */
public class StringListConverter implements IInfoModelConverter {
    
    private StringList model;
    
    public List<String> getList() {
        return model.getList();
    }
    
    @Override
    public void setModel(IInfoModel model) {
        this.model = (StringList)model;
    }
}
