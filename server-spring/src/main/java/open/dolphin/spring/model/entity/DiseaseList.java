package open.dolphin.spring.model.entity;

import java.util.List;

/**
 *
 * @author kazushi Minagawa.
 */
public class DiseaseList extends InfoModel {

    private List<DiseaseEntry> list;

    public List<DiseaseEntry> getList() {
        return list;
    }

    public void setList(List<DiseaseEntry> list) {
        this.list = list;
    }
}
