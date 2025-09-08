package open.dolphin.spring.model.domain.laboratory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabTestRowObject implements Serializable, Comparable {

    private String labCode;

    private String groupCode;

    private String parentCode;

    private String itemCode;

    private String normalValue;

    private String itemName;

    private String unit;

    private List<LabTestValueObject> values;

    public String nameWithUnit() {

        StringBuilder sb = new StringBuilder();
        sb.append(getItemName());
        if (getUnit() != null) {
            sb.append("(");
            sb.append(getUnit());
            sb.append(")");
            // s.oh^ 2014/06/16 基準値の表示
            if (getNormalValue() != null && getNormalValue().length() > 0) {
                sb.append(" [");
                sb.append(getNormalValue());
                sb.append("]");
            }
            // s.oh$
        }
        return sb.toString();
    }

    public void addLabTestValueObjectAt(int index, LabTestValueObject value) {

        if (values == null) {
            values = new ArrayList<LabTestValueObject>(5);
            for (int i = 0; i < 5; i++) {
                values.add(null);
            }
        }
        values.add(index, value);
    }

    public LabTestValueObject getLabTestValueObjectAt(int index) {

        if (getValues() == null || index < 0 || index > getValues().size() - 1) {
            return null;
        }

        return getValues().get(index);
    }

    @Override
    public int compareTo(Object o) {

        if (o != null && getClass() == o.getClass()) {

            LabTestRowObject other = (LabTestRowObject) o;

            StringBuilder sb = new StringBuilder();
            sb.append(getLabCode());
            sb.append(getGroupCode());
            sb.append(getParentCode());
            sb.append(getItemCode());
            String str1 = sb.toString();

            sb = new StringBuilder();
            sb.append(other.getLabCode());
            sb.append(other.getGroupCode());
            sb.append(other.getParentCode());
            sb.append(other.getItemCode());
            String str2 = sb.toString();

            return str1.compareTo(str2);
        }

        return -1;
    }

    public String toClipboard() {

        if (values == null || values.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(itemName);

        for (LabTestValueObject val : values) {
            if (val != null && val.getValue() != null) {
                sb.append(",").append(val.getValue());
                if (val.getOut() != null) {
                    sb.append(",").append(val.getOut());
                }
                if (unit != null) {
                    sb.append(",").append(unit);
                }
                sb.append(",").append(val.getSampleDate());
            }
        }

        return sb.toString();
    }

    public String toClipboardLatest() {

        if (values == null || values.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(itemName);

        int last = values.size() - 1;
        LabTestValueObject test = null;
        for (int i = last; i > -1; i--) {
            test = values.get(i);
            if (test != null && test.getValue() != null) {
                break;
            }
        }

        if (test == null) {
            return null;
        }

        sb.append(",").append(test.getValue());
        if (test.getOut() != null) {
            sb.append(",").append(test.getOut());
        }
        if (unit != null) {
            sb.append(",").append(unit);
        }
        sb.append(",").append(test.getSampleDate());

        return sb.toString();
    }

    // s.oh^ 2013/06/13 カラムの並び順
    public String toClipboardLatestReverse() {

        if (values == null || values.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(itemName);

        int last = values.size() - 1;
        LabTestValueObject test = null;
        for (int i = 0; i < last; i++) {
            test = values.get(i);
            if (test != null && test.getValue() != null) {
                break;
            }
        }

        if (test == null) {
            return null;
        }

        sb.append(",").append(test.getValue());
        if (test.getOut() != null) {
            sb.append(",").append(test.getOut());
        }
        if (unit != null) {
            sb.append(",").append(unit);
        }
        sb.append(",").append(test.getSampleDate());

        return sb.toString();
    }
    // s.oh$
}
