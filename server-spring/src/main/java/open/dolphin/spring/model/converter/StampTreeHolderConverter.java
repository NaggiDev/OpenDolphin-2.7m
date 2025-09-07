package open.dolphin.spring.model.converter;

import java.util.ArrayList;
import java.util.List;
import open.dolphin.spring.model.entity.IInfoModel;
import open.dolphin.spring.model.entity.PublishedTreeModel;
import open.dolphin.spring.model.entity.StampTreeHolder;

/**
 *
 * @author kazushi
 */
public class StampTreeHolderConverter implements IInfoModelConverter {
    
    private StampTreeHolder model;
    
    public StampTreeModelConverter getPersonalTree() {
        if (model.getPersonalTree()!=null) {
            StampTreeModelConverter conv = new StampTreeModelConverter();
            conv.setModel(model.getPersonalTree());
            return conv;
        }
        return null;
    }

    public List<PublishedTreeModelConverter> getSubscribedList() {
        List<PublishedTreeModelConverter> ret = new ArrayList<PublishedTreeModelConverter>();
        List<PublishedTreeModel> list = model.getSubscribedList();
        if (list==null || list.isEmpty()) {
            return ret;
        }
        for (PublishedTreeModel m : list) {
            PublishedTreeModelConverter conv = new PublishedTreeModelConverter();
            conv.setModel(m);
            ret.add(conv);
        }
        
        return ret;
    }
    
    @Override
    public void setModel(IInfoModel model) {
        this.model = (StampTreeHolder)model;
    }
}