package nb.service;

import nb.domain.User;

import java.util.List;

public interface UserService {
    User getUser(Long id);
    boolean createUser(User user);
    boolean delete(Long id);
    void updateUser(User user);
    List showUser();
    boolean isExist(String l, String p);

    User getByLogin(String login);
}
