package nb.service;

import nb.dao.AssetDao;
import nb.dao.AssetDaoImpl;
import nb.dao.AssetHistoryDao;
import nb.dao.LotHistoryDao;
import nb.domain.Asset;
import nb.domain.AssetHistory;
import nb.domain.Bid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static nb.controller.util.StringUtils.getBidDateFormatted;
import static nb.controller.util.StringUtils.getExchangeName;

@Service //(name ="assetServiceImpl")
@Transactional
public class AssetServiceImpl implements AssetService {
    @Autowired
    private AssetHistoryDao assetHistoryDao;
    @Autowired
    private AssetDao assetDao;
    @Autowired
    private LotHistoryDao lotHistoryDao;

    public AssetServiceImpl() {
    }

    public AssetServiceImpl(AssetDaoImpl assetDao) {
        this.assetDao = assetDao;
    }

    public AssetDao getAssetDao() {
        return assetDao;
    }

    public void setAssetDao(AssetDao assetDao) {
        this.assetDao = assetDao;
    }

    public AssetHistoryDao getAssetHistoryDao() {
        return assetHistoryDao;
    }

    public void setAssetHistoryDao(AssetHistoryDao assetHistoryDao) {
        this.assetHistoryDao = assetHistoryDao;
    }

    public LotHistoryDao getLotHistoryDao() {
        return lotHistoryDao;
    }

    public void setLotHistoryDao(LotHistoryDao lotHistoryDao) {
        this.lotHistoryDao = lotHistoryDao;
    }

    @Override
    @Transactional(readOnly = true)
    public Asset getAsset(Long id) {
        return assetDao.read(id);
    }

    @Override
    public boolean createAsset(Asset asset) {
        assetDao.create(asset);
        return true;
    }

    @Override
    public boolean createAsset(String userName, Asset asset) {
        assetHistoryDao.create(new AssetHistory(userName, asset));
        assetDao.create(asset);
        return true;
    }

    @Override
    public boolean updateAsset(Asset asset) {
        return assetDao.update(asset);
    }

    @Override
    public boolean updateAsset(String userName, Asset asset) {
        assetHistoryDao.create(new AssetHistory(userName, asset));
        return assetDao.update(asset);
    }

    @Override
    public boolean delete(Long id) {
        assetDao.delete(assetDao.read(id));
        return true;
    }

    @Override
    public boolean delete(Asset asset) {
        assetDao.delete(asset);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Asset> getAssetsByPortion(int num) {
        return assetDao.findAll(num);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalCountOfAssets() {
        return assetDao.totalCount();
    }

    @Override
    public BigDecimal getTotalSumOfAssets() {
        return assetDao.totalSum();
    }

    @Override
    @Transactional(readOnly = true)
    public List getAll() {
        return assetDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List findAllSuccessBids(Date startBidDate, Date endBidDate) {
        return assetDao.findAllSuccessBids(startBidDate, endBidDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List findAllSuccessBids(Date startBidDate, Date endBidDate, int portion) {
        return assetDao.findAllSuccessBids(startBidDate, endBidDate, portion);
    }

    @Override
    @Transactional(readOnly = true)
    public List getRegions() {
        return assetDao.getRegions();
    }

    @Override
    @Transactional(readOnly = true)
    public List getTypes() {
        return assetDao.getTypes();
    }

    @Override
    @Transactional(readOnly = true)
    public List getAssetsByInNum(String inn) {
        return assetDao.getAssetsByINum(inn);
    }

    @Override
    @Transactional(readOnly = true)
    public List getAllAssetsByInNum(String inn) {
        return assetDao.getAllAssetsByINum(inn);
    }

    @Override
    @Transactional(readOnly = true)
    public List getAllBidDates() {
        return assetDao.getAllBidDates();
    }

    @Override
    @Transactional(readOnly = true)
    public List getExchanges() {
        return assetDao.getExchanges();
    }

    @Override
    @Transactional(readOnly = true)
    public List getDecisionNumbers() {
        return assetDao.getDecisionNumbers();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getFirstAccPrice(Long assId) {
        return assetHistoryDao.getFirstAccPrice(assId);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getFirstAccPrice(Asset asset) {
        return assetHistoryDao.getFirstAccPrice(asset.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getLastAccPrice(Asset asset) {
        return assetHistoryDao.getLastAccPrice(asset.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getLastAccPrice(Long id) {
        return assetHistoryDao.getLastAccPrice(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List getLotIdHistoryByAsset(Long assId) {
        return assetHistoryDao.getLotIdHistoryByAsset(assId);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getAccPriceByLotIdHistory(Long assetId, Long lotId) {
        return assetHistoryDao.getAccPriceByLotIdHistory(assetId, lotId);
    }

    @Override
    @Transactional(readOnly = true)
    public List getDateAndAccPriceHistoryByAsset(Long assId) {
        return assetHistoryDao.getDateAndAccPriceHistoryByAsset(assId);
    }

    @Override
    public List getAssetHistory(String inn) {
        List<String> rezList = new ArrayList<>();

        Asset asset = (Asset) getAllAssetsByInNum(inn).get(0);
        List<Long> lotIdList = getLotIdHistoryByAsset(asset.getId());

        lotIdList.forEach(lotId -> {
            List<Bid> bidList = lotHistoryDao.getLotHistoryAggregatedByBid(lotId);

            bidList.stream()
                    .sorted()
                    .map(bid -> String.join("||", asset.getInn(), lotId.toString(), getExchangeName(bid), getBidDateFormatted(bid), getAccPriceByLotIdHistory(asset.getId(), lotId).toString()))
                    .forEach(rezList::add);
        });
        return rezList;
    }

}