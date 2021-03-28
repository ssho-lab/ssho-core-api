package ssho.api.core.service.useritemcache;

import ssho.api.core.domain.swipelog.model.SwipeLog;
import ssho.api.core.domain.useritemcache.UserItemCache;

import java.io.IOException;
import java.util.List;

public interface UserItemCacheService {

    /**
     * 회원 추천 상품 캐시 업데이트(V2)
     * @return
     */
    void updateUserItemCacheV2() throws IOException;

    /**
     * 회원 추천 상품 캐시 업데이트(V1)
     * @return
     */
    void updateUserItemCacheV1() throws IOException;

    /**
     * 회원 고유 번호로 회원 추천 상품 캐시 조회
     * @param userId
     * @return
     */
    UserItemCache getUserItemCache(int userId) throws IOException;

    List<SwipeLog> swipeLogList(String userId);
}
