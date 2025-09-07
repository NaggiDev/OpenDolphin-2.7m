package open.dolphin.spring.model.converter;

import java.util.ArrayList;
import java.util.List;
import open.dolphin.spring.model.entity.IInfoModel;
import open.dolphin.spring.model.entity.ModuleList;
import open.dolphin.spring.model.entity.ModuleModel;

/**
 *
 * @author kazushi Minagawa.
 */
public class ModuleListConverter implements IInfoModelConverter {
    
    private ModuleList model;
    
    public List<ModuleModelConverter> getList() {
        
        List<ModuleModelConverter> ret = new ArrayList<ModuleModelConverter>();
        
        List<ModuleModel> list = model.getList();
        if (list==null || list.isEmpty()) {
            return ret;
        }
        
        for (ModuleModel m : list) {
            ModuleModelConverter con = new ModuleModelConverter();
            con.setModel(m);
            ret.add(con);
        }
        
        return ret;
    }
    
    @Override
    public void setModel(IInfoModel model) {
        this.model = (ModuleList)model;
    }
}
