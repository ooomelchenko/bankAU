package nb.dao;

import nb.domain.User;

import java.util.List;

public interface UserDao {
    Long create(User user);
    User read(Long id);
    User readByLogin(String login);
    boolean update(User user);
    boolean delete(User user);
    List<User> findAll();
    boolean isExist(String login, String password);

}