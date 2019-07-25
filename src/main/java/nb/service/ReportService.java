package nb.service;

import nb.domain.*;
import nb.queryDomain.BidDetails;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface ReportService {

    File makeDodatok(List<Asset> assetList, List<Credit> creditList, String startDate, String endDate) throws IOException;

    String makeOgoloshennya(Long bidId) throws IOException;

    String fillCrdTab(List<Credit> creditList) throws IOException;

    File fillSoldedCrdTab(List<Credit> creditList) throws IOException;

    File fillCreditsReestr(List<Lot> lotList) throws IOException;

    File fillAssTab() throws IOException;

    File getTempFile(MultipartFile multipartFile) throws IOException;

    File makePaymentsReport(List<Pay> payList, String start, String end) throws IOException;

    File makeBidsSumReport(List<LotHistory> lotList, List<BidDetails> aggregatedLotList) throws IOException;
}
