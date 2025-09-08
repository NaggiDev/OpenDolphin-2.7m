package open.dolphin.spring.model.domain.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kazushi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StampTreeHolder extends InfoModel {

    // 個人用のtree
    private StampTreeModel personalTree;

    // import しているtreeのリスト
    private List<PublishedTreeModel> subscribedList;

    public void addSubscribedTree(PublishedTreeModel tree) {
        if (this.subscribedList == null) {
            this.subscribedList = new ArrayList<PublishedTreeModel>();
        }
        this.subscribedList.add(tree);
    }
}
