package open.dolphin.spring.model.entity;

import java.util.List;

/**
 *
 * @author kazushi Minagawa.
 */
public class LetterModuleList extends InfoModel {

    private List<LetterModule> list;

    public List<LetterModule> getList() {
        return list;
    }

    public void setList(List<LetterModule> list) {
        this.list = list;
    }
}
