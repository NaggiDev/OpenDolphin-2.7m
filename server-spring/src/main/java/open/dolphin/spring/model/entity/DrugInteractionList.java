package open.dolphin.spring.model.entity;

import java.util.List;

/**
 *
 * @author kazushi
 */
public class DrugInteractionList extends InfoModel {

    private List<DrugInteractionModel> list;

    public List<DrugInteractionModel> getList() {
        return list;
    }

    public void setList(List<DrugInteractionModel> list) {
        this.list = list;
    }
}
