package ssho.api.core.domain.groupitemcache;

import lombok.Data;
import ssho.api.core.domain.item.Item;

@Data
public class GroupItem implements Comparable<GroupItem> {
    private Item item;
    private Double rate;

    @Override
    public int compareTo(GroupItem groupItem) {
        if(this.getRate() < groupItem.getRate()){
            return 1;
        }
        else if(this.getRate().equals(groupItem.getRate())){
            return 0;
        }
        return -1;
    }
}
