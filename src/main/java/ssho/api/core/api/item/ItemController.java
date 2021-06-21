package ssho.api.core.api.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssho.api.core.domain.item.Item;
import ssho.api.core.service.item.ItemServiceImpl;

import java.io.IOException;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/item")
public class ItemController {

    private final ItemServiceImpl itemService;

    public ItemController(ItemServiceImpl itemService) {
        this.itemService = itemService;
    }

    /**
     * 상품 전체 조회
     *
     * @return
     */
    @GetMapping("")
    public List<Item> getItemList() {
        return itemService.getItems();
    }

    /**
     * 몰별 상품 전체 조회
     *
     * @param mallNo
     * @return
     * @throws IOException
     */
    @GetMapping("/{mallNo}")
    public List<Item> getMallItemList(@PathVariable String mallNo) throws IOException {
        return itemService.getItemsByMallNo(mallNo);
    }
}
