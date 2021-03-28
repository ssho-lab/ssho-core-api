package ssho.api.core.api.groupitemcache;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssho.api.core.service.groupitemcache.GroupItemCacheServiceImpl;

@RequestMapping("/cache/group-item")
@RestController
public class GroupItemCacheController {

    private GroupItemCacheServiceImpl groupItemCacheService;

    public GroupItemCacheController(GroupItemCacheServiceImpl groupItemCacheService) {
        this.groupItemCacheService = groupItemCacheService;
    }

    /**
     * 그룹 추천 상품 캐시 업데이트
     */
    @GetMapping("/update")
    public void updateGroupItemCache() {
        groupItemCacheService.updateGroupItemCache();
    }
}
