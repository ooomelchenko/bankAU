package nb.dao;

import nb.domain.User;
import org.hibernate.query.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {
    @Autowired
    private SessionFactory factory;

    public UserDaoImpl(){
    }
    public UserDaoImpl(SessionFactory factory){
        this.factory = factory;
    }

    public SessionFactory getFactory() {
        return factory;
    }
    public void setFactory(SessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public Long create(User user) {
        return (Long)factory.getCurrentSession().save(user);
    }
    @Override
    public User read(Long id) {
        return (User)factory.getCurrentSession().get(User.class, id);
    }
    @Override
    public boolean update(User user) {
        factory.getCurrentSession().update(user);
        return true;
    }
    @Override
    public boolean delete(User user) {
        factory.getCurrentSession().delete(user);
        return true;
    }
    @Override
    public List<User> findAll() {
        List<User>list;
        list =factory.getCurrentSession().createQuery("from nb.domain.User").list();
        return list;
    }
    @Override
    public boolean isExist(String login, String password) {
        Query query = factory.getCurrentSession().createQuery
       ("from nb.domain.User u where u.login=:log and u.password=:pass");
        query.setParameter("log",login);
        query.setParameter("pass",password);
        return !query.list().isEmpty();
    }
    @Override
    public User readByLogin(String login) {
        Query query = factory.getCurrentSession().createQuery("from nb.domain.User u where u.login=:log");
        query.setParameter("log",login);
        return (User)query.list().get(0);
    }
}