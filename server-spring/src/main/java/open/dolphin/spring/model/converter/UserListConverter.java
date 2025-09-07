package open.dolphin.spring.model.converter;

import java.util.ArrayList;
import java.util.List;
import open.dolphin.spring.model.entity.IInfoModel;
import open.dolphin.spring.model.entity.UserList;
import open.dolphin.spring.model.entity.UserModel;

/**
 *
 * @author kazushi Minagawa.
 */
public class UserListConverter implements IInfoModelConverter {
    
    private UserList model;
    
    public List<UserModelConverter> getList() {
        
        List<UserModel> list = model.getList();
        if (list==null || list.isEmpty()) {
            return null;
        }
        
        List<UserModelConverter> ret = new ArrayList<UserModelConverter>();
        for (UserModel m : list) {
            UserModelConverter con = new UserModelConverter();
            con.setModel(m);
            ret.add(con);
        }
        
        return ret;
    }
    
    @Override
    public void setModel(IInfoModel model) {
        this.model = (UserList)model;
    }
}
