package open.dolphin.spring.model.entity;

import java.util.List;

/**
 *
 * @author kazushi Minagawa.
 */
public class UserList extends InfoModel {

    private List<UserModel> list;

    public List<UserModel> getList() {
        return list;
    }

    public void setList(List<UserModel> list) {
        this.list = list;
    }
}
