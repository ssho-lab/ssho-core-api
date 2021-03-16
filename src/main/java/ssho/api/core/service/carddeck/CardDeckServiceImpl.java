package ssho.api.core.service.carddeck;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import ssho.api.core.domain.carddeck.CardDeck;
import ssho.api.core.domain.item.Item;
import ssho.api.core.domain.mall.model.CategoryCode;
import ssho.api.core.domain.mall.model.Mall;
import ssho.api.core.domain.tag.model.Tag;
import ssho.api.core.domain.tutoriallog.TutorialLog;
import ssho.api.core.domain.usercardset.UserCardSet;
import ssho.api.core.domain.useritemcache.model.UserItemCache;
import ssho.api.core.service.item.ItemServiceImpl;
import ssho.api.core.service.mall.MallServiceImpl;
import ssho.api.core.service.tag.TagServiceImpl;
import ssho.api.core.service.usercardset.UserCardSetServiceImpl;
import ssho.api.core.service.useritemcache.UserItemCacheServiceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardDeckServiceImpl implements CardDeckService {

    private  UserItemCacheServiceImpl userItemCacheService;
    private  ItemServiceImpl itemService;
    private  TagServiceImpl tagService;
    private  MallServiceImpl mallService;
    private UserCardSetServiceImpl userCardSetService;

    private WebClient webClient;

    @Value("${log.api.host}")
    private String LOG_API_HOST;

    private final ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder().codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1)).build();

    public CardDeckServiceImpl(UserItemCacheServiceImpl userItemCacheService, ItemServiceImpl itemService, TagServiceImpl tagService, MallServiceImpl mallService, UserCardSetServiceImpl userCardSetService) {
        this.userItemCacheService = userItemCacheService;
        this.itemService = itemService;
        this.tagService = tagService;
        this.mallService = mallService;
        this.userCardSetService = userCardSetService;
    }

    @Override
    public CardDeck cardDeckByUserId(int userId) {

        try {

            boolean tutorialYn = tutorialYn(userId);

            // tutorial 미완료시
            if(!tutorialYn) {
               return tutorialCardDeck(userId);
            }

            UserCardSet userCardSet = userCardSetService.getRecentByUserId(userId);

            UserItemCache userItemCache = userItemCacheService.getUserItemCache(userId);

            List<Item> userItemList = userItemCache
                                            .getItemIdList()
                                            .stream()
                                            .map(itemService::getItemById)
                                            .collect(Collectors.toList());

            List<Item> filteredItemList = filteredItemList(userCardSet, userItemList);

            CardDeck cardDeck = new CardDeck();
            cardDeck.setItemList(filteredItemList);
            cardDeck.setUserId(Integer.parseInt(userItemCache.getUserId()));

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

    private CardDeck tutorialCardDeck(int userId) {

        CardDeck cardDeck = new CardDeck();

        List<String> swipedItemIdList = swipedItemIdList(userId);
        List<String> tagIdList = tagService.getTagList().stream().map(Tag::getId).collect(Collectors.toList());
        List<Item> cardDeckItemList = new ArrayList<>();

        tagIdList.forEach(tagId -> {

            List<Mall> mallList = mallService.getMallList()
                                                    .stream()
                                                    .filter(mall -> mall.getTagList().stream().anyMatch(tag -> tag.getId().equals(tagId)))
                                                    .collect(Collectors.toList());

            List<Item> itemList = mallList.stream().map(mall -> {
                try {
                    return itemService.getItemsByMallNo(mall.getId());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }).flatMap(items -> items != null ? items.stream() : null)
                    .filter(item -> !swipedItemIdList.contains(item.getId()))
                    .collect(Collectors.toList());

            if (itemList.size() > 0) {
                cardDeckItemList.add(itemList.get((int)(Math.random() * itemList.size())));
            }
        });

        Collections.shuffle(cardDeckItemList);
        cardDeck.setItemList(cardDeckItemList);

        return cardDeck;
    }

    private List<String> swipedItemIdList(int userId) {

        this.webClient = WebClient.builder().baseUrl(LOG_API_HOST).exchangeStrategies(exchangeStrategies).build();

        return webClient
                        .get()
                        .uri("/log/tutorial?userId=" + userId)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<List<TutorialLog>>() {
                        })
                        .block()
                        .stream()
                        .map(TutorialLog::getItemId)
                        .collect(Collectors.toList());
    }

    private Boolean tutorialYn(int userId) {

        this.webClient = WebClient.builder().baseUrl(LOG_API_HOST).exchangeStrategies(exchangeStrategies).build();

        return webClient
                        .get()
                        .uri("/log/tutorial/yn?userId=" + userId)
                        .retrieve()
                        .bodyToMono(Boolean.class)
                        .blockOptional().orElse(false);
    }

    private List<Item> filteredItemList(UserCardSet userCardSet, List<Item> itemList) {

        String startPrice = userCardSet.getStartPrice();
        String endPrice = userCardSet.getEndPrice();
        String selectedCat = userCardSet.getSelectedCat();

        List<Item> priceFiltered = itemList.stream().filter(item -> {
            String priceStr = item.getPrice();
            try {
                int price = Integer.parseInt(priceStr);

                if(price >= Integer.parseInt(startPrice) && price <= Integer.parseInt(endPrice)) {
                   return true;
                }
                return false;

            } catch (NumberFormatException e) {
                return false;
            }
        }).collect(Collectors.toList());

        List<String> categoryCodeList = new ArrayList<>();

        for(int i = 0; i < selectedCat.length(); i++) {
            if(selectedCat.charAt(i) == '1'){
                categoryCodeList.add(mapCategoryCode(i).code);
            }
        }

        return priceFiltered.stream().filter(item -> {
            List<String> itemCategoryCodeList = item.getCategory().stream().map(category -> category.getCatCd().getCode()).collect(Collectors.toList());
            for(String s: itemCategoryCodeList) {
                if(categoryCodeList.contains(s)){
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
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
