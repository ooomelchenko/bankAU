package nb.service;

import nb.dao.*;
import nb.domain.*;
import nb.queryDomain.FondDecisionsByLotHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service //(name ="LotServiceImpl")
@Transactional
public class LotServiceImpl implements LotService {
    @Autowired
    private LotHistoryDao lotHistoryDao;
    @Autowired
    private LotDao lotDao;
    @Autowired
    private AssetDao assetDao;
    @Autowired
    private PayDao payDao;
    @Autowired
    private CreditDao creditDao;
    @Autowired
    private CreditHistoryDao creditHistoryDao;
    @Autowired
    private AssetService assetService;
    @Autowired
    private UserService userService;

    public LotServiceImpl() {
    }
    public LotServiceImpl(LotDaoImpl lotDao) {
        this.lotDao = lotDao;
    }

    public LotDao getLotDao() {
        return lotDao;
    }
    public void setLotDao(LotDaoImpl lotDao) {
        this.lotDao = lotDao;
    }

    @Override
    @Transactional(readOnly = true)
    public Lot getLot(Long id) {
        return lotDao.read(id);
    }
    @Override
    public Long createLot(Lot lot) {
        return lotDao.create(lot);
    }
    @Override
    @Transactional
    public Long createLot(String userName, Lot lot) {
        Long lotId =lotDao.create(lot);
        lotHistoryDao.create(new LotHistory(userName,lot));
        return lotId;
    }
    @Override
    @Transactional
    public Long createCreditLot(String login, List<Long> creditIdList) {
        User user = userService.getByLogin(login);
        Lot lot = new Lot( "", user, new Date(), 0);
        Long lotId =lotDao.create(lot);
        lotHistoryDao.create(new LotHistory(login,lot));

        creditDao.updateLot(creditIdList, lotId);
        //creditHistoryDao.setLot(unitIdList, lot.getId());
        return lotId;
    }
    @Override
    @Transactional
    public Long createAssetLot(String login, List<String> invent) {
        User user = userService.getByLogin(login);
        Lot lot = new Lot( "", user, new Date(), 1);
        Long lotId =lotDao.create(lot);
        lotHistoryDao.create(new LotHistory(login,lot));

        List<Asset> assetList = assetDao.getAllAssetsByINum(invent);

        for(Asset asset: assetList){
            asset.setLot(lot);
            assetService.updateAsset(login, asset);
        }
        return lotId;
    }
    @Override
    public boolean delete(Long id) {
        Lot lot = lotDao.read(id);
        lotDao.delete(lot);
        return true;
    }
    @Override
    public boolean delete(Lot lot) {
        lotDao.delete(lot);
        return true;
    }
    @Override
    public boolean updateLot( Lot lot) {
        return lotDao.update(lot);
    }
    @Override
    public boolean updateLot(String userName, Lot lot) {
        boolean r =lotDao.update(lot);
        lotHistoryDao.create(new LotHistory(userName,lot));
       return r;
    }
    @Override
    @Transactional(readOnly = true)
    public Lot getLotByLotNum(String lotNum) {
        return lotDao.findByLotNum(lotNum);
    }
    @Override
    @Transactional(readOnly = true)
    public List getLots() {
        return lotDao.findAll();
    }
    @Override
    @Transactional(readOnly = true)
    public List getLots(int lotType) {
        return lotDao.findAll(lotType);
    }
    @Override
    @Transactional(readOnly = true)
    public List getSoldedLots() {
        return lotDao.findSolded();
    }
    @Override
    @Transactional(readOnly = true)
    public List getSoldedLots(int lotType) {
        return lotDao.findSolded(lotType);
    }
    @Override
    public List getSoldedWithoutDealLots(int lotType, Date start, Date end) {
        return lotDao.findSoldedWithoutDeal(lotType, start, end);
    }
    @Override
    @Transactional(readOnly = true)
    public List getNotSoldedLots() {
        return lotDao.findNotSolded();
    }
    @Override
    @Transactional(readOnly = true)
    public List getNotSoldedLots(int lotType) {
        return lotDao.findNotSolded(lotType);
    }
    @Override
    @Transactional(readOnly = true)
    public List getLotsByType(int lotType){
        return lotDao.findByLotType(lotType);
    }
    @Override
    @Transactional(readOnly = true)
    public List getLotsId() {
        return lotDao.findAllId();
    }
    @Override
    @Transactional(readOnly = true)
    public BigDecimal lotSum(Lot lot){
        return lotDao.lotSum(lot);
    }
    @Override
    @Transactional(readOnly = true)
    public BigDecimal lotAcceptedSum(Lot lot){
        return lotDao.lotAcceptedSum(lot);
    }
    @Override
    @Transactional(readOnly = true)
    public Long lotCount(Lot lot){
        return lotDao.lotCount(lot);
    }
    @Override
    @Transactional
    public boolean delLot(Lot lot) {
        try {
            List<Pay> paysByLot = payDao.getPaymentsByLot(lot);

            for (Pay pay : paysByLot) {
                pay.setHistoryLotId(pay.getLotId());
                pay.setLotId(null);
                payDao.update(pay);
            }
        } catch (NullPointerException e) {
        }
        assetDao.delAssetsFromLot(lot);
        creditDao.delCreditsFromLot(lot.getId());
        return lotDao.delete(lot);
    }
    @Override
    public boolean delLot(Long lotId) {
        Lot lot = getLot(lotId);
        return delLot(lot);
    }
    @Override
    @Transactional(readOnly = true)
    public List getAssetsByLot(Lot lot){ 
        return lotDao.getAssetsByLot(lot);
    }
    @Override
    @Transactional(readOnly = true)
    public List getTMCAssetsByLot(Lot lot){
        return lotDao.getTMCAssetsByLot(lot);
    }
    @Override
    @Transactional(readOnly = true)
    public List getNotTMCAssetsByLot(Lot lot){
        return lotDao.getNotTMCAssetsByLot(lot);
    }
    @Override
    @Transactional(readOnly = true)
    public List getCRDTSByLot(Lot lot){
        return lotDao.getCRDTSByLot(lot);
    }
    @Override
    @Transactional(readOnly = true)
    public List getAssetsByLot(Long lotId){
        return lotDao.getAssetsByLot(lotDao.read(lotId));
    }
    @Override
    @Transactional(readOnly = true)
    public List getLotsByBidDate(Date first, Date last){
        return lotDao.getLotsByBidDate(first, last);
    }
    @Override
    @Transactional(readOnly = true)
    public BigDecimal paymentsSumByLot(Lot lot){
        return payDao.sumByLot(lot);
    }
    @Override
    @Transactional(readOnly = true)
    public List paymentsByLot(Lot lot){
        return payDao.getPaymentsByLot(lot);
    }
    @Override
    @Transactional(readOnly = true)
    public List getLotsByBid(Bid bid){
        return lotDao.getLotsByBid(bid);
    }
    @Override
    @Transactional(readOnly = true)
    public List getLotsByExchange(Exchange exchange){
        return lotDao.getLotsByExchange(exchange);
    }
    @Override
    @Transactional(readOnly = true)
    public List getBidsIdByLot(Long lotId){
        return lotHistoryDao.getAllBidsId(lotId);
    }
    @Override
    @Transactional(readOnly = true)
    public List getLotsFromHistoryByBid(Bid bid) {
        return lotHistoryDao.getLotsFromHistoryByBid(bid);
    }
    @Override
    @Transactional(readOnly = true)
    public List getLotsFromHistoryByBid(long bidId) {
        return lotHistoryDao.getLotsFromHistoryByBid(bidId);
    }
    @Override
    @Transactional(readOnly = true)
    public List getLotsHistoryByBidDates(Date startDate, Date endDate) {
        return lotHistoryDao.getLotsHistoryByBidDates(startDate, endDate);
    }
    @Override
    @Transactional(readOnly = true)
    public List getLotsHistoryAggregatedByBid(Date bidStartDate, Date bidEndDate) {
        return lotHistoryDao.getLotsHistoryAggregatedByBid(bidStartDate, bidEndDate);
    }
    @Override
    @Transactional(readOnly = true)
    public List getLotHistoryAggregatedByBid(Long lotId) {
        return lotHistoryDao.getLotHistoryAggregatedByBid(lotId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FondDecisionsByLotHistory> getFondDecisionsByLotHistory(Long lotId){
        return lotHistoryDao.getFondDecisionsByLotHistory(lotId);
    }
}