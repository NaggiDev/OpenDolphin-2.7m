package open.dolphin.spring.model.entity;

import java.util.List;

/**
 *
 * @author kazushi Minagawa.
 */
public class NLaboModuleList extends InfoModel {

    private List<NLaboModule> list;

    public List<NLaboModule> getList() {
        return list;
    }

    public void setList(List<NLaboModule> list) {
        this.list = list;
    }
}
