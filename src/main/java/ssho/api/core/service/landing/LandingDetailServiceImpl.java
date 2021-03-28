package ssho.api.core.service.landing;

import org.springframework.stereotype.Service;
import ssho.api.core.domain.landing.res.LandingDetailRes;
import ssho.api.core.domain.tag.Tag;
import ssho.api.core.domain.tagset.TagSet;
import ssho.api.core.service.tag.TagServiceImpl;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LandingDetailServiceImpl implements LandingDetailService {

    private final TagServiceImpl tagService;

    public LandingDetailServiceImpl(TagServiceImpl tagService) {
        this.tagService = tagService;
    }

    @Override
    public LandingDetailRes getLandingDetail(String tagId) throws IOException {
        List<TagSet> tagSetList = tagService.getTagSetListById(tagId);
        Tag tag = tagSetList.get(0).getTagA();
        List<Tag> relatedTagList = tagSetList
                                        .stream()
                                        .sorted(Comparator.comparing(TagSet::getRate).reversed())
                                        .map(TagSet::getTagB)
                                        .collect(Collectors.toList())
                                        .subList(0,4);
        LandingDetailRes detailRes = new LandingDetailRes();
        detailRes.setTag(tag);
        detailRes.setRelatedTagList(relatedTagList);

        return detailRes;
    }
}
