package ssho.api.core.service.groupitemcache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;
import ssho.api.core.domain.groupitemcache.GroupItem;
import ssho.api.core.domain.groupitemcache.GroupItemCache;
import ssho.api.core.domain.stylegroup.StyleGroup;
import ssho.api.core.domain.tag.Tag;
import ssho.api.core.domain.useritemcache.UserItemCache;
import ssho.api.core.repository.stylegroup.StyleGroupRepository;
import ssho.api.core.service.tag.TagServiceImpl;
import ssho.api.core.service.useritemcache.UserItemCacheServiceImpl;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupItemCacheServiceImpl implements GroupItemCacheService {

    private final StyleGroupRepository styleGroupRepository;
    private final TagServiceImpl tagService;
    private final UserItemCacheServiceImpl userItemCacheService;
    private final RestHighLevelClient restHighLevelClient;
    private final ObjectMapper objectMapper;

    private final String GROUP_ITEM_CACHE_INDEX = "cache-groupitem";

    public GroupItemCacheServiceImpl(StyleGroupRepository styleGroupRepository, TagServiceImpl tagService, UserItemCacheServiceImpl userItemCacheService,
                                     RestHighLevelClient restHighLevelClient, ObjectMapper objectMapper) {
        this.styleGroupRepository = styleGroupRepository;
        this.tagService = tagService;
        this.userItemCacheService = userItemCacheService;
        this.restHighLevelClient = restHighLevelClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public void updateGroupItemCache() {
        List<GroupItemCache> groupItemCacheList = styleGroupRepository
                .findAll()
                .stream()
                .map(StyleGroup::getTagId)
                .distinct()
                .map(id -> {
                    try {
                        GroupItemCache groupItemCache = new GroupItemCache();

                        Tag tag = tagService.getTagById(id);
                        List<UserItemCache> userItemCacheList = styleGroupRepository
                                .findAllByTagId(id)
                                .stream()
                                .map(StyleGroup::getUserId)
                                .map(userId -> {
                                    try {
                                        return userItemCacheService.getUserItemCacheByUserId(userId);
                                    } catch (IOException e) {
                                        return null;
                                    }
                                }).collect(Collectors.toList());
                        Collections.shuffle(userItemCacheList);

                        // TODO 그룹아이템 평균 내는 방법

                        List<GroupItem> groupItemList = userItemCacheList.get(0).getUserItemList().stream().map(userItem -> {
                            GroupItem groupItem = new GroupItem();
                            groupItem.setItem(userItem.getItem());
                            groupItem.setRate(userItem.getRate());
                            return groupItem;
                        }).collect(Collectors.toList());

                        groupItemCache.setTagId(tag.getId());
                        groupItemCache.setGroupItemList(groupItemList);

                        return groupItemCache;

                    } catch (IOException e) {
                        return null;
                    }
                }).collect(Collectors.toList());

        groupItemCacheList.forEach(groupItemCache -> {
            try {
                save(groupItemCache, GROUP_ITEM_CACHE_INDEX);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void save(GroupItemCache cache, String index) throws IOException {

        IndexRequest indexRequest =
                new IndexRequest(index)
                        .id(cache.getTagId())
                        .source(objectMapper.writeValueAsString(cache), XContentType.JSON);

        restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
    }
}
