package ssho.api.core.domain.tagitemcache;

import lombok.Data;
import ssho.api.core.domain.item.Item;

import java.util.List;

@Data
public class TagItemCache {
    private String tagId;
    private List<Item> itemList;
}
