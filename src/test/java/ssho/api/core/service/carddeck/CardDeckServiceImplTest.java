package ssho.api.core.service.carddeck;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ssho.api.core.domain.carddeck.CardDeck;

@Slf4j
@SpringBootTest
public class CardDeckServiceImplTest {
    @Autowired
    CardDeckServiceImpl cardDeckService;

    @Test
    void cardDeckByUserId() {
        int userId = 5;
        CardDeck cardDeck = cardDeckService.cardDeckByUserId(userId);
        log.info(cardDeck.toString());
    }
}
