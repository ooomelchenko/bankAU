package nb.service;

import nb.domain.Bid;
import nb.domain.Exchange;
import nb.domain.Lot;
import nb.queryDomain.FondDecisionsByLotHistory;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface LotService {

    Lot getLot(Long id);

    Long createLot(Lot lot);

    Long createLot(String userName, Lot lot);

    boolean delete(Long id);
    boolean delete(Lot lot);

    boolean updateLot(Lot lot);

    boolean updateLot(String userName, Lot lot);

    @Transactional(readOnly = true)
    Lot getLotByLotNum(String lotNum);

    List getLots();

    @Transactional(readOnly = true)
    List getLots(int lotType);

    @Transactional(readOnly = true)
    List getSoldedLots();

    @Transactional(readOnly = true)
    List getSoldedLots(int lotType);

    @Transactional(readOnly = true)
    List getSoldedLots(int lotType, Date start, Date end);

    @Transactional(readOnly = true)
    List getNotSoldedLots();

    @Transactional(readOnly = true)
    List getNotSoldedLots(int lotType);

    @Transactional(readOnly = true)
    List getLotsByType(int lotType);

    @Transactional(readOnly = true)
    List getLotsId();

    BigDecimal lotSum(Lot lot);

    @Transactional(readOnly = true)
    BigDecimal lotAcceptedSum(Lot lot);

    Long lotCount(Lot lot);

    boolean delLot(Lot lot);

    boolean delLot(Long lotId);

    List getAssetsByLot(Lot lot);

    @Transactional(readOnly = true)
    List getTMCAssetsByLot(Lot lot);

    @Transactional(readOnly = true)
    List getNotTMCAssetsByLot(Lot lot);

    @Transactional(readOnly = true)
    List getCRDTSByLot(Lot lot);

    List getAssetsByLot(Long lotId);

    @Transactional(readOnly = true)
    List getLotsByBidDate(Date first, Date last);

    @Transactional(readOnly = true)
    BigDecimal paymentsSumByLot(Lot lot);

    @Transactional(readOnly = true)
    List paymentsByLot(Lot lot);

    @Transactional(readOnly = true)
    List getLotsByBid(Bid bid);

    @Transactional(readOnly = true)
    List getLotsByExchange(Exchange exchange);

    @Transactional(readOnly = true)
    List getBidsIdByLot(Long lotId);

    List getLotsFromHistoryByBid(Bid bid);

    List getLotsFromHistoryByBid(long bidId);

    List getLotsHistoryByBidDates(Date startDate, Date endDate);

    List getLotsHistoryAggregatedByBid(Date bidStartDate, Date bidEndDate);

    List getLotHistoryAggregatedByBid(Long lotId);

    @Transactional(readOnly = true)
    List<FondDecisionsByLotHistory> getFondDecisionsByLotHistory(Long lotId);
}