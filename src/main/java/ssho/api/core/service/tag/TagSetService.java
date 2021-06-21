package ssho.api.core.service.tag;

import ssho.api.core.domain.tagset.TagSet;

import java.io.IOException;
import java.util.List;

public interface TagSetService {
    void saveTagSet(TagSet tagSet) throws IOException;

    TagSet getTagSet(TagSet tagSet) throws IOException;

    List<TagSet> getTagSetListById(String tagId) throws IOException;

    List<TagSet> getTagSetList();
}
