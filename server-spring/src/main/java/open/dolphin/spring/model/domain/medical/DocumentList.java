package open.dolphin.spring.model.domain.medical;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;

import java.util.List;

/**
 *
 * @author kazushi Minagawa.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentList extends InfoModel {

    private List<DocumentModel> list;
}
