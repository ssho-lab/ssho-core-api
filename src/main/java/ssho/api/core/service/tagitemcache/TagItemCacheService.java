package ssho.api.core.service.tagitemcache;

import ssho.api.core.domain.tagitemcache.TagItemCache;

import java.io.IOException;

public interface TagItemCacheService {
    void updateTagItemCache();
    TagItemCache getTagItemCache(String tagId) throws IOException;
}
