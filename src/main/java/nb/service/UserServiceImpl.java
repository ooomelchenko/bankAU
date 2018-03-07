package nb.service;

import nb.dao.UserDao;
import nb.dao.UserDaoImpl;
import nb.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service //(name ="UserServiceImpl")
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    public UserServiceImpl() {
    }
    public UserServiceImpl(UserDaoImpl userDao) {
        this.userDao = userDao;
    }

    public UserDao getUserDao() {
        return userDao;
    }
    public void setUserDao(UserDaoImpl userDao) {
        this.userDao = userDao;
    }

    @Override
    @Transactional(readOnly = true)
    public User getUser(Long id) {
        return userDao.read(id);
    }
    @Override
    @Transactional(readOnly = true)
    public User getByLogin (String login){
        return userDao.readByLogin(login);
    }
    @Override
    public boolean createUser(User user) {
        userDao.create(user);
        return true;
    }
    @Override
    public boolean delete(Long id) {
        userDao.delete(userDao.read(id));
        return true;
    }
    @Override
    public void updateUser(User user) {
        userDao.update(user);
    }
    @Override
    @Transactional(readOnly = true)
    public List showUser() {
        return userDao.findAll();
    }
    @Override
    public boolean isExist(String l, String p) {
        return userDao.isExist(l, p);
    }

}