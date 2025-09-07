package open.dolphin.spring.model.entity;

import java.util.List;

/**
 *
 * @author kazushi Minagawa.
 */
public class TensuList extends InfoModel {

    private List<TensuMaster> list;

    public List<TensuMaster> getList() {
        return list;
    }

    public void setList(List<TensuMaster> list) {
        this.list = list;
    }
}
