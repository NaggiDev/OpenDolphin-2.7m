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
 * 温度板エントリークラス。
 * firstConfirmed=confirmed とし 時刻部分は 00:00:00 で運用する。
 * 上記に dayIndex を組み合わせ対象測定項目の日時を指定する。
 * これとは別に recorded を正確な記録日時として担保する。
 *
 * @author kazushi Minagawa
 */
@Entity
@Table(name = "d_ondoban")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OndobanModel extends KarteEntryBean {

    // 測定項目の名前 BT, BPH, BPL etc.
    @Column(nullable = false)
    private String seriesName;

    // 測定項目につける Index
    @Column(nullable = false)
    private int seriesIndex;

    // １日に複数回測定するのでそのIndex
    @Column(nullable = false)
    private int dayIndex;

    // 測定値
    @Column(name = "c_value", nullable = false)
    private float value;

    // 単位
    private String unit;

    // メモ
    private String memo;
}
