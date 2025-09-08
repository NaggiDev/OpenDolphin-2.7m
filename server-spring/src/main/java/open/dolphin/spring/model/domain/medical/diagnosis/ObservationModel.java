package open.dolphin.spring.model.domain.medical.diagnosis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.KarteEntryBean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Onservation
 *
 * @author Minagawa, Kazushi
 *
 */
@Entity
@Table(name = "d_observation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObservationModel extends KarteEntryBean {

    // Observation 名
    @Column(nullable = false)
    private String observation;

    // 現象型
    @Column(nullable = false)
    private String phenomenon;

    // 値
    @Column(name = "c_value")
    private String value;

    // 単位
    private String unit;

    // カテゴリー値
    private String categoryValue;

    // 値の説明
    private String valueDesc;

    // 値のコード体系
    private String valueSys;

    // メモ
    private String memo;
}
