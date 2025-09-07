package open.dolphin.spring.model.converter;

import java.util.ArrayList;
import java.util.List;
import open.dolphin.spring.model.entity.IInfoModel;
import open.dolphin.spring.model.entity.NLaboModule;
import open.dolphin.spring.model.entity.NLaboModuleList;

/**
 *
 * @author kazushi Minagawa.
 */
public class NLaboModuleListConverter implements IInfoModelConverter {
    
    private NLaboModuleList model;
    
    public List<NLaboModuleConverter> getList() {
        
        List<NLaboModule> list = model.getList();
        if (list==null || list.isEmpty()) {
            return null;
        }
        
        List<NLaboModuleConverter> ret = new ArrayList<NLaboModuleConverter>();
        for (NLaboModule m : list) {
            NLaboModuleConverter con = new NLaboModuleConverter();
            con.setModel(m);
            ret.add(con);
        }
        
        return ret;
    }
    
    @Override
    public void setModel(IInfoModel model) {
        this.model = (NLaboModuleList)model;
    }
}
