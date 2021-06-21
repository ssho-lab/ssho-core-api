package ssho.api.core.service.user;

import ssho.api.core.domain.user.model.User;
import ssho.api.core.domain.user.model.req.SignInReq;
import ssho.api.core.domain.user.model.req.SocialSignInReq;
import ssho.api.core.domain.user.model.req.UserModificationReq;
import ssho.api.core.domain.user.model.res.SignInRes;

import java.util.List;

public interface UserService {

    String saveUser(final User user);

    void updateUser(int userId, UserModificationReq userModificationReq);

    SignInRes authUser(final SignInReq signInReq);

    SignInRes authSocialUser(final SocialSignInReq signInReq);

    boolean checkEmailRegistered(final String email);

    User getUserById(int userId);
}
