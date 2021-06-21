package ssho.api.core.service.shoppingbag;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ssho.api.core.domain.item.Item;
import ssho.api.core.domain.shoppingbag.ShoppingBagCardSet;
import ssho.api.core.domain.swipelog.model.SwipeLog;
import ssho.api.core.domain.usercardset.UserCardSet;
import ssho.api.core.service.item.ItemServiceImpl;
import ssho.api.core.service.usercardset.UserCardSetServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ShoppingBagServiceImpl implements ShoppingBagService {

    private WebClient webClient;
    private final ItemServiceImpl itemService;
    private final UserCardSetServiceImpl userCardSetService;

    @Value("${log.api.host}")
    private String LOG_API_HOST;

    public ShoppingBagServiceImpl(ItemServiceImpl itemService, UserCardSetServiceImpl userCardSetService) {
        this.itemService = itemService;
        this.userCardSetService = userCardSetService;
        this.webClient = WebClient.builder().baseUrl(LOG_API_HOST).build();
    }

    @Override
    public List<ShoppingBagCardSet> getShoppingBagCardSetListByUserId(final String userId) {

        List<ShoppingBagCardSet> shoppingBagCardSetList = new ArrayList<>();

        // Key: 회원 고유 번호
        // Value: 스와이프 로그 리스트
        Map<Integer, List<SwipeLog>> groupedSwipeLogList = webClient
                .get()
                .uri("/log/swipe/user/like/grouped?userId={userId}", userId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<Integer, List<SwipeLog>>>() {
                })
                .block();

        if (groupedSwipeLogList != null && groupedSwipeLogList.size() > 0) {

            for (Map.Entry<Integer, List<SwipeLog>> entry : groupedSwipeLogList.entrySet()) {

                ShoppingBagCardSet shoppingBagCardSet = new ShoppingBagCardSet();
                List<SwipeLog> swipeLogList = entry.getValue();

                if (swipeLogList.size() == 0) {
                    continue;
                }

                SwipeLog firstLog = swipeLogList.get(0);

                UserCardSet userCardSet = userCardSetService.getById(firstLog.getUserCardSetId());

                if (userCardSet.equals(new UserCardSet()) || userCardSet.getCreateTime() == null) {
                    continue;
                }

                List<Item> itemList = swipeLogList
                        .stream()
                        .map(swipeLog -> itemService.getItemCumById(swipeLog.getItemId()))
                        .collect(Collectors.toList());

                shoppingBagCardSet.setItemList(itemList);
                shoppingBagCardSet.setUserCardSet(userCardSetService.getById(firstLog.getUserCardSetId()));

                shoppingBagCardSetList.add(shoppingBagCardSet);
            }
        }

        return shoppingBagCardSetList;
    }
}
