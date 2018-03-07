package nb.dao;

import nb.domain.Bid;
import nb.domain.Exchange;

import java.util.Date;
import java.util.List;

public interface BidDao {
    Long create(Bid bid);
    Bid read(Long id);
    boolean update(Bid bid);
    boolean delete(Bid bid);
    List findAll();

    Long countOfLots(Bid bid);

    List lotsByBid(Bid bid);

    List assetsByBid(Bid bid);

    List getBidsByExchange(Exchange exchange);

    List getBidsByDates(Date minDate, Date maxDate);
}