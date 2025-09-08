package open.dolphin.spring.model.domain.laboratory;

import java.util.Comparator;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class SampleDateComparator implements Comparator<NLaboModule> {

    @Override
    public int compare(NLaboModule m1, NLaboModule m2) {

        int result = m1.getSampleDate().compareTo(m2.getSampleDate());

        if (result == 0) {
            String key1 = m1.getModuleKey();
            String key2 = m2.getModuleKey();
            if (key1 != null && key2 != null) {
                return key1.compareTo(key2);
            }
        }

        return m1.getSampleDate().compareTo(m2.getSampleDate());
    }
}
