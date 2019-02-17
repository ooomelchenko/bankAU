package nb.dao;

import nb.domain.Customer;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomerDaoImpl implements CustomerDao {
    @Autowired
    private SessionFactory factory;

    public CustomerDaoImpl() {
    }
    public CustomerDaoImpl(SessionFactory factory) {
        this.factory=factory;
    }

    public SessionFactory getFactory() {
        return factory;
    }
    public void setFactory(SessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public Long create(Customer customer) {
        return (Long)factory.getCurrentSession().save(customer);
    }

    @Override
    public Customer read(Long id) {
        return factory.getCurrentSession().get(Customer.class, id);
    }

    @Override
    public boolean update(Customer customer) {
        factory.getCurrentSession().update(customer);
        return true;
    }

    @Override
    public boolean delete(Customer customer) {
        factory.getCurrentSession().delete(customer);
        return true;
    }

    @Override
    public Customer findByInn(long inn) {
        Query query = factory.getCurrentSession().createQuery("FROM nb.domain.Customer customer where customerInn = :inn");
        query.setParameter("inn", inn);

        try{
            return (Customer) query.list().get(0);
        }
        catch (IndexOutOfBoundsException e){
            return null;
        }

    }

    @Override
    public List<Customer> findAll() {
        return (List<Customer>) factory.getCurrentSession().createQuery("FROM nb.domain.Customer customer").list();
    }
}
