package ssho.api.core.service.landing;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ssho.api.core.domain.landing.res.LandingRes;

import java.io.IOException;

@Slf4j
@SpringBootTest
public class LandingServiceImplTest {
    @Autowired
    LandingServiceImpl landingService;

    @Test
    void getLandingByUserId() throws IOException {
        LandingRes res = landingService.getLandingByUserId(72);
        log.info(res.toString());
    }
}