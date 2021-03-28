package ssho.api.core.service.landingdetail;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ssho.api.core.domain.landing.res.LandingDetailRes;
import ssho.api.core.service.landing.LandingDetailServiceImpl;

import java.io.IOException;

@Slf4j
@SpringBootTest
public class LandingDetailServiceImplTest {
    @Autowired
    LandingDetailServiceImpl landingDetailService;

    @Test
    void getLandingDetail() throws IOException {
        String tagId = "fc894811d7454cd89ba0f2c21b91d942";
        LandingDetailRes res = landingDetailService.getLandingDetail(tagId);
        log.info(res.toString());
    }
}
