package open.dolphin.spring.model.domain.communication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.KarteEntryBean;

import javax.persistence.*;

/**
 * 紹介状モデル。
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "docType", discriminatorType = DiscriminatorType.STRING)
// @DiscriminatorValue("Letter")
@Table(name = "d_letter")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LetterModel extends KarteEntryBean {

    // @Lob // OpenDolphin-1.4 ではこのアノテーションなし
    @Column(nullable = false)
    private byte[] beanBytes;
}
