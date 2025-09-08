package open.dolphin.spring.model.domain.communication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.KarteEntryBean;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Entity
@Table(name = "d_letter_module")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LetterModule extends KarteEntryBean {

    // Title
    private String title;

    // 紹介状種別
    private String letterType;

    // Handle Class
    private String handleClass;

    // 紹介元病院
    private String clientHospital;

    // 紹介元診療科
    private String clientDept;

    // 紹介元医師
    private String clientDoctor;

    // 紹介元郵便番号
    private String clientZipCode;

    // 紹介元住所
    private String clientAddress;

    // 紹介元電話番号
    private String clientTelephone;

    // 紹介元FAX番号
    private String clientFax;

    // 紹介先病院
    private String consultantHospital;

    // 紹介先診療科
    private String consultantDept;

    // 紹介先医師
    private String consultantDoctor;

    // 紹介先郵便番号
    private String consultantZipCode;

    // 紹介先住所
    private String consultantAddress;

    // 紹介先電話番号
    private String consultantTelephone;

    // 紹介先FAX番号
    private String consultantFax;

    // 患者ID
    private String patientId;

    // 患者氏名
    private String patientName;

    // 患者カナ
    private String patientKana;

    // 患者性別
    private String patientGender;

    // 患者生年月日
    private String patientBirthday;

    // 患者年齢
    private String patientAge;

    // 職業
    private String patientOccupation;

    // 患者郵便番号
    private String patientZipCode;

    // 患者住所
    private String patientAddress;

    // 患者電話番号
    private String patientTelephone;

    // 患者携帯番号
    private String patientMobilePhone;

    // 患者Fax番号
    private String patientFaxNumber;

    // @OneToMany(mappedBy="module", cascade={CascadeType.ALL})
    @Transient
    private List<LetterItem> letterItems;

    // @OneToMany(mappedBy="module", cascade={CascadeType.ALL})
    @Transient
    private List<LetterText> letterTexts;

    @Transient
    private List<LetterDate> letterDates;

    public void addLetterItem(LetterItem item) {
        if (letterItems == null) {
            letterItems = new ArrayList<LetterItem>();
            letterItems.add(item);
        } else {
            LetterItem exist = getLetterItem(item.getName());
            if (exist == null) {
                letterItems.add(item);
            } else {
                exist.setValue(item.getValue());
            }
        }
    }

    public void addLetterText(LetterText text) {
        if (letterTexts == null) {
            letterTexts = new ArrayList<LetterText>();
            letterTexts.add(text);
        } else {
            LetterText exist = getLetterText(text.getName());
            if (exist == null) {
                letterTexts.add(text);
            } else {
                exist.setTextValue(text.getTextValue());
            }
        }
    }

    public void addLetterDate(LetterDate date) {
        if (letterDates == null) {
            letterDates = new ArrayList<LetterDate>();
            letterDates.add(date);
        } else {
            LetterDate exist = getLetterDate(date.getName());
            if (exist == null) {
                letterDates.add(date);
            } else {
                exist.setValue(date.getValue());
            }
        }
    }

    public LetterItem getLetterItem(String name) {

        LetterItem ret = null;

        if (letterItems != null) {
            for (LetterItem item : letterItems) {
                if (item.getName().equals(name)) {
                    ret = item;
                    break;
                }
            }
        }

        return ret;
    }

    public LetterText getLetterText(String name) {

        LetterText ret = null;

        if (letterTexts != null) {
            for (LetterText txt : letterTexts) {
                if (txt.getName().equals(name)) {
                    ret = txt;
                    break;
                }
            }
        }

        return ret;
    }

    public LetterDate getLetterDate(String name) {

        LetterDate ret = null;

        if (letterDates != null) {
            for (LetterDate date : letterDates) {
                if (date.getName().equals(name)) {
                    ret = date;
                    break;
                }
            }
        }

        return ret;
    }

    public String getItemValue(String name) {

        String value = null;

        if (letterItems != null) {
            for (LetterItem item : letterItems) {
                if (item.getName().equals(name)) {
                    value = item.getValue();
                    break;
                }
            }
        }

        return value;
    }

    public String getTextValue(String name) {

        String value = null;

        if (letterTexts != null) {
            for (LetterText txt : letterTexts) {
                if (txt.getName().equals(name)) {
                    value = txt.getTextValue();
                    break;
                }
            }
        }

        return value;
    }

    public Date getDateValue(String name) {

        Date value = null;

        if (letterDates != null) {
            for (LetterDate date : letterDates) {
                if (date.getName().equals(name)) {
                    value = date.getValue();
                    break;
                }
            }
        }

        return value;
    }
}
