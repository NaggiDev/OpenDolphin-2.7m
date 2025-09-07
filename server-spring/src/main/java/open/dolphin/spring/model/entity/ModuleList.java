package open.dolphin.spring.model.entity;

import java.util.List;

/**
 *
 * @author kazushi Minagawa.
 */
public class ModuleList extends InfoModel {

    private List<ModuleModel> list;

    public List<ModuleModel> getList() {
        return list;
    }

    public void setList(List<ModuleModel> list) {
        this.list = list;
    }
}
