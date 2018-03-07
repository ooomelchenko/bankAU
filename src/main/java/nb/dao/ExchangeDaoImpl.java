package nb.dao;

import nb.domain.Exchange;
import org.hibernate.query.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ExchangeDaoImpl implements ExchangeDao {
    @Autowired
    private SessionFactory factory;

    public ExchangeDaoImpl() {
    }
    public ExchangeDaoImpl(SessionFactory factory) {
        this.factory =factory;
    }

    public SessionFactory getFactory() {
        return factory;
    }
    public void setFactory(SessionFactory factory) {
        this.factory = factory;
    }
    @Override
    public Long create(Exchange exchange) {
        return (Long)factory.getCurrentSession().save(exchange);
    }
    @Override
    public Exchange read(Long id) {
        return (Exchange)factory.getCurrentSession().get(Exchange.class, id);
    }
    @Override
    public boolean update(Exchange exchange) {
        factory.getCurrentSession().update(exchange);
        return true;
    }
    @Override
    public boolean delete(Exchange exchange) {
        factory.getCurrentSession().delete(exchange);
        return true;
    }
    @Override
    public List findAll() {
        List<Exchange>list;
        list =factory.getCurrentSession().createQuery("from nb.domain.Exchange ex order by ex.id").list();
        return list;
    }

    @Override
    public List getBidsByExchange(Exchange exchange){
        Query query = factory.getCurrentSession().createQuery("FROM nb.domain.Bid bid WHERE bid.exchange=:ex");
        query.setParameter("ex", exchange);
        return query.list();
    }
}
