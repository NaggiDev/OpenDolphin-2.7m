package open.dolphin.spring.model.converter;

import java.util.ArrayList;
import java.util.List;
import open.dolphin.spring.model.entity.IInfoModel;
import open.dolphin.spring.model.entity.ModuleList;
import open.dolphin.spring.model.entity.ModuleListList;

/**
 *
 * @author kazushi Minagawa.
 */
public class ModuleListListConverter implements IInfoModelConverter {
    
    private ModuleListList model;
    
    public List<ModuleListConverter> getList() {
        
        List<ModuleList> list = model.getList();
        if (list==null || list.isEmpty()) {
            return null;
        }
        
        List<ModuleListConverter> ret = new ArrayList<ModuleListConverter>();
        for (ModuleList m : list) {
            ModuleListConverter con = new ModuleListConverter();
            con.setModel(m);
            ret.add(con);
        }
        
        return ret;
    }
    
    @Override
    public void setModel(IInfoModel model) {
        this.model = (ModuleListList)model;
    }
}
