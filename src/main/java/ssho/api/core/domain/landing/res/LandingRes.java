package ssho.api.core.domain.landing.res;

import lombok.Data;
import ssho.api.core.domain.tag.Tag;

import java.util.List;

@Data
public class LandingRes {
    private List<Tag> recoTagList;
    private List<Tag> trendingTagList;
}
