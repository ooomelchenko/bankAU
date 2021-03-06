package nb.dao;

import nb.domain.Asset;
import nb.domain.Lot;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface AssetDao {
    Long create(Asset asset);
    Asset read(Long id);
    boolean update(Asset asset);

    boolean update(List<Asset> assets);

    boolean delete(Asset asset);

    int delAssetsFromLot(Lot lot);

    List findAll();

    List findAllSuccessBids(Date startBids, Date endBids);

    List findAllSuccessBids(Date startBids, Date endBids, int portionNum);

    List findAll(int portionNum);

    Long totalCount();
    BigDecimal totalSum();

    List getRegions();
    List getTypes();

    int delCRDTS(Lot lot);

    List getAssetsByINum(String inn);

    List getAllAssetsByINum(String inn);

    List getAllAssetsByINum(List<String> innList);

    List getAllBidDates();

    List getExchanges();

    List getDecisionNumbers();
}