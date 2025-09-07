/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.spring.model.converter;

import java.util.ArrayList;
import java.util.List;
import open.dolphin.spring.model.entity.IInfoModel;
import open.dolphin.spring.model.entity.VitalList;
import open.dolphin.spring.model.entity.VitalModel;

/**
 * バイタル対応
 * 
 * @author Life Sciences Computing Corporation.
 */
public class VitalListConverter implements IInfoModelConverter {
    
    private VitalList model;
    
    public List<VitalModelConverter> getList() {
        
        List<VitalModel> list = model.getList();
        if (list==null || list.isEmpty()) {
            return null;
        }
        
        List<VitalModelConverter> ret = new ArrayList<VitalModelConverter>();
        for (VitalModel m : list) {
            VitalModelConverter con = new VitalModelConverter();
            con.setModel(m);
            ret.add(con);
        }
        
        return ret;
    }
    
    @Override
    public void setModel(IInfoModel model) {
        this.model = (VitalList)model;
    }
}
