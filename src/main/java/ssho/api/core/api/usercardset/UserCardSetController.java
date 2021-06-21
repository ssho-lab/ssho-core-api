package ssho.api.core.api.usercardset;

import org.springframework.web.bind.annotation.*;
import ssho.api.core.domain.usercardset.UserCardSet;
import ssho.api.core.service.usercardset.UserCardSetServiceImpl;
import ssho.api.core.util.auth.Auth;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/user-cardset")
@RestController
public class UserCardSetController {

    private UserCardSetServiceImpl userCardSetService;

    public UserCardSetController(UserCardSetServiceImpl userCardSetService) {
        this.userCardSetService = userCardSetService;
    }

    /**
     * 최근 회원 카드셋 조회
     *
     * @param httpServletRequest
     * @return
     */
    @Auth
    @GetMapping("")
    public UserCardSet getRecentUserCardSet(final HttpServletRequest httpServletRequest) {
        final int userId = Integer.parseInt(String.valueOf(httpServletRequest.getAttribute("userId")));
        return userCardSetService.getRecentByUserId(userId);
    }

    /**
     * 회원 카드셋 저장
     *
     * @param userCardSet
     * @param httpServletRequest
     * @return
     */
    @Auth
    @PostMapping("")
    public UserCardSet save(@RequestBody UserCardSet userCardSet, final HttpServletRequest httpServletRequest) {
        final int userId = Integer.parseInt(String.valueOf(httpServletRequest.getAttribute("userId")));
        userCardSet.setUserId(userId);
        return userCardSetService.save(userCardSet);
    }
}
