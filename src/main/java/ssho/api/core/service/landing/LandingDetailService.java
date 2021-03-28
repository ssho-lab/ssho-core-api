package ssho.api.core.service.landing;

import ssho.api.core.domain.landing.res.LandingDetailRes;

import java.io.IOException;

public interface LandingDetailService {
    LandingDetailRes getLandingDetail(String tagId) throws IOException;
}
