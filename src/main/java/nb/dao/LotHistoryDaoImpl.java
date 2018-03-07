package nb.dao;

import nb.domain.Bid;
import nb.domain.LotHistory;
import nb.queryDomain.BidDetails;
import org.hibernate.query.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class LotHistoryDaoImpl implements LotHistoryDao {
    @Autowired
    private SessionFactory factory;

    public LotHistoryDaoImpl(){
    }
    public LotHistoryDaoImpl(SessionFactory factory){
        this.factory = factory;
    }

    public SessionFactory getFactory() {
        return factory;
    }
    public void setFactory(SessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public Long create(LotHistory lotHistory) {
        return (Long)factory.getCurrentSession().save(lotHistory);
    }
    @Override
    public LotHistory read(Long id) {
        return (LotHistory)factory.getCurrentSession().get(LotHistory.class, id);
    }
    @Override
    public boolean update(LotHistory lotHistory) {
        factory.getCurrentSession().update(lotHistory);
        return true;
    }
    @Override
    public boolean delete(LotHistory lotHistory) {
        factory.getCurrentSession().delete(lotHistory);
        return true;
    }
    @Override
    public List getAllBidsId(Long lotId){
        Query query = factory.getCurrentSession().createQuery("SELECT lotHistory.bidId FROM nb.domain.LotHistory lotHistory Where lotHistory.id=:lotId and lotHistory.bidId is not null GROUP BY lotHistory.bidId ORDER BY max(lotHistory.idKey) DESC");
        query.setParameter("lotId", lotId);
        return query.list();
    }
    @Override
    public List getLotsFromHistoryByBid(Bid bid) {
        return getLotsFromHistoryByBid(bid.getId());
        }
    @Override
    public List getLotsFromHistoryByBid(long bidId) {
        Query query = factory.getCurrentSession().createQuery("FROM nb.domain.LotHistory lh " +
                "Where lh.bidId=:bidId and lh.idKey = (SELECT max(idKey) FROM LotHistory WHERE id=lh.id and lh.bidId=bidId)");
        query.setParameter("bidId", bidId);
        return query.list();
    }

    @Override
    public List getLotsHistoryByBidDates(Date startDate, Date endDate) {
        Query query = factory.getCurrentSession().createQuery("FROM nb.domain.LotHistory lh " +
                "WHERE lh.idKey in (SELECT max(idKey) FROM LotHistory WHERE id=lh.id AND bidId in (SELECT id from Bid bid WHERE bidDate>=:startDate AND bidDate<=:endDate))");
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.list();
    }

    @Override
    public List<BidDetails> getLotsHistoryAggregatedByBid(Date bidStartDate, Date bidEndDate) {
        Query query = factory.getCurrentSession().createQuery("Select new nb.queryDomain.BidDetails(lh.bidId, sum(lh.startPrice)) FROM nb.domain.LotHistory lh " +
                "WHERE lh.idKey in (SELECT max(idKey) FROM LotHistory WHERE id=lh.id AND bidId in (SELECT id from Bid bid WHERE bidDate>=:startDate AND bidDate<=:endDate)) " +
                "GROUP BY lh.bidId");
        query.setParameter("startDate", bidStartDate);
        query.setParameter("endDate", bidEndDate);
        return query.list();
    }

    @Override
    public List<Bid> getLotHistoryAggregatedByBid(Long lotId) {
        Query query = factory.getCurrentSession().createQuery("FROM nb.domain.Bid bid WHERE bid.id in (SELECT lh.bidId FROM LotHistory lh WHERE lh.id=:lotId Group by lh.bidId)");
        query.setParameter("lotId", lotId);
        return query.list();
    }

}