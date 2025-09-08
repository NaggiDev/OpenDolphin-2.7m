package open.dolphin.spring.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;

/**
 * ProgressCourse
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressCourse extends InfoModel {

    String freeText;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        ProgressCourse ret = new ProgressCourse();
        ret.setFreeText(this.getFreeText());
        return ret;
    }
}
