package nb.dao;

import nb.domain.CreditHistory;
import nb.queryDomain.CreditAccPriceHistory;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class CreditHistoryDaoImpl implements CreditHistoryDao {
    @Autowired
    private SessionFactory factory;

    public CreditHistoryDaoImpl(){
    }
    public CreditHistoryDaoImpl(SessionFactory factory){
        this.factory = factory;
    }

    public SessionFactory getFactory() {
        return factory;
    }
    public void setFactory(SessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public Long create(CreditHistory creditHistory) {
        return (Long)factory.getCurrentSession().save(creditHistory);
    }
    @Override
    public CreditHistory read(Long id) {
        return (CreditHistory)factory.getCurrentSession().get(CreditHistory.class, id);
    }
    @Override
    public boolean update(CreditHistory creditHistory) {
        factory.getCurrentSession().update(creditHistory);
        return true;
    }
    @Override
    public boolean delete(CreditHistory creditHistory) {
        factory.getCurrentSession().delete(creditHistory);
        return true;
    }
    @Override
    public List getCreditHistoryById(Long id){
        Query query = factory.getCurrentSession().createQuery("FROM nb.domain.CreditHistory credit WHERE credit.id=:id");
        query.setParameter("id", id);
        return query.list();
    }
    @Override
    public List getLotIdHistoryByCredit(Long idBars){
        Query query = factory.getCurrentSession().createQuery("select ch.lotId from CreditHistory ch where ch.nd=:idBars GROUP BY ch.lotId ");
        query.setParameter("idBars", idBars);
        try {
            return  query.list();
        }
        catch(IndexOutOfBoundsException e){
            return null;
        }
    }
    @Override
    public List getLotIdHistoryByCredit(String inn){
        Query query = factory.getCurrentSession().createQuery("select ch.lotId from CreditHistory ch where ch.inn=:inn GROUP BY ch.lotId ");
        query.setParameter("inn", inn);
        try {
            return  query.list();
        }
        catch(IndexOutOfBoundsException e){
            return null;
        }
    }
    @Override
    public List getLotIdHistoryByCredit(String inn, Long idBars){
        Query query = factory.getCurrentSession().createQuery("select ch.lotId from CreditHistory ch where ch.inn=:inn and ch.nd=:idBars GROUP BY ch.lotId ");
        query.setParameter("inn", inn);
        query.setParameter("idBars", idBars);
        try {
            return  query.list();
        }
        catch(IndexOutOfBoundsException e){
            return null;
        }
    }
    @Override
    public BigDecimal getPriceByLotIdHistory(Long crId, Long lotId){
        Query query = factory.getCurrentSession().createQuery("select ch.acceptPrice from CreditHistory ch where (ch.crId=:crId and ch.lotId=:lotId) and ch.acceptPrice is not null ORDER BY ch.changeDate DESC ");
        query.setParameter("crId", crId);
        query.setParameter("lotId", lotId);
        query.setMaxResults(1);
        try {
            System.out.println(query.list());
            return (BigDecimal) query.list().get(0);
        }
        catch(IndexOutOfBoundsException e){
            return null;
        }
    }
    @Override
    public List<CreditAccPriceHistory> getDateAndAccPriceHistoryByCredit(Long crId){
        Query query = factory.getCurrentSession().createQuery("select new nb.queryDomain.CreditAccPriceHistory(min(ch.changeDate), ch.acceptPrice) from CreditHistory ch where ch.crId=:crId and acceptPrice is not null GROUP BY ch.acceptPrice ");
        query.setParameter("crId", crId);
        try {
            return  query.list();
        }
        catch(IndexOutOfBoundsException e){
            return null;
        }
    }
}