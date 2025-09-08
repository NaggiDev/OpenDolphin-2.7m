package open.dolphin.spring.model.domain.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.Embeddable;
import open.dolphin.spring.model.core.InfoModel;

/**
 * DepartmentModel
 *
 * @author Minagawa,Kazushi
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class DepartmentModel extends InfoModel {

    private String department;
    private String departmentDesc;
    private String departmentCodeSys;

    @Override
    public String toString() {
        return departmentDesc;
    }
}
