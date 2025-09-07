package open.dolphin.spring.model.entity;

import java.util.List;

/**
 *
 * @author kazushi Minagawa.
 */
public class DocumentList extends InfoModel {

    private List<DocumentModel> list;

    public List<DocumentModel> getList() {
        return list;
    }

    public void setList(List<DocumentModel> list) {
        this.list = list;
    }
}
