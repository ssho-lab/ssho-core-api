package ssho.api.core.service.tagitemcache;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TagItemCacheServiceImplTest {
    @Autowired
    TagItemCacheServiceImpl tagItemCacheService;

    @Test
    void updateTagItemCache() {
        tagItemCacheService.updateTagItemCache();
    }
}
