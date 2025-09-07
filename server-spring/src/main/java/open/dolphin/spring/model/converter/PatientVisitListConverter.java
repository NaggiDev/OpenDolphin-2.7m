package open.dolphin.spring.model.converter;

import java.util.ArrayList;
import java.util.List;
import open.dolphin.spring.model.entity.IInfoModel;
import open.dolphin.spring.model.entity.PatientVisitList;
import open.dolphin.spring.model.entity.PatientVisitModel;

/**
 *
 * @author kazushi Minagawa.
 */
public class PatientVisitListConverter implements IInfoModelConverter {
    
    private PatientVisitList model;
    
    public List<PatientVisitModelConverter> getList() {
        
        List<PatientVisitModel> list = model.getList();
        if (list==null || list.isEmpty()) {
            return null;
        }
        
        List<PatientVisitModelConverter> ret = new ArrayList<PatientVisitModelConverter>();
        for (PatientVisitModel m : list) {
            PatientVisitModelConverter con = new PatientVisitModelConverter();
            con.setModel(m);
            ret.add(con);
        }
        
        return ret;
    }
    
    @Override
    public void setModel(IInfoModel model) {
        this.model = (PatientVisitList)model;
    }
}
