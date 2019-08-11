package nb.dao;

import nb.domain.Credit;
import nb.domain.Lot;

import java.util.Date;
import java.util.List;

public interface CreditDao {
    String getFilterText(int isSold, int isInLot, int clientType, int isNbu, int isFondDec, String[] idBarsMass, String[] innMass, String[] idLotMass);

    Long create(Credit credit);
    Credit read(Long id);
    boolean update(Credit credit);
    boolean delete(Credit credit);
    List findAll();

    List findAll(int portionNum);

    List findAll(int portionNum, int isSold, int isInLot, int clientType, int isNbu, int isFondDec, String[] idBarsMass, String[] innMass, String[] idLotMass);

    Long countOfFilteredCredits(int isSold, int isInLot, int clientType, int isNbu, int isFondDec, String[] idBarsMass, String[] innMass, String[] idLotMass);

    Long totalCount();

    Credit findByInventar(String invNum);
    List getRegions();
    List getTypes();
    List getCur();

    String makeQueryText(String[] types, String[] regions, String[] cur);

    List <Credit> selectCredits(String[] types, String[] regions, String[] cur, int dpdMin, int dpdMax, double zbMin, double zbMax);

    List selectCrdSum(String[] types, String[] regions, String[] cur, int dpdMin, int dpdMax, double zbMin, double zbMax);

    boolean addCreditsToLot(Lot lot, String[] types, String[] regions, String[] cur, int dpdMin, int dpdMax, double zbMin, double zbMax);

    int delCreditsFromLot(Long lot);

    List getCreditsByClient(String inn, Long idBars);

    List getAllCreditsByClient(String inn, Long idBars);

    List getCreditsByLot(Long lotId);

    List getCreditsByIdBars(Long id);

    List getCredits_SuccessBids(Date startBids, Date endBids);

    boolean updateLot(List<Long> creditList, Long lot);
}