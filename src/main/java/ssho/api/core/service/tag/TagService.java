package ssho.api.core.service.tag;

import ssho.api.core.domain.tag.model.ExpTag;
import ssho.api.core.domain.tag.model.RealTag;

import java.io.IOException;
import java.util.List;

public interface TagService {
    void saveRealTag(List<RealTag> tagList, String index) throws IOException;

    void saveExpTag(List<ExpTag> tagList, String index) throws IOException;

    List<RealTag> findAllRealTags(final String index);

    RealTag findRealTagByRealTagName(final String tagName);

    ExpTag findExpTagByRealTagName(final String tagId);

    void deleteAllRealTags(final String index);

    void deleteAllExpTags(final String index);

    List<ExpTag> getExpTagListOrderedByTagCountByUserId(final String userId);

    List<ExpTag> getExpTagListOrderedBySearchScoreByKeyword(final String keyword);
}