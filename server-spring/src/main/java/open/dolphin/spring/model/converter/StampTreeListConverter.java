package open.dolphin.spring.model.converter;

import java.util.ArrayList;
import java.util.List;
import open.dolphin.spring.model.entity.IInfoModel;
import open.dolphin.spring.model.entity.StampTreeList;
import open.dolphin.spring.model.entity.StampTreeModel;

/**
 *
 * @author kazushi Minagawa.
 */
public class StampTreeListConverter implements IInfoModelConverter {
    
    private StampTreeList model;
    
    public List<StampTreeModelConverter> getList() {
        
        List<StampTreeModel> list = model.getList();
        if (list==null || list.isEmpty()) {
            return null;
        }
        
        List<StampTreeModelConverter> ret = new ArrayList<StampTreeModelConverter>();
        for (StampTreeModel m : list) {
            StampTreeModelConverter con = new StampTreeModelConverter();
            con.setModel(m);
            ret.add(con);
        }
        
        return ret;
    }
    
    @Override
    public void setModel(IInfoModel model) {
        this.model = (StampTreeList)model;
    }
}
