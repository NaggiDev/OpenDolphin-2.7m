/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package open.dolphin.spring.model.entity;

import java.util.List;

/**
 *
 * @author kazushi
 */
public class StringList extends InfoModel {

    private List<String> list;

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> model) {
        this.list = model;
    }
}
