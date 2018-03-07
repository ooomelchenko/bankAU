package nb.dao;

import nb.domain.Lot;
import nb.domain.Pay;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface PayDao {
    Long create(Pay pay);
    Pay read(Long id);
    boolean update(Pay pay);
    boolean delete(Pay pay);
    List findAll();

    List getPaysByDates(Date startDate, Date endDate);

    BigDecimal sumByLot(Lot lot);

    BigDecimal sumByLotFromBid(Long lotId);

    BigDecimal sumByLotFromCustomer(Long lotId);

    List getPaymentsByLot(Lot lot);

    Date getLastDateByBid(Long lotId);

    Date getLastDateByCustomer(Long lotId);
}