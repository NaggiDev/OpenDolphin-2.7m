package open.dolphin.spring.model.domain.laboratory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * LaboImportReply
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaboImportReply implements Serializable {

    private static final long serialVersionUID = 4916041527411972913L;

    private long karteId;
    private String patientName;
    private String patinetGender;
    private String patientBirthday;
}
