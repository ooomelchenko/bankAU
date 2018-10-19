package nb.service;

import nb.dao.CreditDao;
import nb.dao.CreditDaoImpl;
import nb.dao.CreditHistoryDao;
import nb.domain.Credit;
import nb.domain.CreditHistory;
import nb.domain.Lot;
import nb.queryDomain.CreditAccPriceHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service //(name ="creditServiceImpl")
@Transactional
public class CreditServiceImpl implements CreditService {
    @Autowired
    private CreditDao creditDao;
    @Autowired
    private CreditHistoryDao creditHistoryDao;

    public CreditServiceImpl() {
    }
    public CreditServiceImpl(CreditDaoImpl creditDao) {
        this.creditDao = creditDao;
    }

    public CreditDao getCreditDao() {
        return creditDao;
    }
    public void setCreditDao(CreditDaoImpl creditDao) {
        this.creditDao = creditDao;
    }

    @Override
    @Transactional(readOnly = true)
    public Credit getCredit(Long id) {
        return creditDao.read(id);
    }
    @Override
    @Transactional(readOnly = true)
    public Credit getByInventar(String invNum) {
        return creditDao.findByInventar(invNum);
    }
    @Override
    public boolean createCredit(Credit credit) {
        creditDao.create(credit);
        return true;
    }
    @Override
    public boolean createCredit(String user, Credit credit) {
        creditDao.create(credit);
        creditHistoryDao.create(new CreditHistory(user, credit));
        return true;
    }
    @Override
    public boolean delete(Long id) {
        creditDao.delete(creditDao.read(id));
        return true;
    }
    @Override
    public boolean delete(Credit credit) {
        creditDao.delete(credit);
        return true;
    }
    @Override
    public boolean updateCredit(Credit credit) {
      return creditDao.update(credit);
    }
    @Override
    @Transactional(readOnly = true)
    public List <Credit> getAllCredits(){
        return creditDao.findAll();
    }
    @Override
    @Transactional(readOnly = true)
    public List <Credit> getCreditsByPortion(int num){
        return creditDao.findAll(num);
    }
    @Override
    @Transactional(readOnly = true)
    public List <Credit> getCreditsByPortion(int num, int isSold, int isInLot, int clientType, int isNbu, int isFondDec, String[] idBarsMass, String[] innMass, String[] idLotMass){
        return creditDao.findAll(num, isSold, isInLot, clientType, isNbu, isFondDec, idBarsMass , innMass, idLotMass);
    }
    @Override
    @Transactional(readOnly = true)
    public Long countOfFilteredCredits(int isSold, int isInLot, int clientType, int isNbu, int isFondDec, String[] idBarsMass, String[] innMass, String[] idLotMass) {
       return creditDao.countOfFilteredCredits(isSold, isInLot, clientType, isNbu, isFondDec, idBarsMass , innMass, idLotMass);
    }
    @Override
    @Transactional(readOnly = true)
    public Long getTotalCountOfCredits(){
       return creditDao.totalCount();
    }
    @Override
    @Transactional(readOnly = true)
    public List showEquip() {
        return creditDao.findAll();
    }
    @Override
    @Transactional(readOnly = true)
    public List getRegions() {
        return creditDao.getRegions();
    }
    @Override
    @Transactional(readOnly = true)
    public List getTypes() { return creditDao.getTypes();}
    @Override
    @Transactional(readOnly = true)
    public List getCurrencys() {
        return  creditDao.getCur();
    }
    @Override
    @Transactional(readOnly = true)
    public List<Credit> selectCredits(String [] types, String [] regs, String [] currs, int dpdmin, int dpdmax, double zbmin, double zbmax) {
        return creditDao.selectCredits(types, regs, currs, dpdmin, dpdmax, zbmin, zbmax);
    }
    @Override
    @Transactional(readOnly = true)
    public List getCreditsResults(String [] types, String [] regs, String [] currs, int dpdmin, int dpdmax, double zbmin, double zbmax){
        return creditDao.selectCrdSum(types, regs, currs, dpdmin, dpdmax, zbmin, zbmax);
    }
    @Override
    public boolean addCreditsToLot(Lot lot, String[] types, String[] regions, String[] cur, int dpdMin, int dpdMax, double zbMin, double zbMax){
        return creditDao.addCreditsToLot(lot, types, regions, cur, dpdMin, dpdMax, zbMin, zbMax);
    }
    @Override
    @Transactional(readOnly = true)
    public List getCreditsByClient(String inn, Long id){
        return creditDao.getCreditsByClient(inn, id);
    }
    @Override
    @Transactional(readOnly = true)
    public List getAllCreditsByClient(String inn, Long id){
        return creditDao.getAllCreditsByClient(inn, id);
    }
    @Override
    public List getCreditsByIdBars(Long id){
        return creditDao.getCreditsByIdBars(id);
    }
    @Override
    public boolean updateCredit(String userLogin, Credit credit) {
        creditHistoryDao.create(new CreditHistory(userLogin, credit));
        return creditDao.update(credit);
    }
    @Override
    @Transactional(readOnly = true)
    public List getCrditsByLotId(Long lotId){
        return creditDao.getCreditsByLot(lotId);
    }
    @Override
    @Transactional(readOnly = true)
    public List getCredits_SuccessBids(Date startBids, Date endBids) {
        return creditDao.getCredits_SuccessBids(startBids, endBids);
    }
    @Override
    @Transactional(readOnly = true)
    public List getLotIdHistoryByCredit(String crInn){
        return creditHistoryDao.getLotIdHistoryByCredit(crInn);
    }
    @Override
    @Transactional(readOnly = true)
    public List getLotIdHistoryByCredit(Long idBars){
        return creditHistoryDao.getLotIdHistoryByCredit(idBars);
    }
    @Override
    @Transactional(readOnly = true)
    public List getLotIdHistoryByCredit(String crInn, Long idBars){
        return creditHistoryDao.getLotIdHistoryByCredit(crInn, idBars);
    }
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getPriceByLotIdHistory(Long crId, Long lotId){
        return creditHistoryDao.getPriceByLotIdHistory(crId, lotId);
    }
    @Override
    @Transactional(readOnly = true)
    public List<CreditAccPriceHistory> getDateAndAccPriceHistoryByCredit(Long id){
       return  creditHistoryDao.getDateAndAccPriceHistoryByCredit(id);
    }
}