package nb.dao;

import nb.domain.Exchange;

import java.util.List;

public interface ExchangeDao {
    Long create(Exchange exchange);
    Exchange read(Long id);
    boolean update(Exchange exchange);
    boolean delete(Exchange exchange);

    List findAll();

    List getBidsByExchange(Exchange exchange);
}
