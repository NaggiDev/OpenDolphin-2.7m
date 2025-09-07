package open.dolphin.spring.model.entity;

import java.util.List;

/**
 *
 * @author kazushi Minagawa.
 */
public class DocInfoList extends InfoModel {

    private List<DocInfoModel> list;

    public List<DocInfoModel> getList() {
        return list;
    }

    public void setList(List<DocInfoModel> list) {
        this.list = list;
    }
}
