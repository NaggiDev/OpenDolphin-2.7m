package open.dolphin.spring.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.KarteEntryBean;

import javax.persistence.*;

/**
 * 初診時情報クラス。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "docType", discriminatorType = DiscriminatorType.STRING)
@Table(name = "d_first_encounter")
public class FirstEncounterModel extends KarteEntryBean {

    // @Lob ASP サーバへ配備する時、コメントアウトしてはいけない
    @Column(nullable = false)
    private byte[] beanBytes;
}
