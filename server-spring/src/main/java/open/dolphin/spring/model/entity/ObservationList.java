package open.dolphin.spring.model.entity;

import java.util.List;

/**
 *
 * @author kazushi Minagawa.
 */
public class ObservationList extends InfoModel {

    private List<ObservationModel> list;

    public List<ObservationModel> getList() {
        return list;
    }

    public void setList(List<ObservationModel> list) {
        this.list = list;
    }
}
