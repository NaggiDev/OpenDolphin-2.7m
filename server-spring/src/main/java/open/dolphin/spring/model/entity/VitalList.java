/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.spring.model.entity;

import java.util.List;

/**
 * バイタル対応
 * 
 * @author Life Sciences Computing Corporation.
 */
public class VitalList extends InfoModel {

    private List<VitalModel> list;

    public List<VitalModel> getList() {
        return list;
    }

    public void setList(List<VitalModel> list) {
        this.list = list;
    }
}
