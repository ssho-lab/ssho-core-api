package ssho.api.core.service.carddeck;

import ssho.api.core.domain.carddeck.CardDeck;

import java.io.IOException;
import java.util.List;

public interface CardDeckService {

    /**
     * 회원 카드덱 조회
     *
     * @param userId
     * @return
     * @throws IOException
     */
    CardDeck cardDeckByUserId(int userId);
}
