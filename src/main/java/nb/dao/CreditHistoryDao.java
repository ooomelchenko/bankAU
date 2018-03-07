package nb.dao;

import nb.domain.CreditHistory;
import nb.queryDomain.CreditAccPriceHistory;

import java.math.BigDecimal;
import java.util.List;

public interface CreditHistoryDao {
    Long create(CreditHistory creditHistory);
    CreditHistory read(Long id);
    boolean update(CreditHistory creditHistory);
    boolean delete(CreditHistory creditHistory);

    List getCreditHistoryById(Long id);

    List getLotIdHistoryByCredit(Long idBars);

    List getLotIdHistoryByCredit(String inn);

    List getLotIdHistoryByCredit(String inn, Long idBars);

    BigDecimal getPriceByLotIdHistory(Long id, Long lotId);

    List<CreditAccPriceHistory> getDateAndAccPriceHistoryByCredit(Long id);
}