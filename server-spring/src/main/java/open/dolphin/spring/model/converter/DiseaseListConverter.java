package open.dolphin.spring.model.converter;

import java.util.ArrayList;
import java.util.List;
import open.dolphin.spring.model.entity.DiseaseEntry;
import open.dolphin.spring.model.entity.DiseaseList;
import open.dolphin.spring.model.entity.IInfoModel;

/**
 *
 * @author kazushi Minagawa.
 */
public class DiseaseListConverter implements IInfoModelConverter {
    
    private DiseaseList model;
    
    public List<DiseaseEntryConverter> getList() {
        
        List<DiseaseEntry> list = model.getList();
        if (list==null || list.isEmpty()) {
            return null;
        }
        
        List<DiseaseEntryConverter> ret = new ArrayList<DiseaseEntryConverter>();
        for (DiseaseEntry m : list) {
            DiseaseEntryConverter con = new DiseaseEntryConverter();
            con.setModel(m);
            ret.add(con);
        }
        
        return ret;
    }
    
    @Override
    public void setModel(IInfoModel model) {
        this.model = (DiseaseList)model;
    }
}
