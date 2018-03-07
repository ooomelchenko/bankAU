package nb.service;

import nb.domain.Credit;
import nb.domain.Lot;
import nb.queryDomain.CreditAccPriceHistory;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface CreditService {
    Credit getCredit(Long id);
    boolean createCredit(Credit credit);

    boolean createCredit(String user, Credit credit);

    boolean delete(Long id);
    boolean delete(Credit credit);
    boolean updateCredit(Credit credit);

    List <Credit> getAllCredits();

    List <Credit> getCreditsByPortion(int num);

    @Transactional(readOnly = true)
    List <Credit> getCreditsByPortion(int num, int isSold, int isInLot, int clientType, int isNbu, int isFondDec, String[] idBarsMass, String[] innMass, String[] idLotMass);

    @Transactional(readOnly = true)
    Long countOfFilteredCredits(int isSold, int isInLot, int clientType, int isNbu, int isFondDec, String[] idBarsMass, String[] innMass, String[] idLotMass);

    Long getTotalCountOfCredits();

    List showEquip();
    Credit getByInventar(String invNum);
    List getRegions();
    List getTypes();
    List getCurrencys();

    List<Credit> selectCredits(String[] types, String[] regs, String[] currs, int dpdmin, int dpdmax, double zbmin, double zbmax);

    List getCreditsResults(String[] types, String[] regs, String[] currs, int dpdmin, int dpdmax, double zbmin, double zbmax);

    boolean addCreditsToLot(Lot lot, String[] types, String[] regions, String[] cur, int dpdMin, int dpdMax, double zbMin, double zbMax);

    List getCreditsByClient(String inn, Long id);

    @Transactional(readOnly = true)
    List getAllCreditsByClient(String inn, Long id);

    List getCreditsByIdBars(Long id);

    boolean updateCredit(String userLogin, Credit crdt);

    @Transactional(readOnly = true)
    List getCrditsByLotId(Long lotId);

    @Transactional(readOnly = true)
    List getCredits_SuccessBids(Date startBids, Date endBids);

    @Transactional(readOnly = true)
    List getLotIdHistoryByCredit(String crInn);

    @Transactional(readOnly = true)
    List getLotIdHistoryByCredit(Long idBars);

    @Transactional(readOnly = true)
    List getLotIdHistoryByCredit(String crInn, Long idBars);

    @Transactional(readOnly = true)
    BigDecimal getPriceByLotIdHistory(Long id, Long lotId);

    @Transactional(readOnly = true)
    List<CreditAccPriceHistory> getDateAndAccPriceHistoryByCredit(Long id);
}
