package nb.dao;

import nb.domain.Bid;
import nb.domain.Exchange;
import nb.domain.Lot;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface LotDao {
    Long create(Lot lot);
    Lot read(Long id);
    boolean update(Lot lot);
    boolean delete(Lot lot);

    Lot findByLotNum(String lotNum);

    List findAll();

    List<Lot> findAll(int lotType);

    List<Lot> findSolded();

    List<Lot> findSolded(int type);

   // List<Lot> findSolded(int lotType, Date start, Date end);

    List<Lot> findSoldedWithoutDeal(int lotType, Date first, Date last);

    List<Lot> findNotSolded();

    List<Lot> findNotSolded(int lotType);

    List<Lot> findByLotType(int type);

    List<Long> findAllId();

    BigDecimal lotSum(Lot lot);

    BigDecimal lotAcceptedSum(Lot lot);

    Long lotCount(Lot lot);

    List getAssetsByLot(Lot lot);

    List getTMCAssetsByLot(Lot lot);

    List getNotTMCAssetsByLot(Lot lot);

    List getCRDTSByLot(Lot lot);

    List<Lot> getLotsByBidDate(Date first, Date last);

    List getLotsByBid(Bid bid);

    List getLotsByExchange(Exchange exchange);
}