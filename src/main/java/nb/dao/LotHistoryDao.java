package nb.dao;

import nb.domain.Bid;
import nb.domain.LotHistory;

import java.util.Date;
import java.util.List;

public interface LotHistoryDao {
    Long create(LotHistory lotHistory);
    LotHistory read(Long id);
    boolean update(LotHistory lotHistory);
    boolean delete(LotHistory lotHistory);

    List getAllBidsId(Long lotId);

    List getLotsFromHistoryByBid(Bid bid);


    List getLotsHistoryByBidDates(Date startDate, Date endDate);

    List getLotsFromHistoryByBid(long bidId);

    List getLotsHistoryAggregatedByBid(Date startDate, Date endDate);

    List<Bid> getLotHistoryAggregatedByBid(Long lotId);
}