package ssho.api.core.domain.tagset;

import lombok.Data;
import ssho.api.core.domain.tag.Tag;

@Data
public class TagSet {
    private Tag tagA;
    private Tag tagB;
    private Double rate;
}
