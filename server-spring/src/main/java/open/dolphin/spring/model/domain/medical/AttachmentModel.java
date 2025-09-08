package open.dolphin.spring.model.domain.medical;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import open.dolphin.spring.model.core.KarteEntryBean;

import javax.persistence.*;
import javax.swing.ImageIcon;

/**
 * カルテのアタッチメント（文書や画像）クラス。
 *
 * @author Kazushi Minagawa. Digital Globe, Inc.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "d_attachment")
public class AttachmentModel extends KarteEntryBean {

    private String fileName;
    private String contentType;
    private long contentSize;
    private long lastModified;
    private String digest;

    private String title;
    private String uri;
    private String extension;
    private String memo;

    @Lob
    @Column(nullable = false)
    private byte[] bytes; // data

    @ManyToOne
    @JoinColumn(name = "doc_id", nullable = false)
    private DocumentModel document;

    @Transient
    private ImageIcon icon; // icon

    @Transient
    private int attachmentNumber;

    @Transient
    private String location;

    @Override
    public Object clone() throws CloneNotSupportedException {
        AttachmentModel ret = new AttachmentModel();
        ret.setConfirmed(this.getConfirmed());
        ret.setEnded(this.getEnded());
        ret.setFirstConfirmed(this.getConfirmed());
        ret.setLinkId(this.getLinkId());
        ret.setLinkRelation(this.getLinkRelation());
        ret.setRecorded(this.getRecorded());
        ret.setStarted(this.getStarted());
        ret.setStatus(this.getStatus());
        ret.setFileName(this.getFileName());
        ret.setContentType(this.getContentType());
        ret.setContentSize(this.getContentSize());
        ret.setLastModified(this.getLastModified());
        ret.setDigest(this.getDigest());
        ret.setTitle(this.getTitle());
        ret.setUri(this.getUri());
        ret.setExtension(this.getExtension());
        ret.setMemo(this.getMemo());
        ret.setAttachmentNumber(this.getAttachmentNumber());
        ret.setLocation(this.getLocation());

        if (this.getIcon() != null) {
            ret.setIcon(new ImageIcon(this.getIcon().getImage()));
        }

        if (this.getBytes() != null) {
            byte[] dest = new byte[this.getBytes().length];
            System.arraycopy(this.getBytes(), 0, dest, 0, this.getBytes().length);
            ret.setBytes(dest);
        }

        return ret;
    }
}
