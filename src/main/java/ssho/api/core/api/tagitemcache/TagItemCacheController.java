package ssho.api.core.api.tagitemcache;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssho.api.core.service.tagitemcache.TagItemCacheServiceImpl;

@RequestMapping("/cache/tag-item")
@RestController
public class TagItemCacheController {

    private final TagItemCacheServiceImpl tagItemCacheService;

    public TagItemCacheController(TagItemCacheServiceImpl tagItemCacheService) {
        this.tagItemCacheService = tagItemCacheService;
    }

    @GetMapping("")
    public void updateTagItemCache() {
        tagItemCacheService.updateTagItemCache();
    }
}
