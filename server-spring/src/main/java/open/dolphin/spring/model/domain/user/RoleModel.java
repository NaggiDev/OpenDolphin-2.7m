package open.dolphin.spring.model.domain.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import open.dolphin.spring.model.core.InfoModel;

/**
 * RoleModel
 *
 * @author Minagawa,Kazushi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "d_roles")
public class RoleModel extends InfoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "c_role", nullable = false)
    private String role;

    @ManyToOne
    @JoinColumn(name = "c_user", nullable = false)
    private UserModel user;
}
