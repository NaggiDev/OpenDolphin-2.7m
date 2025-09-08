package open.dolphin.spring.model.domain.medical;

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
public class ModuleListList extends InfoModel {

    private List<ModuleList> list;

    public void addList(ModuleList l) {
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(l);
    }
}
