package open.dolphin.spring.model.converter;

import open.dolphin.spring.model.entity.DiagnosisCategoryModel;
import open.dolphin.spring.model.entity.IInfoModel;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class DiagnosisCategoryModelConverter implements IInfoModelConverter {

    private DiagnosisCategoryModel model;

    public DiagnosisCategoryModelConverter() {
    }

    public String getDiagnosisCategory() {
        return model.getDiagnosisCategory();
    }

    public String getDiagnosisCategoryDesc() {
        return model.getDiagnosisCategoryDesc();
    }

    public String getDiagnosisCategoryCodeSys() {
        return model.getDiagnosisCategoryCodeSys();
    }

    @Override
    public void setModel(IInfoModel model) {
        this.model = (DiagnosisCategoryModel)model;
    }
}
