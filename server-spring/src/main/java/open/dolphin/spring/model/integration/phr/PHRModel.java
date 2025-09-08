package open.dolphin.spring.model.integration.phr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author kazushi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PHRModel implements java.io.Serializable {

    // OneToOne
    private String module_Id;
}
