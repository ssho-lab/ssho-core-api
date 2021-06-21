package ssho.api.core.service.user;

import org.springframework.stereotype.Service;
import ssho.api.core.domain.user.model.User;
import ssho.api.core.repository.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserAdminServiceImpl implements UserAdminService {

    private final UserRepository userRepository;

    public UserAdminServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll()
                                .stream()
                                .peek(user -> user.setPassword(""))
                                .collect(Collectors.toList());
    }
}
