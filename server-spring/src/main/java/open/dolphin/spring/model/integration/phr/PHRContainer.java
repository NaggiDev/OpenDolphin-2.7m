package open.dolphin.spring.model.integration.phr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *
 * @author kazushi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PHRContainer implements java.io.Serializable {

    private List<PHRCatch> docList;

    private List<PHRLabModule> labList;
}
