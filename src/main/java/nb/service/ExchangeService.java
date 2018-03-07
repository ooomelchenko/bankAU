package nb.service;

import nb.domain.Exchange;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ExchangeService {
    @Transactional(readOnly = true)
    Exchange getExchange(Long id);
    boolean createExchange(Exchange exchange);
    void updateExchange(Exchange exchange);
    boolean delete(Long id);
    boolean delete(Exchange exchange);

    List getAllExchanges();

    List getBidsByExchange(Exchange exchange);
}
