package ssho.api.core.api.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssho.api.core.domain.user.model.User;
import ssho.api.core.service.user.UserAdminServiceImpl;

import java.util.List;

@Slf4j
@RequestMapping("/users/admin")
@RestController
public class UserAdminController {

    private final UserAdminServiceImpl userAdminService;

    public UserAdminController(UserAdminServiceImpl userAdminService) {
        this.userAdminService = userAdminService;
    }

    /**
     * 회원 전체 조회
     *
     * @return
     */
    @GetMapping("")
    public List<User> getUsers() {
        return userAdminService.getUsers();
    }
}