package nb.dao;

import nb.domain.Asset;
import nb.domain.Bid;
import nb.domain.Exchange;
import nb.domain.Lot;
import org.hibernate.query.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class BidDaoImpl implements BidDao {
    @Autowired
    private SessionFactory factory;

    public BidDaoImpl() {
    }
    public BidDaoImpl(SessionFactory factory) {
        this.factory=factory;
    }

    public SessionFactory getFactory() {
        return factory;
    }
    public void setFactory(SessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public Long create(Bid bid) {
        return (Long)factory.getCurrentSession().save(bid);
    }
    @Override
    public Bid read(Long id) {
        return factory.getCurrentSession().get(Bid.class, id);
    }
    @Override
    public boolean update(Bid bid) {
        factory.getCurrentSession().update(bid);
        return true;
    }
    @Override
    public boolean delete(Bid bid) {
        factory.getCurrentSession().delete(bid);
        return true;
    }
    @Override
    public List findAll() {
        List<Bid>list;
        list =factory.getCurrentSession().createQuery("FROM nb.domain.Bid b order by b.bidDate DESC").list();
        return list;
    }
    @Override
    public Long countOfLots(Bid bid) {
        Query query =factory.getCurrentSession().createQuery("SELECT count(l.id) FROM nb.domain.Lot l WHERE l.bid=:b");
        query.setParameter("b", bid);
        return (Long) query.list().get(0);
    }
    @Override
    public List<Lot> lotsByBid (Bid bid) {
        Query query = factory.getCurrentSession().createQuery("FROM nb.domain.Lot l WHERE l.bid=:b");
        query.setParameter("b", bid);
        return query.list();
    }
    @Override
    public List<Asset> assetsByBid (Bid bid) {
        Query query = factory.getCurrentSession().createQuery("FROM nb.domain.Asset ass WHERE ass.lot.bid=:b");
        query.setParameter("b", bid);
        return query.list();
    }
    @Override
    public List getBidsByExchange (Exchange exchange){
        Query query = factory.getCurrentSession().createQuery("FROM nb.domain.Bid bid WHERE bid.exchange=:exchange");
        query.setParameter("exchange", exchange);
        return query.list();
    }
    @Override
    public List getBidsByDates(Date minDate, Date maxDate){
        Query query = factory.getCurrentSession().createQuery("FROM nb.domain.Bid bid WHERE bid.bidDate>=:minDate and bid.bidDate<=:maxDate");
        query.setParameter("minDate", minDate);
        query.setParameter("maxDate", maxDate);
        return query.list();
    }
    @Override
    public List getBidsByMinimumDate(Date minDate){
        Query query = factory.getCurrentSession().createQuery("FROM nb.domain.Bid bid WHERE bid.bidDate>=:minDate order by bid.bidDate DESC");
        query.setParameter("minDate", minDate);
        return query.list();
    }
}