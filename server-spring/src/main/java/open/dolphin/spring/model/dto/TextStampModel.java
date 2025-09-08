package open.dolphin.spring.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import open.dolphin.spring.model.core.InfoModel;

/**
 * Text stamp model for storing text-based stamps.
 *
 * @author Kazushi Minagawa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TextStampModel extends InfoModel {

    private String text;

    @Override
    public String toString() {
        return getText();
    }
}
