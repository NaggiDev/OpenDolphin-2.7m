package open.dolphin.spring.model.converter;

import java.util.ArrayList;
import java.util.List;
import open.dolphin.spring.model.entity.IInfoModel;
import open.dolphin.spring.model.entity.RegisteredDiagnosisList;
import open.dolphin.spring.model.entity.RegisteredDiagnosisModel;

/**
 *
 * @author kazushi Minagawa.
 */
public class RegisteredDiagnosisListConverter implements IInfoModelConverter {
    
    private RegisteredDiagnosisList model;
    
    public List<RegisteredDiagnosisModelConverter> getList() {
        
        List<RegisteredDiagnosisModel> list = model.getList();
        if (list==null || list.isEmpty()) {
            return null;
        }
        
        List<RegisteredDiagnosisModelConverter> ret = new ArrayList<RegisteredDiagnosisModelConverter>();
        for (RegisteredDiagnosisModel m : list) {
            RegisteredDiagnosisModelConverter con = new RegisteredDiagnosisModelConverter();
            con.setModel(m);
            ret.add(con);
        }
        
        return ret;
    }
    
    @Override
    public void setModel(IInfoModel model) {
        this.model = (RegisteredDiagnosisList)model;
    }
}
