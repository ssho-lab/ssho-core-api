package ssho.api.core.service.stylegroup;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ssho.api.core.domain.stylegroup.StyleGroup;
import ssho.api.core.domain.swipelog.model.SwipeLog;
import ssho.api.core.domain.tag.Tag;
import ssho.api.core.domain.tutoriallog.TutorialLog;
import ssho.api.core.domain.user.model.User;
import ssho.api.core.domain.useritemcache.req.UserItemCacheReq;
import ssho.api.core.repository.stylegroup.StyleGroupRepository;
import ssho.api.core.service.carddeck.CardDeckServiceImpl;
import ssho.api.core.service.item.ItemServiceImpl;
import ssho.api.core.service.tag.TagServiceImpl;
import ssho.api.core.service.user.UserAdminService;
import ssho.api.core.service.useritemcache.UserItemCacheServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StyleGroupServiceImpl implements StyleGroupService {

    private StyleGroupRepository styleGroupRepository;
    private UserAdminService userAdminService;
    private CardDeckServiceImpl cardDeckService;
    private ItemServiceImpl itemService;
    private UserItemCacheServiceImpl userItemCacheService;
    private TagServiceImpl tagService;

    public StyleGroupServiceImpl(StyleGroupRepository styleGroupRepository, UserAdminService userAdminService, @Lazy CardDeckServiceImpl cardDeckService,
                                 ItemServiceImpl itemService, UserItemCacheServiceImpl userItemCacheService, TagServiceImpl tagService) {
        this.styleGroupRepository = styleGroupRepository;
        this.userAdminService = userAdminService;
        this.cardDeckService = cardDeckService;
        this.itemService = itemService;
        this.userItemCacheService = userItemCacheService;
        this.tagService = tagService;
    }

    @Override
    public void saveInitial(int userId, List<TutorialLog> tutorialLogList) {
        List<String> itemIdList = tutorialLogList.stream().filter(tutorialLog -> tutorialLog.getScore() == 1).map(TutorialLog::getItemId).collect(Collectors.toList());
        List<Tag> tagList = itemIdList
                .stream()
                .map(itemId -> itemService.getItemById(itemId).getTagList().get(0))
                .collect(Collectors.toList());

        Collections.shuffle(tagList);

        StyleGroup styleGroup = new StyleGroup();
        styleGroup.setUserId(userId);
        styleGroup.setTagId(tagList.get(0).getId());

        styleGroupRepository.save(styleGroup);
    }

    @Override
    public void updateStyleGroup() {
        List<User> userList = userAdminService.getUsers();
        UserItemCacheReq userItemCacheReq = userItemCacheService.getUserItemCacheReq();

        List<StyleGroup> styleGroupList = userList.stream()
                .map(user -> {
                    int userId = user.getId();

                    List<SwipeLog> swipeLogList = userItemCacheService.swipeLogList(String.valueOf(userId));

                    List<Tag> tagList = tagService.getTagList();

                    if(tagList.size() > 0 ){
                    }

                    //TODO: 생성된 회원-상품 캐시를 사용
                    //TODO: 태그 벡터 생성 로직 만들기

                    StyleGroup styleGroup = new StyleGroup();
                    styleGroup.setUserId(userId);

                    return styleGroup;
                }).collect(Collectors.toList());

        styleGroupRepository.saveAll(styleGroupList);
    }
    public StyleGroup styleGroupByUserId(int userId) {
        return styleGroupRepository.findById(userId).orElse(null);
    }

    public List<StyleGroup> styleGroupList() {
        return styleGroupRepository.findAll();
    }

    private boolean styleGroupExists(int userId) {
        return styleGroupRepository.existsById(userId);
    }
}
