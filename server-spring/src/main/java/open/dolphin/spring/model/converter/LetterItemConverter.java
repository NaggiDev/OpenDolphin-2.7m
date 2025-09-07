package open.dolphin.spring.model.converter;

import open.dolphin.spring.model.entity.IInfoModel;
import open.dolphin.spring.model.entity.LetterItem;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class LetterItemConverter implements IInfoModelConverter {

    private LetterItem model;

    public LetterItemConverter() {
    }

    public String getName() {
        return model.getName();
    }

    public String getValue() {
        return model.getValue();
    }

    @Override
    public void setModel(IInfoModel model) {
        this.model = (LetterItem)model;
    }
}
