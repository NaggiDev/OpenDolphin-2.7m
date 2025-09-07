package open.dolphin.spring.model.entity;

import java.util.List;

/**
 *
 * @author kazushi Minagawa.
 */
public class PatientLiteList extends InfoModel {

    private List<PatientLiteModel> list;

    public List<PatientLiteModel> getList() {
        return list;
    }

    public void setList(List<PatientLiteModel> list) {
        this.list = list;
    }
}
