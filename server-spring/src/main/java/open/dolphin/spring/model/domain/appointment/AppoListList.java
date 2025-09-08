package open.dolphin.spring.model.domain.appointment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kazushi Minagawa.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppoListList extends InfoModel {

    private List<AppoList> list;

    public void addList(AppoList l) {
        if (list == null) {
            list = new ArrayList<AppoList>();
        }
        list.add(l);
    }
}
