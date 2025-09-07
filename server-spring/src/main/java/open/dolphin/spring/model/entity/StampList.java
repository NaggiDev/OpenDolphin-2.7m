package open.dolphin.spring.model.entity;

import java.util.List;

/**
 *
 * @author kazushi Minagawa.
 */
public class StampList extends InfoModel {

    private List<StampModel> list;

    public List<StampModel> getList() {
        return list;
    }

    public void setList(List<StampModel> list) {
        this.list = list;
    }
}
