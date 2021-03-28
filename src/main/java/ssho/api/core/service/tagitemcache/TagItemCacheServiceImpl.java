package ssho.api.core.service.tagitemcache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;
import ssho.api.core.domain.item.Item;
import ssho.api.core.domain.stylegroup.StyleGroup;
import ssho.api.core.domain.swipelog.model.SwipeLog;
import ssho.api.core.domain.tagitemcache.TagItemCache;
import ssho.api.core.domain.tagset.TagSet;
import ssho.api.core.domain.useritemcache.UserItem;
import ssho.api.core.domain.useritemcache.UserItemCache;
import ssho.api.core.service.item.ItemServiceImpl;
import ssho.api.core.service.stylegroup.StyleGroupServiceImpl;
import ssho.api.core.service.tag.TagServiceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagItemCacheServiceImpl implements TagItemCacheService {

    private final StyleGroupServiceImpl styleGroupService;
    private final TagServiceImpl tagService;
    private final ItemServiceImpl itemService;
    private final RestHighLevelClient restHighLevelClient;
    private final ObjectMapper objectMapper;

    private String TAG_ITEM_CACHE_INDEX = "cache-tagitem";

    public TagItemCacheServiceImpl(StyleGroupServiceImpl styleGroupService, TagServiceImpl tagService, ItemServiceImpl itemService, RestHighLevelClient restHighLevelClient, ObjectMapper objectMapper) {
        this.styleGroupService = styleGroupService;
        this.tagService = tagService;
        this.itemService = itemService;
        this.restHighLevelClient = restHighLevelClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public void updateTagItemCache() {

        List<StyleGroup> styleGroupList = styleGroupService.styleGroupList();

        styleGroupList.forEach(styleGroup -> {
            try {
                List<Item> styleGroupItemList = styleGroupItemList(styleGroup.getTagId());

                TagItemCache cache = new TagItemCache();
                cache.setTagId(styleGroup.getTagId());
                cache.setItemList(styleGroupItemList);

                save(cache, TAG_ITEM_CACHE_INDEX);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public TagItemCache getTagItemCache(String tagId) throws IOException {
        return get(tagId, TAG_ITEM_CACHE_INDEX);
    }

    private List<Item> styleGroupItemList(String tagId) throws IOException {
        List<TagSet> tagSetList = tagService.getTagSetListById(tagId);
        double rateSum = tagSetList.stream().map(TagSet::getRate).mapToDouble(r -> r).sum();

        List<Item> itemList = tagSetList.stream().map(tagSet -> {
            double rate = tagSet.getRate() / rateSum;
            String tagBId = tagSet.getTagB().getId();

            List<Item> tagItemList = itemService.getItemsByTagId(tagBId);
            //Collections.shuffle(tagItemList);

            return tagItemList.subList(0, (int) (20 * rate));

        }).collect(Collectors.toList())
                .stream()
                .flatMap(Collection::stream)
                .map(itemService::simplify)
                .collect(Collectors.toList());

        Collections.shuffle(itemList);

        return itemList;
    }

    private void save(TagItemCache cache, String index) throws IOException {

        IndexRequest indexRequest =
                new IndexRequest(index)
                        .id(cache.getTagId())
                        .source(objectMapper.writeValueAsString(cache), XContentType.JSON);

        restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
    }

    private TagItemCache get(String tagId, String index) throws IOException {
        GetRequest getRequest = new GetRequest(index, tagId);
        return objectMapper.readValue(restHighLevelClient.get(getRequest, RequestOptions.DEFAULT).getSourceAsString(), TagItemCache.class);
    }
}
