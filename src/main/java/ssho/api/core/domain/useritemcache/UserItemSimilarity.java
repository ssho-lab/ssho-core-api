package ssho.api.core.domain.useritemcache;

import lombok.Data;
import ssho.api.core.domain.item.Item;

import java.util.List;

@Data
public class UserItemSimilarity {
    private List<Item> recentItemList;
    private List<UserItem> userItemList;
}
