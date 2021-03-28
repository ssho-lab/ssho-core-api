package ssho.api.core.domain.stylegroup;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "style_group")
@Data
public class StyleGroup {

    @Id
    private int userId;
    private String tagId;
    private Double rate;
}
