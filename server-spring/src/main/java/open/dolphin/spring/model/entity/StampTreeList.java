package open.dolphin.spring.model.entity;

import java.util.List;

/**
 *
 * @author kazushi Minagawa.
 */
public class StampTreeList extends InfoModel {

    private List<StampTreeModel> list;

    public List<StampTreeModel> getList() {
        return list;
    }

    public void setList(List<StampTreeModel> list) {
        this.list = list;
    }
}
