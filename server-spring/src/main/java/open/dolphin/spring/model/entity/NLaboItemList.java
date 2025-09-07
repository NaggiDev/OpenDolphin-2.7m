package open.dolphin.spring.model.entity;

import java.util.List;

/**
 *
 * @author kazushi Minagawa.
 */
public class NLaboItemList extends InfoModel {

    private List<NLaboItem> list;

    public List<NLaboItem> getList() {
        return list;
    }

    public void setList(List<NLaboItem> list) {
        this.list = list;
    }
}
