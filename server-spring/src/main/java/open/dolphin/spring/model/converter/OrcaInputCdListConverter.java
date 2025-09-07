package open.dolphin.spring.model.converter;

import java.util.ArrayList;
import java.util.List;
import open.dolphin.spring.model.entity.IInfoModel;
import open.dolphin.spring.model.entity.OrcaInputCd;
import open.dolphin.spring.model.entity.OrcaInputCdList;

/**
 *
 * @author kazushi Minagawa.
 */
public class OrcaInputCdListConverter implements IInfoModelConverter {
    
    private OrcaInputCdList model;
    
    public List<OrcaInputCdConverter> getList() {
        
        List<OrcaInputCd> list = model.getList();
        if (list==null || list.isEmpty()) {
            return null;
        }
        
        List<OrcaInputCdConverter> ret = new ArrayList<OrcaInputCdConverter>();
        for (OrcaInputCd m : list) {
            OrcaInputCdConverter con = new OrcaInputCdConverter();
            con.setModel(m);
            ret.add(con);
        }
        
        return ret;
    }
    
    @Override
    public void setModel(IInfoModel model) {
        this.model = (OrcaInputCdList)model;
    }
}
