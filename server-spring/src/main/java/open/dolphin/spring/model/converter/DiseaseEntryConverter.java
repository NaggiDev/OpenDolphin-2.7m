package open.dolphin.spring.model.converter;

import open.dolphin.spring.model.entity.DiseaseEntry;
import open.dolphin.spring.model.entity.IInfoModel;

/**
 * DiseaseEntry
 * 
 * @author  Minagawa, Kazushi
 */
public final class DiseaseEntryConverter implements IInfoModelConverter {
	
    private DiseaseEntry model;

    /** Creates a new instance of DeseaseEntry */
    public DiseaseEntryConverter() {
    }
        
    public String getCode() {
        return model.getCode();
    }
    
    public String getName() {
        return model.getName();
    }
    
    public String getKana() {
        return model.getKana();
    }
    
    public String getStartDate() {
        return model.getStartDate();
    }
    
    public String getEndDate() {
        return model.getEndDate();
    }
    
    public String getDisUseDate() {
        return model.getDisUseDate();
    }

    public String getIcdTen() {
        return model.getIcdTen();
    }
    
    @Override
    public void setModel(IInfoModel model) {
        this.model = (DiseaseEntry)model;
    }
}
