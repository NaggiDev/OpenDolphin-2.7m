package open.dolphin.spring.model.entity;

import java.util.List;

/**
 *
 * @author kazushi Minagawa.
 */
public class PublishedTreeList extends InfoModel {

    private List<PublishedTreeModel> list;

    public List<PublishedTreeModel> getList() {
        return list;
    }

    public void setList(List<PublishedTreeModel> list) {
        this.list = list;
    }
}
