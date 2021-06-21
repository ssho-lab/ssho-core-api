package ssho.api.core.service.user;

import ssho.api.core.domain.user.model.User;

import java.util.List;

public interface UserAdminService {
    List<User> getUsers();
}
