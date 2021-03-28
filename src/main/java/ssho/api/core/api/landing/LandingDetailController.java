package ssho.api.core.api.landing;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ssho.api.core.domain.landing.res.LandingDetailRes;
import ssho.api.core.service.landing.LandingDetailServiceImpl;

import java.io.IOException;

@RequestMapping("/landing/detail")
@RestController
public class LandingDetailController {

    private final LandingDetailServiceImpl landingDetailService;

    public LandingDetailController(LandingDetailServiceImpl landingDetailService) {
        this.landingDetailService = landingDetailService;
    }

    @GetMapping("")
    LandingDetailRes getLandingDetail(@RequestParam("tagId") String tagId) throws IOException {
        return landingDetailService.getLandingDetail(tagId);
    }
}
