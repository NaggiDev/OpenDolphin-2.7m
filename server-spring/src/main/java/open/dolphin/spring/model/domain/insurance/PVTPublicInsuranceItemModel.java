package open.dolphin.spring.model.domain.insurance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.InfoModel;

/**
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PVTPublicInsuranceItemModel extends InfoModel {

    // 複数公費の優先順位
    private String priority;

    // 公費負担名称
    private String providerName;

    // 負担者番号
    private String provider;

    // 受給者番号
    private String recipient;

    // 開始日
    private String startDate;

    // 開始日
    private String expiredDate;

    // 負担率または負担金
    private String paymentRatio;

    // 負担率または負担金
    private String paymentRatioType;

    @Override
    public String toString() {

        StringBuilder buf = new StringBuilder();

        if (providerName != null) {
            buf.append(providerName);
        } else if (provider != null) {
            buf.append(provider);
        }

        return buf.toString();
    }

    public String toClaim() {

        StringBuilder sb = new StringBuilder();

        sb.append("<mmlHi:publicInsuranceItem ");

        // 公費の優先順位 attribute
        if (getPriority() != null) {
            sb.append("mmlHi:priority=");
            sb.append(addQuote(getPriority()));
            sb.append(">");
        }

        // 公費負担名称 ?
        if (getProviderName() != null) {
            sb.append("<mmlHi:providerName>");
            sb.append(getProviderName());
            sb.append("</mmlHi:providerName>");
        }

        // 負担者番号
        if (getProvider() != null) {
            sb.append("<mmlHi:provider>");
            sb.append(getProvider());
            sb.append("</mmlHi:provider>");
        }

        // 受給者番号
        if (getRecipient() != null) {
            sb.append("<mmlHi:recipient>");
            sb.append(getRecipient());
            sb.append("</mmlHi:recipient>");
        }

        // 開始日
        if (getStartDate() != null) {
            sb.append("<mmlHi:startDate>");
            sb.append(getStartDate());
            sb.append("</mmlHi:startDate>");
        }

        // 有効期限
        if (getExpiredDate() != null) {
            sb.append("<mmlHi:expiredDate>");
            sb.append(getExpiredDate());
            sb.append("</mmlHi:expiredDate>");
        }

        // 負担率 ?
        if (getPaymentRatio() != null && getPaymentRatioType() != null) {
            sb.append("<mmlHi:paymentRatio mmlHi:RatioType=");
            sb.append(addQuote(getPaymentRatioType()));
            sb.append(">");
            sb.append(getPaymentRatio());
            sb.append("</mmlHi:paymentRatio>");
        }

        sb.append("</mmlHi:publicInsuranceItem>");

        return sb.toString();
    }

    private String addQuote(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"");
        sb.append(str);
        sb.append("\"");
        return sb.toString();
    }
}
