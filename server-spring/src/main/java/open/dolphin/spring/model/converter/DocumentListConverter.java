package open.dolphin.spring.model.converter;

import java.util.ArrayList;
import java.util.List;
import open.dolphin.spring.model.entity.DocumentList;
import open.dolphin.spring.model.entity.DocumentModel;
import open.dolphin.spring.model.entity.IInfoModel;

/**
 *
 * @author kazushi Minagawa.
 */
public class DocumentListConverter implements IInfoModelConverter {
    
    private DocumentList model;
    
    public List<DocumentModelConverter> getList() {
        
        List<DocumentModel> list = model.getList();
        if (list==null || list.isEmpty()) {
            return null;
        }
        
        List<DocumentModelConverter> ret = new ArrayList<DocumentModelConverter>();
        for (DocumentModel m : list) {
            DocumentModelConverter con = new DocumentModelConverter();
            con.setModel(m);
            ret.add(con);
        }
        
        return ret;
    }
    
    @Override
    public void setModel(IInfoModel model) {
        this.model = (DocumentList)model;
    }
}
