package ssho.api.core.domain.useritemcache.req;

import lombok.Data;
import ssho.api.core.domain.item.Item;
import ssho.api.core.domain.mall.model.Mall;
import ssho.api.core.domain.userswipe.model.UserSwipeScore;

import java.util.List;

@Data
public class UserItemCacheReq {
    List<Mall> mallList;
    List<Item> itemList;
    List<UserSwipeScore> userSwipeScoreList;
}
