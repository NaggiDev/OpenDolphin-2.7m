package open.dolphin.spring.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;

import open.dolphin.spring.model.core.InfoModel;

/**
 *
 * @author Kazushi Minagawa.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class CodeNamePack extends InfoModel {

    private String code;
    private String name;
}
