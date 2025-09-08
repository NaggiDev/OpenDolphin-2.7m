package open.dolphin.spring.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.KarteEntryBean;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import org.hibernate.annotations.Type;

/**
 * 看護記録モデル
 *
 * @author kazushi Minagawa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "d_nurse_progress_course")
public class NurseProgressCourseModel extends KarteEntryBean {

    @Lob
    private String progressText;

    // 看護記録の文字数
    private int textLength;
}
