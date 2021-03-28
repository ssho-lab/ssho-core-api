package ssho.api.core.domain.groupitemcache;

import lombok.Data;

import java.util.List;

@Data
public class GroupItemCache {
    private String tagId;
    private List<GroupItem> groupItemList;
}
