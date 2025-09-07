package open.dolphin.spring.model.converter;

import open.dolphin.spring.model.entity.IInfoModel;
import open.dolphin.spring.model.entity.PatientLiteModel;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class PatientLiteModelConverter implements IInfoModelConverter {

    private PatientLiteModel model;

    public PatientLiteModelConverter() {
    }

    public String getPatientId() {
        return model.getPatientId();
    }

    public String getFullName() {
        return model.getFullName();
    }

    public String getKanaName() {
        return model.getKanaName();
    }

    public String getGender() {
        return model.getGender();
    }

    public String getGenderDesc() {
        return model.getGenderDesc();
    }

    public String getBirthday() {
        return model.getBirthday();
    }

    @Override
    public void setModel(IInfoModel model) {
        this.model = (PatientLiteModel)model;
    }
}
