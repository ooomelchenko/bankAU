package nb.service;

import nb.domain.Bid;
import nb.domain.Exchange;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface BidService {
    Bid getBid(Long id);
    Long createBid(Bid bid);
    boolean delete(Long id);
    boolean delete(Bid bid);
    boolean updateBid(Bid bid);
    List getAllBids();

    @Transactional(readOnly = true)
    List getAssetsByBid(Bid bid);

    Long countOfLots(Bid bid);

    List lotsByBid(Bid bid);

    BigDecimal sumByBid(Bid bid);

    @Transactional(readOnly = true)
    List getBidsByExchange(Exchange exchange);

    @Transactional(readOnly = true)
    List getBidsByDates(Date minDate, Date maxDate);
}