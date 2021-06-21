package ssho.api.core.service.carddeck;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import ssho.api.core.domain.carddeck.CardDeck;
import ssho.api.core.domain.item.Item;
import ssho.api.core.domain.mall.model.CategoryCode;
import ssho.api.core.domain.swipelog.model.SwipeLog;
import ssho.api.core.domain.tagset.TagSet;
import ssho.api.core.domain.usercardset.UserCardSet;
import ssho.api.core.service.item.ItemServiceImpl;
import ssho.api.core.service.tag.TagSetServiceImpl;
import ssho.api.core.service.usercardset.UserCardSetServiceImpl;
import ssho.api.core.service.useritemcache.UserItemCacheServiceImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardDeckServiceImpl implements CardDeckService {

    private final ItemServiceImpl itemService;
    private final UserCardSetServiceImpl userCardSetService;
    private final TagSetServiceImpl tagSetService;

    private WebClient webClient;

    @Value("${log.api.host}")
    private String LOG_API_HOST;

    private final ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder().codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1)).build();

    public CardDeckServiceImpl(ItemServiceImpl itemService, UserCardSetServiceImpl userCardSetService, TagSetServiceImpl tagSetService) {
        this.itemService = itemService;
        this.userCardSetService = userCardSetService;
        this.tagSetService = tagSetService;
    }

    @Override
    public CardDeck cardDeckByUserId(int userId) {

        try {

            // 1. 최신 회원 카드셋 조회
            UserCardSet userCardSet = userCardSetService.getRecentByUserId(userId);

            // 2. 회원 스와이프 로그 조회
            List<String> swipedItemIdList = swipedItemIdList(userId);

            // 3. 최신 회원 카드셋의 태그 기준으로 태그셋 조회
            List<TagSet> tagSetList = tagSetService.getTagSetListById(userCardSet.getTagId());

            // 4. 태그셋 가중치 합 계산
            double rateSum = tagSetList
                    .stream()
                    .map(TagSet::getRate)
                    .mapToDouble(r -> r)
                    .sum();

            // 5. 최종 카드덱 상품 리스트 조회
            List<Item> userItemList = tagSetList.stream().map(tagSet -> {

                double rate = tagSet.getRate() / rateSum;

                String tagBId = tagSet.getTagB().getId();

                // 연관 태그 상품 리스트(이미 조회한 상품은 제외)
                List<Item> relatedTagItemList = itemService.getItemsByTagId(tagBId)
                        .stream()
                        .filter(item -> !swipedItemIdList.contains(item.getId()))
                        .collect(Collectors.toList());

                // 카테고리, 가격 필터링
                relatedTagItemList = filterItemList(userCardSet, relatedTagItemList);

                if(relatedTagItemList.size() == 0) {
                    return relatedTagItemList;
                }

                // 연관 태그 상품 리스트에서 가중치 만큼 개수를 결정
                return relatedTagItemList.subList(0, (int) (20 * rate));

            }).collect(Collectors.toList())
                    .stream()
                    .filter(itemList -> itemList.size() > 0)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

            CardDeck cardDeck = new CardDeck();
            cardDeck.setItemList(userItemList);
            cardDeck.setUserId(userId);

            return cardDeck;

        } catch (Exception e) {

            // 예외 발생시 랜덤하게
            List<Item> userItemList = itemService.getItems().subList(0, 20);
            CardDeck cardDeck = new CardDeck();
            cardDeck.setItemList(userItemList);
            cardDeck.setUserId(userId);

            return cardDeck;
        }
    }

    private List<String> swipedItemIdList(int userId) {

        this.webClient = WebClient.builder().baseUrl(LOG_API_HOST).exchangeStrategies(exchangeStrategies).build();

        return webClient
                .get()
                .uri("/log/swipe/user?userId=" + userId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<SwipeLog>>() {
                })
                .block()
                .stream()
                .map(SwipeLog::getItemId)
                .collect(Collectors.toList());
    }

    private List<Item> filterItemList(UserCardSet userCardSet, List<Item> itemList) {

        int startPrice = Integer.parseInt(userCardSet.getStartPrice());
        int endPrice = Integer.parseInt(userCardSet.getEndPrice());
        String selectedCat = userCardSet.getSelectedCat();

        List<Item> priceFiltered = itemList
                .stream()
                .filter(item -> {
                    String priceStr = item.getPrice();
                    try {
                        int price = Integer.parseInt(priceStr);
                        return price >= startPrice && price <= endPrice;

                    } catch (NumberFormatException e) {
                        return false;
                    }
                }).collect(Collectors.toList());

        List<String> categoryCodeList = getCategoryCodeList(selectedCat);

        return priceFiltered
                .stream()
                .filter(item -> {
                    List<String> itemCategoryCodeList = item.getCategory()
                            .stream()
                            .map(category -> category.getCatCd().getCode())
                            .collect(Collectors.toList());

                    // 선택한 카테고리와 일치하는 상품만 필터링 되도록
                    for (String s : itemCategoryCodeList) {
                        if (categoryCodeList.contains(s)) {
                            return true;
                        }
                    }
                    return false;
                }).collect(Collectors.toList());
    }

    private List<String> getCategoryCodeList(String category) {
        List<String> categoryCodeList = new ArrayList<>();

        for (int i = 0; i < category.length(); i++) {
            if (category.charAt(i) == '1') {
                categoryCodeList.add(mapCategoryCode(i).code);
            }
        }
        return categoryCodeList;
    }

    private CategoryCode mapCategoryCode(int index) {
        switch (index) {
            case 0:
                return CategoryCode.TOP;
            case 1:
                return CategoryCode.BOTTOM;
            case 2:
                return CategoryCode.SKIRT;
            case 3:
                return CategoryCode.OUTER;
            case 4:
                return CategoryCode.DRESS;
            case 5:
                return CategoryCode.SHOES;
            case 6:
                return CategoryCode.HAT;
            case 7:
                return CategoryCode.EXTRA;
            default:
                return null;
        }
    }
}
