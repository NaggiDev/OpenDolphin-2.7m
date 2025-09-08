package open.dolphin.spring.model.domain.medical.diagnosis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DiseaseEntry
 *
 * @author Minagawa, Kazushi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class DiseaseEntry extends MasterEntry {

    private String icdTen;

    @Override
    public boolean isInUse() {
        if (disUseDate != null) {
            return refDate.compareTo(disUseDate) <= 0 ? true : false;
        }
        return false;
    }
}
