package open.dolphin.spring.model.domain.laboratory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabTestValueObject implements Serializable {

    private String sampleDate;

    private String value;

    private String out;

    private String comment1;

    private String comment2;

    public void setComment1(String comment1) {
        if (comment1 != null) {
            comment1.trim();
            this.comment1 = comment1;
        }
    }

    public void setComment2(String comment2) {
        if (comment2 != null) {
            comment2.trim();
            this.comment2 = comment2;
        }
    }

    public String concatComment() {

        StringBuilder sb = new StringBuilder();

        if (getComment1() != null && (!getComment1().equals(""))) {
            sb.append(getComment1());
            sb.append(" ");
        }

        if (getComment2() != null && (!getComment2().equals(""))) {
            sb.append(getComment2());
        }

        return sb.length() > 0 ? sb.toString() : null;
    }
}
