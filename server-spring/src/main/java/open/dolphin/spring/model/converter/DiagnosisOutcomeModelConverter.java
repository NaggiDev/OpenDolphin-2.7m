package open.dolphin.spring.model.converter;

import open.dolphin.spring.model.entity.DiagnosisOutcomeModel;
import open.dolphin.spring.model.entity.IInfoModel;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class DiagnosisOutcomeModelConverter implements IInfoModelConverter {

    private DiagnosisOutcomeModel model;

    public DiagnosisOutcomeModelConverter() {
    }

    public String getOutcome() {
        return model.getOutcome();
    }

    public String getOutcomeDesc() {
        return model.getOutcomeDesc();
    }

    public String getOutcomeCodeSys() {
        return model.getOutcomeCodeSys();
    }

    @Override
    public void setModel(IInfoModel model) {
        this.model = (DiagnosisOutcomeModel)model;
    }
}
