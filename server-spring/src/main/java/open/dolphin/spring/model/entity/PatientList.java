package open.dolphin.spring.model.entity;

import java.util.List;

/**
 *
 * @author kazushi Minagawa.
 */
public class PatientList extends InfoModel {

    private List<PatientModel> list;

    public List<PatientModel> getList() {
        return list;
    }

    public void setList(List<PatientModel> list) {
        this.list = list;
    }
}
