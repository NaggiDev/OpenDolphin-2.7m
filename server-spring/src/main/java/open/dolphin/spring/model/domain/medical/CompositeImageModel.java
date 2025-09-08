package open.dolphin.spring.model.domain.medical;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.KarteEntryBean;

import javax.persistence.*;
import javax.swing.ImageIcon;

/**
 *
 * @author kazm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "d_composite_image")
public class CompositeImageModel extends KarteEntryBean {

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private String medicalRole;

    @Transient
    private String medicalRoleTableId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String href;

    @Transient
    private int imageNumber;

    @Transient
    private ImageIcon icon;

    @Lob
    @Column(nullable = false)
    private byte[] jpegByte;

    @Column(nullable = false)
    private long compositor;
}
