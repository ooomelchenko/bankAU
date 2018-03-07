package nb.dao;

import nb.domain.AssetHistory;
import nb.queryDomain.AcceptPriceHistory;
import org.hibernate.query.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class AssetHistoryDaoImpl implements AssetHistoryDao {
    @Autowired
    private SessionFactory factory;

    public AssetHistoryDaoImpl(){
    }
    public AssetHistoryDaoImpl(SessionFactory factory){
        this.factory = factory;
    }

    public SessionFactory getFactory() {
        return factory;
    }
    public void setFactory(SessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public Long create( AssetHistory assetHistory) {
        return (Long)factory.getCurrentSession().save(assetHistory);
    }
    @Override
    public  AssetHistory read(Long id) {
        return (AssetHistory)factory.getCurrentSession().get(AssetHistory.class, id);
    }
    @Override
    public boolean update(AssetHistory assetHistory) {
        factory.getCurrentSession().update(assetHistory);
        return true;
    }
    @Override
    public boolean delete(AssetHistory assetHistory) {
        factory.getCurrentSession().delete(assetHistory);
        return true;
    }
    @Override
    public BigDecimal getFirstAccPrice(Long assId) {
        Query query = factory.getCurrentSession().createQuery("select ah.acceptPrice from AssetHistory ah where ah.id=:assId and ah.acceptPrice is not null ORDER BY ah.changeDate ASC ");
        query.setParameter("assId", assId);
        try {
            return (BigDecimal) query.list().get(0);
        }
        catch(IndexOutOfBoundsException e){
            return null;
        }
    }
    @Override
    public BigDecimal getLastAccPrice(Long assId) {
        Query query = factory.getCurrentSession().createQuery("select ah.acceptPrice from AssetHistory ah where ah.id=:assId and ah.acceptPrice is not null ORDER BY ah.changeDate DESC ");
        query.setParameter("assId", assId);
        try {
            return (BigDecimal) query.list().get(0);
        }
        catch(IndexOutOfBoundsException e){
            return null;
        }
    }
    @Override
    public List getLotIdHistoryByAsset(Long assId){
        Query query = factory.getCurrentSession().createQuery("select ah.lotId from AssetHistory ah where ah.id=:assId GROUP BY ah.lotId ");
        query.setParameter("assId", assId);
        try {
            return  query.list();
        }
        catch(IndexOutOfBoundsException e){
            return null;
        }
    }

    @Override
    public BigDecimal getAccPriceByLotIdHistory(Long assetId, Long lotId){
        Query query = factory.getCurrentSession().createQuery("select ah.acceptPrice from AssetHistory ah where ah.idKey in (SELECT max(a.idKey) from AssetHistory a where a.id=:assetId and a.lotId=:lotId)");
        query.setParameter("assetId", assetId);
        query.setParameter("lotId", lotId);
        try {
            return (BigDecimal) query.list().get(0);
        }
        catch(IndexOutOfBoundsException e){
            return null;
        }
    }
    @Override
    public List<AcceptPriceHistory> getDateAndAccPriceHistoryByAsset(Long assId){
        Query query = factory.getCurrentSession().createQuery("select new nb.queryDomain.AcceptPriceHistory(min(ah.changeDate), ah.acceptPrice) from AssetHistory ah where ah.id=:assId and acceptPrice is not null GROUP BY ah.acceptPrice ");
        query.setParameter("assId", assId);
        try {
            return  query.list();
        }
        catch(IndexOutOfBoundsException e){
            return null;
        }
    }

}