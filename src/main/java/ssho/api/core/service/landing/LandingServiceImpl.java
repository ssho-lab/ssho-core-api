package ssho.api.core.service.landing;

import org.springframework.stereotype.Service;
import ssho.api.core.domain.landing.res.LandingRes;
import ssho.api.core.domain.tag.Tag;
import ssho.api.core.service.tag.TagServiceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LandingServiceImpl implements LandingService {

    private final TagServiceImpl tagService;

    public LandingServiceImpl(TagServiceImpl tagService) {
        this.tagService = tagService;
    }

    @Override
    public LandingRes getLandingByUserId(int userId) {
        List<Tag> tagList = tagService.getTagList().stream().filter(Tag::isActive).collect(Collectors.toList());

        LandingRes landingRes = new LandingRes();

        List<Tag> recoTagList = new ArrayList<>(tagList);
        List<Tag> trendingTagList = new ArrayList<>(recoTagList);

        Collections.shuffle(recoTagList);

        landingRes.setRecoTagList(recoTagList.subList(0, 4));
        landingRes.setTrendingTagList(trendingTagList.subList(0, 4));

        return landingRes;
    }
}
