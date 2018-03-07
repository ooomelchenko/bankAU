package nb.service;

import nb.dao.ExchangeDao;
import nb.domain.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ExchangeServiceImpl implements ExchangeService {
    @Autowired
    private ExchangeDao exchangeDao;

    public ExchangeServiceImpl() {
    }
    public ExchangeServiceImpl(ExchangeDao exchangeDao) {
        this.exchangeDao=exchangeDao;
    }

    public ExchangeDao getExchangeDao() {
        return exchangeDao;
    }
    public void setExchangeDao(ExchangeDao exchangeDao) {
        this.exchangeDao = exchangeDao;
    }

    @Override
    @Transactional(readOnly = true)
    public Exchange getExchange(Long id) {
        return exchangeDao.read(id);
    }
    @Override
    public boolean createExchange(Exchange exchange) {
        exchangeDao.create(exchange);
        return true;
    }
    @Override
    public void updateExchange(Exchange exchange) {
        exchangeDao.update(exchange);
    }
    @Override
    public boolean delete(Long id) {
        exchangeDao.delete(exchangeDao.read(id));
        return true;
    }
    @Override
    public boolean delete(Exchange exchange) {
        exchangeDao.delete(exchange);
        return true;
    }
    @Override
    @Transactional(readOnly = true)
    public List getAllExchanges(){
        return exchangeDao.findAll();
    }
    @Override
    @Transactional(readOnly = true)
    public List getBidsByExchange(Exchange exchange){
        return exchangeDao.getBidsByExchange(exchange);
    }
}
