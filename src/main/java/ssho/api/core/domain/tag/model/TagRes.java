package ssho.api.core.domain.tag.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TagRes {
    private ExpTag expTag;
    private RealTag realTag;
}
