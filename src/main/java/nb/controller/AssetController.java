package nb.controller;

import nb.domain.*;
import nb.queryDomain.AcceptPriceHistory;
import nb.queryDomain.BidDetails;
import nb.queryDomain.CreditAccPriceHistory;
import nb.service.*;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.util.DateFormatConverter;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@SessionAttributes({"userId", "lotRid", "objIdToDownload", "docName", "docType", "reportPath", "assetPortionNum"})
public class AssetController {

    @Autowired
    private CreditService creditService;
    @Autowired
    private LotService lotService;
    @Autowired
    private UserService userService;
    @Autowired
    private ExchangeService exchangeService;
    @Autowired
    private BidService bidService;
    @Autowired
    private AssetService assetService;
    @Autowired
    private PayService payService;

    private static final SimpleDateFormat sdfshort = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private static final SimpleDateFormat sdfpoints = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
    private static final SimpleDateFormat yearMonthFormat = new SimpleDateFormat("MM/yyyy", Locale.ENGLISH);
    private static final String documentsPath = "C:\\SCAN\\DocumentsByLots\\";
    private static final String bidDocumentsPath = "C:\\SCAN\\DocumentsByBid\\";

    private BigDecimal getCoefficient(BigDecimal divident, BigDecimal divisor) {
        try{
            return divident.divide(divisor, 10, BigDecimal.ROUND_HALF_UP);
        }
        catch (NullPointerException e){
            return BigDecimal.valueOf(0);
        }
    }

    private String makeDodatok(List<Asset> assetList, List<Credit> creditList, String startDate, String endDate) throws IOException {
        POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream("C:\\projectFiles\\Table prodaj.xls"));
        HSSFWorkbook wb = new HSSFWorkbook(fs);
        HSSFSheet sheet = wb.getSheetAt(0);
        //int shiftCount = assetList.size() + 6;

        sheet.getRow(1).getCell(3).setCellValue("Звіт щодо реалізації активів Банку ПАТ \"КБ \"НАДРА\" за період з " + startDate + " по " + endDate);

        //задаем формат даты
        String excelFormatter = DateFormatConverter.convert(Locale.ENGLISH, "yyyy-MM-dd");
        CellStyle cellStyle = wb.createCellStyle();
        CellStyle numStyle = wb.createCellStyle();

        numStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("$#,##0.00"));

        DataFormat poiFormat = wb.createDataFormat();
        cellStyle.setDataFormat(poiFormat.getFormat(excelFormatter));
        //end
        sheet.shiftRows(8, 8, assetList.size()+creditList.size() - 1);
        int numRow = 7;
        int i = 0;
        for (Asset asset : assetList) {
            HSSFRow row = sheet.createRow(numRow);
            i++;
            numRow++;
            int j = 0;
            while (j < 65) {
                row.createCell(j);
                j++;
            }
            Lot lot = asset.getLot();
            Bid bid = lot.getBid();
         //   BigDecimal coeffRV = getCoefficient(asset.getRv(), lotService.lotSum(lot));// asset.getRv().divide(lotService.lotSum(lot), 10, BigDecimal.ROUND_HALF_UP);
            BigDecimal coeffAcc = getCoefficient(asset.getAcceptPrice(), lotService.lotAcceptedSum(lot));
            //
            row.getCell(0).setCellValue(i);
            row.getCell(1).setCellValue(380764);
            if (lot != null && lot.getFondDecisionDate()!=null) {
                row.getCell(2).setCellValue(lot.getFondDecisionDate());
                row.getCell(2).setCellStyle(cellStyle);
            }
            if (lot != null) {
                row.getCell(3).setCellValue(asset.getLot().getLotNum());
            }
            row.getCell(4).setCellValue("AU");
            row.getCell(5).setCellValue(asset.getAssetGroupCode());
            row.getCell(6).setCellValue(asset.getInn());
            row.getCell(7).setCellValue(asset.getAsset_name());
            row.getCell(8).setCellValue(asset.getAsset_descr());
            if (asset.getEksplDate() != null) {
                row.getCell(9).setCellValue(asset.getEksplDate());
                row.getCell(9).setCellStyle(cellStyle);
            }
            row.getCell(10).setCellValue(asset.getOriginalPrice().doubleValue());
            if(asset.getZb()!=null)
            row.getCell(11).setCellValue(asset.getZb().doubleValue());
            row.getCell(12).setCellValue(asset.getRv().doubleValue());
            if (lot.getFirstStartPrice() != null)
               // row.getCell(13).setCellValue(lot.getFirstStartPrice().multiply(coeffRV).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());// Початкова ціна реалізації активу, з ПДВ, грн.
              //  row.getCell(13).setCellValue(asset.getAcceptPrice().doubleValue());// Початкова ціна реалізації активу, з ПДВ, грн.
            try {
                row.getCell(13).setCellValue(assetService.getFirstAccPrice(asset.getId()).doubleValue());
            }
            catch (NullPointerException e){
                System.out.println("firstAccPrice is null");
            }

            row.getCell(43).setCellValue(bid.getExchange().getCompanyName());
            row.getCell(44).setCellValue(bid.getExchange().getInn());
            row.getCell(45).setCellValue(bid.getBidDate());
            row.getCell(45).setCellStyle(cellStyle);
            row.getCell(46).setCellValue(lot.getCountOfParticipants());
            row.getCell(48).setCellValue(lot.getBidStage());

         //   BigDecimal lotStartPrice = lot.getStartPrice();
         //   BigDecimal lotFirstStartPrice = lot.getFirstStartPrice();

           /* if (lot.getFirstStartPrice() != null && lot.getStartPrice() != null)
                row.getCell(49).setCellValue((1 - (lotStartPrice.divide(lotFirstStartPrice, 4, BigDecimal.ROUND_HALF_UP)).doubleValue()) * 100);//Зниження початкової ціни реалізації активу

            if (lot.getStartPrice() != null) {
                BigDecimal assetStartPrive = lot.getStartPrice().multiply(coeffRV).setScale(10, BigDecimal.ROUND_HALF_UP);
                row.getCell(50).setCellValue(assetStartPrive.divide(new BigDecimal(6), 4).multiply(new BigDecimal(5)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()); //Початкова ціна реалізації активу на актуальном аукціоні без ПДВ, грн.
                row.getCell(50).setCellStyle(numStyle);
            }
            if (lot.getStartPrice() != null)
                row.getCell(51).setCellValue(lot.getStartPrice().multiply(coeffRV).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()); //Початкова ціна реалізації активу на актуальном аукціоні з ПДВ, грн.
*/
            row.getCell(49).setCellFormula("(1-AZ"+numRow+"/N"+numRow+ ")*100");//Зниження початкової ціни реалізації активу
            row.getCell(49).setCellStyle(numStyle);
            if (lot.getStartPrice() != null) {
                BigDecimal assetStartPrise = lot.getStartPrice().multiply(coeffAcc).setScale(10, BigDecimal.ROUND_HALF_UP);
                row.getCell(50).setCellValue(assetStartPrise.divide(new BigDecimal(6), 4).multiply(new BigDecimal(5)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()); //Початкова ціна реалізації активу на актуальном аукціоні без ПДВ, грн.
                row.getCell(50).setCellStyle(numStyle);
            }
            if (lot.getStartPrice() != null)
                row.getCell(51).setCellValue(lot.getStartPrice().multiply(coeffAcc).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()); //Початкова ціна реалізації активу на актуальном аукціоні з ПДВ, грн.

            if (lot.getActSignedDate() != null) {
                row.getCell(52).setCellValue(lot.getActSignedDate());
                row.getCell(52).setCellStyle(cellStyle);
            }
            if (asset.getFactPrice() != null)
                row.getCell(53).setCellValue(asset.getFactPrice().doubleValue());
            if (payService.getLastDateByBid(lot.getId()) != null) {
                row.getCell(54).setCellValue(payService.getLastDateByBid(lot.getId()));
                row.getCell(54).setCellStyle(cellStyle);
            }
            if (payService.getLastDateByCustomer(lot.getId()) != null) {
                row.getCell(55).setCellValue(payService.getLastDateByCustomer(lot.getId()));
                row.getCell(55).setCellStyle(cellStyle);
            }
            if (asset.getPaysCustomer() != null) {
                row.getCell(56).setCellValue(asset.getPaysCustomer().doubleValue());
                row.getCell(56).setCellStyle(numStyle);
            }
            if (asset.getPaysBid() != null) {
                row.getCell(57).setCellValue(asset.getPaysBid().doubleValue());
                row.getCell(57).setCellStyle(numStyle);
            }
        }
        for (Credit credit : creditList) {
            HSSFRow row = sheet.createRow(numRow);
            i++;
            numRow++;
            int j = 0;
            while (j < 65) {
                row.createCell(j);
                j++;
            }
            Lot lot = lotService.getLot(credit.getLot());
            Bid bid = lot.getBid();

            row.getCell(0).setCellValue(i);
            row.getCell(1).setCellValue(380764);
            if (lot.getFondDecisionDate() != null) {
                row.getCell(2).setCellValue(lot.getFondDecisionDate());
                row.getCell(2).setCellStyle(cellStyle);
            }
            if (credit.getLot() != null) {
                row.getCell(3).setCellValue(lot.getLotNum());
            }
            row.getCell(4).setCellValue("AU");
            row.getCell(5).setCellValue(credit.getAssetGroupCode());
            row.getCell(14).setCellValue(credit.getFio());
            row.getCell(15).setCellValue(credit.getInn());
            row.getCell(16).setCellValue(credit.getContractNum());
            if (credit.getContractStart() != null) {
                row.getCell(17).setCellValue(credit.getContractStart());
                row.getCell(17).setCellStyle(cellStyle);
            }
            if (credit.getContractStart() != null) {
                row.getCell(18).setCellValue(credit.getContractEnd());
                row.getCell(18).setCellStyle(cellStyle);
            }

            BigDecimal creditStartPrice = credit.getStartPrice();
            BigDecimal creditFirstStartPrice = credit.getFirstStartPrice();

            row.getCell(19).setCellValue(credit.getCurr());
            row.getCell(20).setCellValue(credit.getRv().doubleValue());
            if(credit.getFirstStartPrice()!=null)
            row.getCell(21).setCellValue(credit.getFirstStartPrice().doubleValue());
            row.getCell(22).setCellValue(credit.getGageVid());
            row.getCell(25).setCellValue(credit.getZb().doubleValue());

            row.getCell(43).setCellValue(bid.getExchange().getCompanyName());
            row.getCell(44).setCellValue(bid.getExchange().getInn());
            row.getCell(45).setCellValue(bid.getBidDate());
            row.getCell(45).setCellStyle(cellStyle);

            row.getCell(46).setCellValue(lot.getCountOfParticipants());
            row.getCell(48).setCellValue(lot.getBidStage());

            if (creditFirstStartPrice != null && creditStartPrice != null)
                row.getCell(49).setCellValue((1 - (creditStartPrice.divide(creditFirstStartPrice, 4, BigDecimal.ROUND_HALF_UP)).doubleValue()) * 100);//Зниження початкової ціни реалізації активу

            if (credit.getStartPrice() != null) {
                row.getCell(50).setCellValue(credit.getStartPrice().doubleValue()); //Початкова ціна реалізації активу на актуальном аукціоні без ПДВ, грн.
                row.getCell(51).setCellValue(credit.getStartPrice().doubleValue());
            }
            if (lot.getActSignedDate() != null) {
                row.getCell(52).setCellValue(lot.getActSignedDate());
                row.getCell(52).setCellStyle(cellStyle);
            }
            if (credit.getFactPrice() != null)
                row.getCell(53).setCellValue(credit.getFactPrice().doubleValue());
            if (payService.getLastDateByBid(credit.getLot()) != null) {
                row.getCell(54).setCellValue(payService.getLastDateByBid(lot.getId()));
                row.getCell(54).setCellStyle(cellStyle);
            }
            if (payService.getLastDateByCustomer(credit.getLot()) != null) {
                row.getCell(55).setCellValue(payService.getLastDateByCustomer(lot.getId()));
                row.getCell(55).setCellStyle(cellStyle);
            }
            if (credit.getPaysCustomer() != null) {
                row.getCell(56).setCellValue(credit.getPaysCustomer().doubleValue());
                row.getCell(56).setCellStyle(numStyle);
            }
            if (credit.getPaysBid() != null) {
                row.getCell(57).setCellValue(credit.getPaysBid().doubleValue());
                row.getCell(57).setCellStyle(numStyle);
            }
        }

        int tableEnd = 7 + assetList.size()+creditList.size();
        HSSFRow sumRow = sheet.getRow(tableEnd);
        sumRow.getCell(10).setCellFormula("SUM(K8:K" + tableEnd + ")");
        sumRow.getCell(11).setCellFormula("SUM(L8:L" + tableEnd + ")");
        sumRow.getCell(12).setCellFormula("SUM(M8:M" + tableEnd + ")");
        sumRow.getCell(13).setCellFormula("SUM(N8:N" + tableEnd + ")");
        sumRow.getCell(50).setCellFormula("SUM(AY8:AY" + tableEnd + ")");
        sumRow.getCell(51).setCellFormula("SUM(AZ8:AZ" + tableEnd + ")");
        sumRow.getCell(53).setCellFormula("SUM(BB8:BB" + tableEnd + ")");
        sumRow.getCell(56).setCellFormula("SUM(BE8:BE" + tableEnd + ")");
        sumRow.getCell(57).setCellFormula("SUM(BF8:BF" + tableEnd + ")");

        String fileName = "C:\\projectFiles\\" + ("Table prodaj " + startDate + " по " + endDate + ".xls");
        OutputStream fileOut = new FileOutputStream(fileName);

        wb.write(fileOut);
        fileOut.close();
        return fileName;
    }

    private String makeOgoloshennya(Long bidId) throws IOException {

        Bid bid = bidService.getBid(bidId);
        List<Lot> lotsByBidList = lotService.getLotsByBid(bid);
        Set<String> decisionsSet = new TreeSet<>();
        for (Lot lot : lotsByBidList) {
            if(lot.getFondDecisionDate()!=null)
            decisionsSet.add(lot.getDecisionNumber() + " від " + sdfpoints.format(lot.getFondDecisionDate()));
        }

        InputStream fs = new FileInputStream("C:\\\\projectFiles\\\\Dodatok 2.docx");

        XWPFDocument docx = new XWPFDocument(fs);
        List<XWPFTable> tableList = docx.getTables();

        String lotNums = "";
        for (Lot lot : lotsByBidList) {
            lotNums += "№ " + lot.getLotNum() + ", ";
        }
        lotNums = lotNums.substring(0, lotNums.length() - 2);

        for (XWPFParagraph p : docx.getParagraphs()) {
            List<XWPFRun> runs = p.getRuns();
            if (runs != null) {
                for (XWPFRun r : runs) {
                    String text = r.getText(0);
                    if (text != null && text.contains("nlots")) {
                        text = text.replace("nlots", lotNums);
                        r.setText(text, 0);
                    } else if (text != null && text.contains("exchange")) {
                        text = text.replace("exchange", bid.getExchange().getCompanyName());
                        r.setText(text, 0);
                    } else if (text != null && text.contains("webSite") && bid.getExchange().getEmail() != null) {
                        text = text.replace("webSite", bid.getExchange().getEmail());
                        r.setText(text, 0);
                    }
                }
            }
        }
        //Заполнение таблиц
        XWPFTable tab1 = tableList.get(0);
        XWPFTable tab2 = tableList.get(1);
        XWPFTable tab3 = tableList.get(2);

        //Таблица 1 заполнение
        if (bid.getExchange().getReq() != null) {
            tab1.getRow(7).getCell(1).setText(bid.getExchange().getReq());
        }
        for (int i = 0; i < lotsByBidList.size(); i++) {
            String assetName = "";
            String assetDesc = "";
            Lot lot = lotsByBidList.get(i);

            List<Asset> notTMCList = lotService.getNotTMCAssetsByLot(lot);
            List<Asset> TMCList = lotService.getTMCAssetsByLot(lot);
            for (Asset asset : notTMCList) {
                assetName += asset.getAsset_name() + " ";
                assetDesc += asset.getAsset_descr() + " ";
            }
            if (TMCList.size() > 0) {
                assetDesc += " +" + TMCList.size() + " од. ТМЦ";
            }
            XWPFTableRow row = tab1.getRow(i + 1);
            row.getCell(0).setText(lot.getLotNum());
            row.getCell(1).setText(assetName);
            row.getCell(2).setText(assetDesc);
            row.getCell(3).setText(String.valueOf(lot.getStartPrice()));

            if ((lotsByBidList.size() - i) > 1)
                tab1.createRow();

            //частичное заполнение табл2
            if (lot.getBidStage().equals("Перші торги")) {
                tab2.getRow(0).getCell(1).setText(lot.getLotNum() + " - Вперше; ");
                tab3.getRow(0).getCell(1).setText(lot.getLotNum() + " - Вперше; ");
            } else {
                tab2.getRow(0).getCell(1).setText(lot.getLotNum() + " - Повторно;  ");
                tab3.getRow(0).getCell(1).setText(lot.getLotNum() + " - Повторно; ");
            }
            //частичное заполнение табл3
            tab3.getRow(1).getCell(1).setText(assetDesc + "; ");
        }

        //Таблица 2 заполнение
        for (String st : decisionsSet) {
            // XWPFTableRow tab2row2 = tab2.getRow(1);
            tab2.getRow(1).getCell(1).setText("Рішення № " + st + "; ");
        }
        tab2.getRow(2).getCell(1).setText(bid.getExchange().getCompanyName() + ", " + bid.getExchange().getPostAddress() + ", працює щоденно крім вихідних з 09.00 до 17.00, www.aukzion.com.ua");

        tab2.getRow(12).getCell(1).setText(String.valueOf(sdfpoints.format(bid.getBidDate())) + " року");
        tab2.getRow(14).getCell(1).setText(String.valueOf(bid.getExchange().getEmail()));

        if (bid.getRegistrEndDate() != null) {
            tab2.getRow(16).getCell(1).setText(String.valueOf("до 17 год. 00 хв. " + sdfpoints.format(bid.getRegistrEndDate())) + " року");
            tab2.getRow(17).getCell(1).setText(String.valueOf("до 17 год. 00 хв. " + sdfpoints.format(bid.getRegistrEndDate())) + " року; ");
            tab2.getRow(17).getCell(1).setText(String.valueOf("до 17 год. 00 хв. " + sdfpoints.format(bid.getRegistrEndDate())) + " року");
        }

        //Таблица 3 заполнение
        tab3.getRow(3).getCell(1).setText(String.valueOf(sdfpoints.format(bid.getBidDate())) + " р.");

        String fileName = "C:\\projectFiles\\Dodatok 2 (" + String.valueOf(sdfshort.format(bid.getBidDate())) + ").docx";
        OutputStream fileOut = new FileOutputStream(fileName);

        docx.write(fileOut);
        fileOut.close();
        return fileName;
    }

    private String fillCrdTab(List<Credit> creditList) throws IOException {
        InputStream ExcelFileToRead = new FileInputStream("C:\\projectFiles\\Temp.xlsx");
        XSSFWorkbook wb = new XSSFWorkbook(ExcelFileToRead);
        XSSFSheet sheet = wb.getSheetAt(0);

        /*POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream("C:\\projectFiles\\Credits.xls"));
        HSSFWorkbook wb = new HSSFWorkbook(fs);
        HSSFSheet sheet = wb.getSheetAt(0);*/

        //задаем формат даты
        String excelFormatter = DateFormatConverter.convert(Locale.ENGLISH, "yyyy-MM-dd");
        CellStyle cellStyle = wb.createCellStyle();
        CellStyle numStyle = wb.createCellStyle();

        DataFormat poiFormat = wb.createDataFormat();
        cellStyle.setDataFormat(poiFormat.getFormat(excelFormatter));

        numStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("$#,##0.00"));
        //end

        int numRow = 1;
       // int i = 0;
        for(Credit credit : creditList){
            XSSFRow row = sheet.createRow(numRow);
            numRow++;
            row.createCell(0).setCellValue(credit.getNd());
            row.createCell(1).setCellValue(credit.getId());
            row.createCell(2).setCellValue(credit.getInn());
            row.createCell(3).setCellValue(credit.getRegion());
            row.createCell(4).setCellValue(credit.getMfo());
            row.createCell(5).setCellValue(credit.getAssetTypeCode());
            row.createCell(6).setCellValue(credit.getAssetGroupCode());
            row.createCell(7).setCellValue(credit.getClientType());
            row.createCell(8).setCellValue(credit.getProduct());
            row.createCell(9).setCellValue(credit.getZast());
            row.createCell(10).setCellValue(credit.getFio());
        }

        String fileName = "C:\\projectFiles\\" + ("Credits " +sdfshort.format(new Date())+ ".xlsx");
        OutputStream fileOut = new FileOutputStream(fileName);

        wb.write(fileOut);
        fileOut.close();
        return fileName;
    }

    private String fillAssTab() throws IOException {
        List<Lot> lotList = lotService.getLotsByType(1);

        InputStream ExcelFileToRead = new FileInputStream("C:\\projectFiles\\Temp.xlsx");
        XSSFWorkbook xwb = new XSSFWorkbook(ExcelFileToRead);
        SXSSFWorkbook wb = new SXSSFWorkbook(xwb);
        SXSSFSheet sheet = wb.getSheetAt(0);
        SXSSFSheet lotSheet = wb.getSheetAt(1);

        //задаем формат даты
        String excelFormatter = DateFormatConverter.convert(Locale.ENGLISH, "yyyy-MM-dd");
        CellStyle dateStyle = wb.createCellStyle();
        CellStyle numStyle = wb.createCellStyle();

        DataFormat poiFormat = wb.createDataFormat();
        dateStyle.setDataFormat(poiFormat.getFormat(excelFormatter));

        numStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("$#,##0.00"));
        //end

        //Заполнение листа с лотами
        SXSSFRow lotTitleRow = lotSheet.createRow(0);
        lotTitleRow.createCell(0).setCellValue("ID");
        lotTitleRow.createCell(1).setCellValue("LotNum");
        lotTitleRow.createCell(2).setCellValue("Status");
        lotTitleRow.createCell(3).setCellValue("Sold");
        lotTitleRow.createCell(4).setCellValue("Comments");
        lotTitleRow.createCell(5).setCellValue("Lot_Created");
        lotTitleRow.createCell(6).setCellValue("BID_Stage");
        lotTitleRow.createCell(7).setCellValue("COUNT_OF_PARTICIPANTS");
        lotTitleRow.createCell(8).setCellValue("START_PRICE");
        lotTitleRow.createCell(9).setCellValue("FIRST_START_PRICE");
        lotTitleRow.createCell(10).setCellValue("FACT_PRICE");
        lotTitleRow.createCell(11).setCellValue("CUSTOMER");
        lotTitleRow.createCell(12).setCellValue("RESULT_STATUS");
        lotTitleRow.createCell(13).setCellValue("ACT_SIGNED_DATE");
        lotTitleRow.createCell(14).setCellValue("BID_DATE");
        lotTitleRow.createCell(15).setCellValue("EXCHANGE_NAME");

        SXSSFRow lotUKRTitleRow = lotSheet.createRow(1);
        lotUKRTitleRow.createCell(0).setCellValue("ID");
        lotUKRTitleRow.createCell(1).setCellValue("Номер лоту");
        lotUKRTitleRow.createCell(2).setCellValue("Стадія роботи");
        lotUKRTitleRow.createCell(3).setCellValue("Продано");
        lotUKRTitleRow.createCell(4).setCellValue("Коментарі");
        lotUKRTitleRow.createCell(5).setCellValue("Дата створення лоту");
        lotUKRTitleRow.createCell(6).setCellValue("Номер торгів");
        lotUKRTitleRow.createCell(7).setCellValue("К-ть учасників");
        lotUKRTitleRow.createCell(8).setCellValue("Стартова ціна");
        lotUKRTitleRow.createCell(9).setCellValue("Початкова ціна");
        lotUKRTitleRow.createCell(10).setCellValue("Ціна проажу");
        lotUKRTitleRow.createCell(11).setCellValue("Покупець");
        lotUKRTitleRow.createCell(12).setCellValue("Результат торгів");
        lotUKRTitleRow.createCell(13).setCellValue("Дата підписання акту");
        lotUKRTitleRow.createCell(14).setCellValue("Дата торгів");
        lotUKRTitleRow.createCell(15).setCellValue("Біржа");

        int lotN=2;
        for(Lot lot: lotList){
            SXSSFRow lotDataRow = lotSheet.createRow(lotN);
            lotN++;
            lotDataRow.createCell(0).setCellValue(lot.getId());
            lotDataRow.createCell(1).setCellValue(lot.getLotNum());
            lotDataRow.createCell(2).setCellValue(lot.getWorkStage());
            lotDataRow.createCell(3).setCellValue(lot.getItSold());
            lotDataRow.createCell(4).setCellValue(lot.getComment());
            try{
            lotDataRow.createCell(5).setCellValue(lot.getLotDate());
                lotDataRow.getCell(5).setCellStyle(dateStyle);
            }
            catch(NullPointerException e){
            }
            lotDataRow.createCell(6).setCellValue(lot.getBidStage());
            lotDataRow.createCell(7).setCellValue(lot.getCountOfParticipants());
            try {
                lotDataRow.createCell(8).setCellValue(lot.getStartPrice().doubleValue());
            }
            catch(NullPointerException e){
            }
            try {
                lotDataRow.createCell(9).setCellValue(lot.getFirstStartPrice().doubleValue());
            }
            catch(NullPointerException e){
            }
            try {
            lotDataRow.createCell(10).setCellValue(lot.getFactPrice().doubleValue());
            }
            catch(NullPointerException e){
            }
            lotDataRow.createCell(11).setCellValue(lot.getCustomerName());
            lotDataRow.createCell(12).setCellValue(lot.getStatus());
            try {
                lotDataRow.createCell(13).setCellValue(lot.getActSignedDate());
                lotDataRow.getCell(13).setCellStyle(dateStyle);
            }
            catch(NullPointerException e){
            }
            try{
                lotDataRow.createCell(14).setCellValue(lot.getBid().getBidDate());
                lotDataRow.getCell(14).setCellStyle(dateStyle);
            }
            catch (NullPointerException e){
            }
            try{
                lotDataRow.createCell(15).setCellValue(lot.getBid().getExchange().getCompanyName());
            }
            catch (NullPointerException e){
            }
        }

        //Заполнение листа с активами
        SXSSFRow titleRow = sheet.createRow(0);
        titleRow.createCell(0).setCellValue("MY_ID");
        titleRow.createCell(1).setCellValue("INVENT");
        titleRow.createCell(2).setCellValue("TYPE_CODE");
        titleRow.createCell(3).setCellValue("GROUP_CODE");
        titleRow.createCell(4).setCellValue("ASSET_NAME");
        titleRow.createCell(5).setCellValue("ASSET_DESCRIPTION");
        titleRow.createCell(6).setCellValue("VIDDIL");
        titleRow.createCell(7).setCellValue("BALANCE_ACCOUNT");
        titleRow.createCell(8).setCellValue("EKSPL_VVedeno_DATE");
        titleRow.createCell(9).setCellValue("PERVISNA_VARTIST_UAH");
        titleRow.createCell(10).setCellValue("BALANCE_COST_UAH");
        titleRow.createCell(11).setCellValue("RV_BEZ_PDV_UAH");
        titleRow.createCell(12).setCellValue("RV_UAH");
        titleRow.createCell(13).setCellValue("REGION");
        titleRow.createCell(14).setCellValue("FACT_SALE_PRICE_UAH");
        titleRow.createCell(15).setCellValue("IS_IT_SOLD");
        titleRow.createCell(16).setCellValue("NBU_APPROVE");
        titleRow.createCell(17).setCellValue("FOND_DEC_DATE");
        titleRow.createCell(18).setCellValue("FOND_DECISION");
        titleRow.createCell(19).setCellValue("FOND_DECISION_NUM");
        titleRow.createCell(20).setCellValue("ACCEPTED_PRICE");
        titleRow.createCell(21).setCellValue("NEED_NEW_FD");
        titleRow.createCell(22).setCellValue("PAYMENTS_BID");
        titleRow.createCell(23).setCellValue("PAYMENTS_CUSTOMER");
        titleRow.createCell(24).setCellValue("LAST_BID_PAY_DATE");
        titleRow.createCell(25).setCellValue("LAST_CUSTOMER_PAY_DATE");
        titleRow.createCell(26).setCellValue("PLAN_SALE_DATE");
        titleRow.createCell(27).setCellValue("ACCEPTED_EXCHANGE");
        titleRow.createCell(28).setCellValue("LOT_ID");
        titleRow.createCell(29).setCellValue("LOT_NUM");
        titleRow.createCell(30).setCellValue("WORK_STAGE");
        titleRow.createCell(31).setCellValue("BID_STAGE");
        titleRow.createCell(32).setCellValue("RESULT_STATUS");
        titleRow.createCell(33).setCellValue("BID_DATE");
        titleRow.createCell(34).setCellValue("EXCHANGE");
        SXSSFRow titleUKRRow = sheet.createRow(1);
        titleUKRRow.createCell(0).setCellValue("ID");
        titleUKRRow.createCell(1).setCellValue("Інвентарний N");
        titleUKRRow.createCell(2).setCellValue("Код типу активу");
        titleUKRRow.createCell(3).setCellValue("Код групи активу");
        titleUKRRow.createCell(4).setCellValue("Назва активу");
        titleUKRRow.createCell(5).setCellValue("Опис активу");
        titleUKRRow.createCell(6).setCellValue("Відділення");
        titleUKRRow.createCell(7).setCellValue("Балансовий рахунок");
        titleUKRRow.createCell(8).setCellValue("Дата введення в експлуатацію");
        titleUKRRow.createCell(9).setCellValue("Первісна вартість, грн.");
        titleUKRRow.createCell(10).setCellValue("Балансова вартість, грн.");
        titleUKRRow.createCell(11).setCellValue("Оцінка без ПДВ, грн.");
        titleUKRRow.createCell(12).setCellValue("Оцінка з ПДВ, грн.");
        titleUKRRow.createCell(13).setCellValue("Регіон");
        titleUKRRow.createCell(14).setCellValue("Ціна фактичного продажу, грн.");
        titleUKRRow.createCell(15).setCellValue("Продано");
        titleUKRRow.createCell(16).setCellValue("В заставі НБУ");
        titleUKRRow.createCell(17).setCellValue("Дата рішення фонду");
        titleUKRRow.createCell(18).setCellValue("Рівень прийняття рішення ФГВФО");
        titleUKRRow.createCell(19).setCellValue("Номер рішення фонду");
        titleUKRRow.createCell(20).setCellValue("Затверджена ФГВФО ціна, грн.");
        titleUKRRow.createCell(21).setCellValue("Необхідне перепогодження ФГВФО");
        titleUKRRow.createCell(22).setCellValue("Сплачено біржею, грн.");
        titleUKRRow.createCell(23).setCellValue("Сплачено покупцем, грн.");
        titleUKRRow.createCell(24).setCellValue("Дата платежу від біржі");
        titleUKRRow.createCell(25).setCellValue("Дата платежу покупця");
        titleUKRRow.createCell(26).setCellValue("Планова дата реалізації");
        titleUKRRow.createCell(27).setCellValue("Затверджена ФГВФО біржа");
        titleUKRRow.createCell(28).setCellValue("LOT_ID");
        titleUKRRow.createCell(29).setCellValue("Номер Лоту");
        titleUKRRow.createCell(30).setCellValue("Стадія роботи");
        titleUKRRow.createCell(31).setCellValue("BID_STAGE");
        titleUKRRow.createCell(32).setCellValue("Результат торгів");
        titleUKRRow.createCell(33).setCellValue("Дата торгів");
        titleUKRRow.createCell(34).setCellValue("Біржа");

        List<Asset> assetList = assetService.getAll();

        int numRow = 2;

        /*for(int i=0; i< assetList.size(); i++){
            sheet.createRow(i+2);
        }*/

        for (Asset asset : assetList) {
            SXSSFRow row = sheet.createRow(numRow);
            numRow++;
            row.createCell(0).setCellValue(asset.getId());
            row.createCell(1).setCellValue(asset.getInn());
            row.createCell(2).setCellValue(asset.getAssetTypeCode());
            row.createCell(3).setCellValue(asset.getAssetGroupCode());
            row.createCell(4).setCellValue(asset.getAsset_name());
            row.createCell(5).setCellValue(asset.getAsset_descr());
            row.createCell(6).setCellValue(asset.getViddil());
            row.createCell(7).setCellValue(asset.getBalanceAccount());
            try {
                row.createCell(8).setCellValue(asset.getEksplDate());
                row.getCell(8).setCellStyle(dateStyle);
            } catch (NullPointerException npe) {
            }
            try {
                row.createCell(9).setCellValue(asset.getOriginalPrice().doubleValue());
            } catch (NullPointerException e) {
            }
            try {
                row.createCell(10).setCellValue(asset.getZb().doubleValue());
            } catch (NullPointerException e) {
            }
            try {
                row.createCell(11).setCellValue(asset.getRvNoPdv().doubleValue());
            } catch (NullPointerException e) {
            }
            try {
                row.createCell(12).setCellValue(asset.getRv().doubleValue());
            } catch (NullPointerException e) {
            }
            row.createCell(13).setCellValue(asset.getRegion());
            if (asset.getFactPrice() != null)
                row.createCell(14).setCellValue(asset.getFactPrice().doubleValue());
            row.createCell(15).setCellValue(asset.isSold());
            row.createCell(16).setCellValue(asset.isApproveNBU());
            /*try {
                row.createCell(17).setCellValue(asset.getFondDecisionDate());
                row.getCell(17).setCellStyle(dateStyle);
            } catch (NullPointerException e) {
            }*/
            /*row.createCell(18).setCellValue(asset.getFondDecision());
            row.createCell(19).setCellValue(asset.getDecisionNumber());*/
            try {
                row.createCell(20).setCellValue(asset.getAcceptPrice().doubleValue());
            } catch (NullPointerException e) {
            }
            /*if(asset.isNeedNewFondDec())
            row.createCell(21).setCellValue("Так");
            else{
                row.createCell(21).setCellValue("Ні");
            }*/
            try {
                row.createCell(22).setCellValue(asset.getPaysBid().doubleValue());
            } catch (NullPointerException e) {
            }
            try {
                row.createCell(23).setCellValue(asset.getPaysCustomer().doubleValue());
            } catch (NullPointerException e) {
            }
            try {
                row.createCell(24).setCellValue(asset.getBidPayDate());
                row.getCell(24).setCellStyle(dateStyle);
            } catch (NullPointerException e) {
            }
            try {
                row.createCell(25).setCellValue(asset.getCustomerPayDate());
                row.getCell(25).setCellStyle(dateStyle);
            } catch (NullPointerException e) {
            }
            /*try {
                row.createCell(26).setCellValue(asset.getPlanSaleDate());
                row.getCell(26).setCellStyle(dateStyle);
            } catch (NullPointerException e) {
            }
            row.createCell(27).setCellValue(asset.getAcceptExchange());*/
            Lot lot = asset.getLot();
            if (lot != null) {
                try {
                    row.createCell(17).setCellValue(lot.getFondDecisionDate());
                    row.getCell(17).setCellStyle(dateStyle);
                } catch (NullPointerException e) {
                }
                row.createCell(18).setCellValue(lot.getFondDecision());
                row.createCell(19).setCellValue(lot.getDecisionNumber());
                if(lot.isNeedNewFondDec())
                    row.createCell(21).setCellValue("Так");
                else{
                    row.createCell(21).setCellValue("Ні");
                }
                try {
                    row.createCell(26).setCellValue(lot.getPlanSaleDate());
                    row.getCell(26).setCellStyle(dateStyle);
                } catch (NullPointerException e) {
                }
                row.createCell(27).setCellValue(lot.getAcceptExchange());
                row.createCell(28).setCellValue(lot.getId());
                row.createCell(29).setCellValue(lot.getLotNum());
                row.createCell(30).setCellValue(lot.getWorkStage());
                row.createCell(31).setCellValue(lot.getBidStage());
                row.createCell(32).setCellValue(lot.getStatus());
                Bid bid = lot.getBid();
                if (bid != null) {
                    row.createCell(33).setCellValue(bid.getBidDate());
                    row.getCell(33).setCellStyle(dateStyle);
                    row.createCell(34).setCellValue(bid.getExchange().getCompanyName());
                }
            }
        }

        String fileName = "C:\\projectFiles\\" + ("Assets " +sdfshort.format(new Date())+ ".xlsx");
        OutputStream fileOut = new FileOutputStream(fileName);

        wb.write(fileOut);
        fileOut.flush();
        fileOut.close();
        return fileName;
    }

    private File getTempFile(MultipartFile multipartFile) throws IOException {

        CommonsMultipartFile commonsMultipartFile = (CommonsMultipartFile) multipartFile;
        FileItem fileItem = commonsMultipartFile.getFileItem();
        DiskFileItem diskFileItem = (DiskFileItem) fileItem;
        String absPath = diskFileItem.getStoreLocation().getAbsolutePath();
        File file = new File(absPath);

//trick to implicitly save on disk small files (<10240 bytes by default)

        if (!file.exists()) {
            file.createNewFile();
            multipartFile.transferTo(file);
        }

        return file;
    }

    private String makePaymentsReport(List <Pay> payList, String start, String end){
        InputStream ExcelFileToRead = null;
        try {
            ExcelFileToRead = new FileInputStream("C:\\projectFiles\\Pays.xlsx");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        XSSFWorkbook wb = null;
        try {
            wb = new XSSFWorkbook(ExcelFileToRead);
        } catch (IOException e) {
            e.printStackTrace();
        }
        XSSFSheet sheet = wb.getSheetAt(0);

        String excelFormatter = DateFormatConverter.convert(Locale.ENGLISH, "yyyy-MM-dd");
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(wb.createDataFormat().getFormat(excelFormatter));

        for(int i=0; i<payList.size(); i++){
            Pay pay = payList.get(i);
            XSSFRow payRow = sheet.createRow(i+1);
            payRow.createCell(0).setCellValue(pay.getDate());
            payRow.getCell(0).setCellStyle(cellStyle);
            payRow.createCell(1).setCellValue(pay.getPaySum().doubleValue());
            payRow.createCell(2).setCellValue(pay.getPaySource());
            payRow.createCell(3).setCellValue(pay.getLotId());
            payRow.createCell(4).setCellValue(lotService.getLot(pay.getLotId()).getLotNum());
        }
        String fileName = "C:\\projectFiles\\Payments_"+start+"_"+end+".xlsx";
        OutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            wb.write(fileOut);
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    private String makeBidsSumReport(List<LotHistory> lotList, List<BidDetails> aggregatedLotList) throws IOException {
        InputStream ExcelFileToRead = new FileInputStream("C:\\projectFiles\\Temp1.xlsx");
        XSSFWorkbook xwb = new XSSFWorkbook(ExcelFileToRead);
        SXSSFWorkbook wb = new SXSSFWorkbook(xwb);
        SXSSFSheet sheet = wb.getSheetAt(0);

        //задаем форматы
        String excelFormatter = DateFormatConverter.convert(Locale.ENGLISH, "yyyy-MM-dd");
        CellStyle dateStyle = wb.createCellStyle();
        CellStyle numStyle = wb.createCellStyle();

        DataFormat poiFormat = wb.createDataFormat();
        dateStyle.setDataFormat(poiFormat.getFormat(excelFormatter));

        numStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("$#,##0.00"));
        //end

        //Заполнение листа 1 с лотами
        SXSSFRow headRow = sheet.createRow(0);
        headRow.createCell(0).setCellValue("ID_Торгів");
        headRow.createCell(1).setCellValue("Біржа_дата");
        headRow.createCell(2).setCellValue("Біржа");
        headRow.createCell(3).setCellValue("Дата");
        headRow.createCell(4).setCellValue("ID_Лоту");
        headRow.createCell(5).setCellValue("Сума, грн.");

        int rowNum=0;

            for (LotHistory lot : lotList) {
                rowNum++;
                SXSSFRow row = sheet.createRow(rowNum);
                row.createCell(0).setCellValue(lot.getBidId());
                Bid bid = bidService.getBid(lot.getBidId());
                try{
                    row.createCell(1).setCellValue(bid.getExchange().getCompanyName()+"_"+sdfshort.format(bid.getBidDate()));
                }
                catch (NullPointerException e){
                }
                try {
                    row.createCell(2).setCellValue(bid.getExchange().getCompanyName());
                } catch (NullPointerException e) {
                }
                try {
                    row.createCell(3).setCellValue(bid.getBidDate());
                    row.getCell(3).setCellStyle(dateStyle);
                } catch (NullPointerException e) {
                }
                row.createCell(4).setCellValue(lot.getId());
                try {
                    row.createCell(5).setCellValue(lot.getStartPrice().doubleValue());
                } catch (NullPointerException e) {
                }
            }

        SXSSFSheet sheetST = wb.getSheetAt(1);
        //Заполнение листа 2 сумами по торгам
        SXSSFRow headRow2 = sheetST.createRow(0);
        headRow2.createCell(0).setCellValue("ID_Торгів");
        headRow2.createCell(1).setCellValue("Біржа_дата");
        headRow2.createCell(2).setCellValue("Біржа");
        headRow2.createCell(3).setCellValue("Дата");
        headRow2.createCell(4).setCellValue("Сума, грн.");

        int rowN=0;

        for (BidDetails aggregatedLot : aggregatedLotList) {
            rowN++;
            SXSSFRow row = sheetST.createRow(rowN);
            row.createCell(0).setCellValue(aggregatedLot.getBidId());
            Bid bid = bidService.getBid(aggregatedLot.getBidId());
            try{
                row.createCell(1).setCellValue(bid.getExchange().getCompanyName()+"_"+sdfshort.format(bid.getBidDate()));
            }
            catch (NullPointerException e){
            }
            try {
                row.createCell(2).setCellValue(bid.getExchange().getCompanyName());
            } catch (NullPointerException e) {
            }
            try {
                row.createCell(3).setCellValue(bid.getBidDate());
                row.getCell(3).setCellStyle(dateStyle);
            } catch (NullPointerException e) {
            }
            try {
                row.createCell(4).setCellValue(aggregatedLot.getStartPrice().doubleValue());
            } catch (NullPointerException e) {
            }
        }

        String fileName = "C:\\projectFiles\\" + ("Bids_report.xlsx");
        OutputStream fileOut = new FileOutputStream(fileName);

        wb.write(fileOut);
        fileOut.flush();
        fileOut.close();
        return fileName;
    }

    private String makeHistoryReportByAssets(List <Asset> assetList) throws IOException {
        InputStream ExcelFileToRead = new FileInputStream("C:\\projectFiles\\History.xlsx");
        XSSFWorkbook wb = new XSSFWorkbook(ExcelFileToRead);
        XSSFSheet sheet1 = wb.getSheetAt(0);
        XSSFSheet sheet2 = wb.getSheetAt(1);

        //задаем формат даты
        String excelFormatter = DateFormatConverter.convert(Locale.ENGLISH, "yyyy-MM-dd");
        CellStyle cellStyle = wb.createCellStyle();
        CellStyle numStyle = wb.createCellStyle();

        DataFormat poiFormat = wb.createDataFormat();
        cellStyle.setDataFormat(poiFormat.getFormat(excelFormatter));

        numStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("$#,##0.00"));
        //end

        int numRow1 = 0;
        int numRow2 = 0;
        // int i = 0;
        for(Asset asset : assetList){

            List<Long> lotIdList = assetService.getLotIdHistoryByAsset(asset.getId());
            for(Long lotId: lotIdList){

                List<Bid> bidList = lotService.getLotHistoryAggregatedByBid(lotId);
                Collections.sort(bidList);
                for(Bid bid: bidList){
                    numRow1++;
                    XSSFRow row = sheet1.createRow(numRow1);
                    row.createCell(0).setCellValue(asset.getInn());
                    row.createCell(1).setCellValue(lotId);
                    row.createCell(2).setCellValue(bid.getExchange().getCompanyName());
                    row.createCell(3).setCellValue(sdfshort.format(bid.getBidDate()));
                    try {
                        row.createCell(4).setCellValue(assetService.getAccPriceByLotIdHistory(asset.getId(), lotId).doubleValue());
                    }
                    catch(NullPointerException e){
                    }

                }
            }
            List<AcceptPriceHistory> acceptPriceHistoryList = assetService.getDateAndAccPriceHistoryByAsset(asset.getId());
            for(AcceptPriceHistory acceptPriceHistory: acceptPriceHistoryList){
                numRow2++;
                XSSFRow row2 = sheet2.createRow(numRow2);
                row2.createCell(0).setCellValue(asset.getInn());
                row2.createCell(1).setCellValue(sdfshort.format(acceptPriceHistory.getDate()));
                row2.createCell(2).setCellValue(acceptPriceHistory.getAcceptedPrice().doubleValue());
            }
        }

        String fileName = "C:\\projectFiles\\" + ("History " +sdfshort.format(new Date())+ ".xlsx");
        OutputStream fileOut = new FileOutputStream(fileName);

        wb.write(fileOut);
        fileOut.close();
        return fileName;
    }

    @RequestMapping(value = "/lotList", method = RequestMethod.GET)
    private @ResponseBody
    List<Lot> getLots() {
        return lotService.getLots();
    }

    @RequestMapping(value = "/exchanges", method = RequestMethod.GET)
    private @ResponseBody
    List<Exchange> jsonGetExchanges() {
        return exchangeService.getAllExchanges();
    }

 /*   @RequestMapping(value = "/setRlot", method = RequestMethod.GET)
    private @ResponseBody
    String toLotRedactor(@RequestParam("lotID") String lotId, Model model) {
        model.addAttribute("lotRid", lotId);
        return "1";
    }*/

    @RequestMapping(value = "/lotDel", method = RequestMethod.POST)
    private @ResponseBody
    int deleteLot(@RequestParam("lotId") String lotId) {

        boolean isitDel = lotService.delLot(Long.parseLong(lotId));
        if (isitDel)
            return 1;
        else
            return 0;
    }

    @RequestMapping(value = "/setLotSold", method = RequestMethod.POST)
    private @ResponseBody
    String setLotSold(HttpSession session, @RequestParam("lotID") String lotId) {
        String login = (String) session.getAttribute("userId");
        Lot lot = lotService.getLot(Long.parseLong(lotId));
        lot.setItSold(true);
        boolean isitUpdated = lotService.updateLot(login, lot);
        if (isitUpdated)
            return "1";
        else
            return "0";
    }

    @RequestMapping(value = "/statusChanger", method = RequestMethod.POST)
    private @ResponseBody
    String changeStatus
            (HttpSession session, @RequestParam("lotID") String lotId, @RequestParam("status") String status) {
        String login = (String) session.getAttribute("userId");
        Lot lot = lotService.getLot(Long.parseLong(lotId));
        lot.setWorkStage(status);
        lotService.updateLot(login, lot);
        return "1";
    }

    @RequestMapping(value = "/regions", method = RequestMethod.POST)
    private @ResponseBody
    List<String> getAllRegs() {
        List<String> regList;
        regList = creditService.getRegions();
        return regList;
    }

    @RequestMapping(value = "/crType", method = RequestMethod.POST)
    private @ResponseBody
    List<String> getAllTypes() {
        List<String> typesList;
        typesList = creditService.getTypes();
        return typesList;
    }

    @RequestMapping(value = "/getCurs", method = RequestMethod.POST)
    private @ResponseBody
    List<String> getAllCurr() {
        List<String> currList;
        currList = creditService.getCurrencys();
        return currList;
    }

    @RequestMapping(value = "/countSumByLot", method = RequestMethod.POST)
    private @ResponseBody
    List<String> countSumByLot(@RequestParam("lotId") Long idLot) {
        List<String> countSumList = new ArrayList<>();
        Lot lot = lotService.getLot(idLot);
        Long count = lotService.lotCount(lot);
        BigDecimal sum = lotService.lotSum(lot);
        if(count!=null)
        countSumList.add(count.toString());
        if(sum!=null)
        countSumList.add(sum.toString());
        return countSumList;
    }

    @RequestMapping(value = "/paymentsSumByLot", method = RequestMethod.POST)
    private @ResponseBody
    BigDecimal paymentsSum(@RequestParam("lotId") Long idLot) {
        return lotService.paymentsSumByLot(lotService.getLot(idLot));
    }

    @RequestMapping(value = "/paymentsByLot", method = RequestMethod.POST)
    private @ResponseBody
    List<Pay> paymentsByLot(@RequestParam("lotId") Long idLot) {
        Lot lot = lotService.getLot(idLot);
        return lotService.paymentsByLot(lot);
    }

    @RequestMapping(value = "/addPayToLot", method = RequestMethod.POST)
    private @ResponseBody
    String addPayToLot(HttpSession session,
                       @RequestParam("lotId") Long idLot,
                       @RequestParam("payDate") String payDate,
                       @RequestParam("pay") BigDecimal pay,
                       @RequestParam("paySource") String paySource) {
        String login = (String) session.getAttribute("userId");
        Date date;
        try {
            date = sdfshort.parse(payDate);
        } catch (ParseException e) {
            return "0";
        }
        Lot lot = lotService.getLot(idLot);
        if(lot.getLotType()==1) {
            List<Asset> assetsByLot = lotService.getAssetsByLot(idLot);
            BigDecimal lotFactPrice = lot.getFactPrice();

            BigDecimal assetsTotalPays = new BigDecimal(0.00);

            for (int i = 0; i < assetsByLot.size(); i++) {

                Asset asset = assetsByLot.get(i);

                BigDecimal coeff = getCoefficient(asset.getFactPrice(), lotFactPrice);
                // asset.getFactPrice().divide(lotFactPrice, 10, BigDecimal.ROUND_HALF_UP);

                BigDecimal payByAsset = pay.multiply(coeff).setScale(2, BigDecimal.ROUND_HALF_UP);

                if(paySource.equals("Біржа")) {
                    asset.setPaysBid(asset.getPaysBid().add(payByAsset));
                    asset.setBidPayDate(date);
                }
                else{
                    asset.setPaysCustomer(asset.getPaysCustomer().add(payByAsset));
                    asset.setCustomerPayDate(date);
                }
                assetsTotalPays = assetsTotalPays.add(payByAsset);

                if (i == assetsByLot.size() - 1 & paySource.equals("Біржа")) {
                    asset.setPaysBid(asset.getPaysBid().add(pay.subtract(assetsTotalPays)));
                }
                if (i == assetsByLot.size() - 1 & paySource.equals("Покупець")) {
                    asset.setPaysCustomer(asset.getPaysCustomer().add(pay.subtract(assetsTotalPays)));
                }
                assetService.updateAsset(login, asset);
            }
            Pay payment = new Pay(lot, date, pay, paySource);
            if (payService.createPay(payment) > 0L) return "1";
            else return "0";
        }
        if(lot.getLotType()==0) {
            List<Credit> creditsByLot = lotService.getCRDTSByLot(lot);
            BigDecimal lotFactPrice = lot.getFactPrice();

            BigDecimal assetsTotalPays = new BigDecimal(0.00);

            for (int i = 0; i < creditsByLot.size(); i++) {

                Credit credit = creditsByLot.get(i);

                BigDecimal coeff = getCoefficient(credit.getFactPrice(), lotFactPrice);
                // asset.getFactPrice().divide(lotFactPrice, 10, BigDecimal.ROUND_HALF_UP);

                BigDecimal payByAsset = pay.multiply(coeff).setScale(2, BigDecimal.ROUND_HALF_UP);

                if(paySource.equals("Біржа")) {
                    credit.setPaysBid(credit.getPaysBid().add(payByAsset));
                    credit.setBidPayDate(date);
                }
                else{
                    credit.setPaysCustomer(credit.getPaysCustomer().add(payByAsset));
                    credit.setCustomerPayDate(date);
                }
                assetsTotalPays = assetsTotalPays.add(payByAsset);
                if (i == creditsByLot.size() - 1 & paySource.equals("Біржа")) {
                    credit.setPaysBid(credit.getPaysBid().add(pay.subtract(assetsTotalPays)));
                }
                if (i == creditsByLot.size() - 1 & paySource.equals("Покупець")) {
                    credit.setPaysCustomer(credit.getPaysCustomer().add(pay.subtract(assetsTotalPays)));
                }
                creditService.updateCredit(login, credit);
            }
            Pay payment = new Pay(lot, date, pay, paySource);
            if (payService.createPay(payment) > 0L) return "1";
            else return "0";
        }
        else return "1";
    }

    @RequestMapping(value = "/delPay", method = RequestMethod.GET)
    private @ResponseBody
    String delPay(HttpSession session, @RequestParam("payId") Long payId){
        String login = (String) session.getAttribute("userId");
        Pay pay = payService.getPay(payId);
        Lot lot = lotService.getLot(pay.getLotId());
        if (lot.getLotType() == 1) {
            List<Asset> assetsByLot = lotService.getAssetsByLot(pay.getLotId());
            BigDecimal lotFactPrice = lot.getFactPrice();

            BigDecimal totalToMinus = pay.getPaySum();
            BigDecimal currentMinus = new BigDecimal(0.00);

            for (int i = 0; i < assetsByLot.size(); i++) {

                Asset asset = assetsByLot.get(i);

                BigDecimal coeff = getCoefficient(asset.getFactPrice(), lotFactPrice);

                BigDecimal minusByAsset;

                if (i == assetsByLot.size() - 1) {
                    minusByAsset = totalToMinus.subtract(currentMinus);
                } else
                    minusByAsset = totalToMinus.multiply(coeff).setScale(2, BigDecimal.ROUND_HALF_UP);

                if (pay.getPaySource().equals("Біржа")) {
                    asset.setPaysBid(asset.getPaysBid().subtract(minusByAsset));
                    //    asset.setBidPayDate(date); //С этого места
                } else {
                    asset.setPaysCustomer(asset.getPaysCustomer().subtract(minusByAsset));
                    //    asset.setCustomerPayDate(date);
                }
                currentMinus = currentMinus.add(minusByAsset);
                assetService.updateAsset(login, asset);
            }
            pay.setHistoryLotId(pay.getLotId());
            pay.setLotId(null);
            if (payService.updatePay(pay))
                return "1";
            else return "0";
        }
        else if (lot.getLotType() == 0) {
            List<Credit> creditsByLot = lotService.getCRDTSByLot(lot);
            BigDecimal lotFactPrice = lot.getFactPrice();

            BigDecimal totalToMinus = pay.getPaySum();
            BigDecimal currentMinus = new BigDecimal(0.00);

            for (int i = 0; i < creditsByLot.size(); i++) {

                Credit credit = creditsByLot.get(i);

                BigDecimal coeff = getCoefficient(credit.getFactPrice(), lotFactPrice);

                BigDecimal minusByAsset;

                if (i == creditsByLot.size() - 1) {
                    minusByAsset = totalToMinus.subtract(currentMinus);
                } else
                    minusByAsset = totalToMinus.multiply(coeff).setScale(2, BigDecimal.ROUND_HALF_UP);

                if (pay.getPaySource().equals("Біржа")) {
                    credit.setPaysBid(credit.getPaysBid().subtract(minusByAsset));
                    //    asset.setBidPayDate(date); //С этого места
                } else {
                    credit.setPaysCustomer(credit.getPaysCustomer().subtract(minusByAsset));
                    //    asset.setCustomerPayDate(date);
                }
                currentMinus = currentMinus.add(minusByAsset);
                creditService.updateCredit(login, credit);
            }

            pay.setHistoryLotId(pay.getLotId());
            pay.setLotId(null);
            if (payService.updatePay(pay))
                return "1";
            else return "0";
        }
        else
        return "0";
    }

    @RequestMapping(value = "/setLotToPrint", method = RequestMethod.GET)
    private @ResponseBody
    String setLotsToPrint(@RequestParam("objId") String lotId,
                          Model model) {
        model.addAttribute("objIdToDownload", lotId);
        return "1";
    }

    @RequestMapping(value = "/setDocToDownload", method = RequestMethod.GET)
    private @ResponseBody
    String setDocToDownload(@RequestParam("objType") String objType,
                            @RequestParam("objId") String objId,
                            @RequestParam("docName") String docName,
                            Model model) {
        model.addAttribute("objIdToDownload", objId);
        model.addAttribute("docName", docName);
        model.addAttribute("docType", objType);
        return "1";
    }

    @RequestMapping(value = "/setReportPath", method = RequestMethod.GET)
    private @ResponseBody
    String setReportPath(@RequestParam("reportNum") int reportNum,
                         @RequestParam("startDate") String start,
                         @RequestParam("endDate") String end,
                         Model model) {
        String reportPath = "";

        if(reportNum==4){
            try {
                reportPath=fillAssTab();
            } catch (IOException e) {
                e.printStackTrace();
            }
            model.addAttribute("reportPath", reportPath);
            return "1";
        }

        if(reportNum==3){
            try {
                reportPath=fillCrdTab(creditService.getCreditsByPortion(1));
            } catch (IOException e) {
                e.printStackTrace();
            }
            model.addAttribute("reportPath", reportPath);
            return "1";
        }

        Date startDate = null;
        Date endDate = null;
        try {
            startDate = sdfshort.parse(start);
        } catch (ParseException e) {
            System.out.println("Невірний формат дати або дату не введено");
        }
        try {
            endDate = sdfshort.parse(end);
        } catch (ParseException e) {
            System.out.println("Невірний формат дати або дату не введено");
        }
        List<Asset> assetList = assetService.findAllSuccessBids(startDate, endDate);
        List<Credit> crList = creditService.getCredits_SuccessBids(startDate, endDate);

        if (reportNum==1) {
            try {
                reportPath = makeDodatok(assetList, crList, start, end);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (reportNum==2) {
            reportPath = "C:\\projectFiles\\Dodatok 2_14.xls";
        }
        if (reportNum==5) {
                reportPath = makePaymentsReport(payService.getPaysByDates(startDate, endDate), start, end);
        }
        if (reportNum==6) {
            try {
                reportPath = makeBidsSumReport(lotService.getLotsHistoryByBidDates(startDate, endDate), lotService.getLotsHistoryAggregatedByBid(startDate, endDate));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        model.addAttribute("reportPath", reportPath);
        return "1";
    }

    @RequestMapping(value = "/getReport/{reportNum}/{start}/{end}", method = RequestMethod.GET)
    public void getReport(HttpServletResponse response, @PathVariable String start, @PathVariable String end,
                         @PathVariable int reportNum) throws IOException {
        String reportPath = "";

        if(reportNum==4){
            try {
                reportPath=fillAssTab();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(reportNum==3){
            try {
                reportPath=fillCrdTab(creditService.getCreditsByPortion(1));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = sdfshort.parse(start);
        } catch (ParseException e) {
        }
        try {
            endDate = sdfshort.parse(end);
        } catch (ParseException e) {
        }
        List<Asset> assetList = assetService.findAllSuccessBids(startDate, endDate);
        List<Credit> crList = creditService.getCredits_SuccessBids(startDate, endDate);

        if (reportNum==1) {
            try {
                reportPath = makeDodatok(assetList, crList, start, end);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (reportNum==2) {
            reportPath = "C:\\projectFiles\\Dodatok 2_14.xls";
        }
        if (reportNum==5) {
            reportPath = makePaymentsReport(payService.getPaysByDates(startDate, endDate), start, end);
        }
        if (reportNum==6) {
            try {
                reportPath = makeBidsSumReport(lotService.getLotsHistoryByBidDates(startDate, endDate), lotService.getLotsHistoryAggregatedByBid(startDate, endDate));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File file = new File(reportPath);
        InputStream is = new FileInputStream(file);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

        OutputStream os = response.getOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        os.flush();
        is.close();
        os.close();
        file.delete();
    }

    @RequestMapping(value = "/getFileNames", method = RequestMethod.POST)
    private @ResponseBody
    List<String> getFileNames(@RequestParam("objId") String objId,
                              @RequestParam("objType") String objType) {

        List<String> fileList = new ArrayList<>();
        File[] fList;
        File F = null;

        if (objType.equals("lot"))
            F = new File(documentsPath + objId);
        if (objType.equals("bid"))
            F = new File(bidDocumentsPath + objId);
        try {
        fList = F.listFiles();
            for (File aFList : fList) {
                if (aFList.isFile())
                    fileList.add(aFList.getName());
            }
        } catch (NullPointerException e) {
            return fileList;
        }
        return fileList;
    }

    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    private @ResponseBody
    String uploadLotFile(@RequestParam("file") MultipartFile file,
                         @RequestParam("objId") Long objId,
                         @RequestParam("objType") String objType,
                         HttpServletResponse response,
                         HttpServletRequest request) {
        response.setCharacterEncoding("UTF-8");
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        String name = null;
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();

                name = file.getOriginalFilename();

                String rootPath = null;
                if (objType.equals("lot"))
                    rootPath = documentsPath;
                if (objType.equals("bid"))
                    rootPath = bidDocumentsPath;

                File dir = new File(rootPath + File.separator + objId);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File uploadedFile = new File(dir.getAbsolutePath() + File.separator + name);

                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(uploadedFile));
                stream.write(bytes);
                stream.flush();
                stream.close();

                return "File " + name + " zavantajeno";

            } catch (Exception e) {
                return "Download error " + name + " => " + e.getMessage();
            }
        } else {
            return "Error. File not choosen.";
        }
    }

    @RequestMapping(value = "/uploadIdFileForHistory", method = RequestMethod.POST)
    private @ResponseBody
    String uploadIdFileForHistory(@RequestParam("file") MultipartFile multipartFile, @RequestParam("idType") int idType, Model model) throws IOException {

            List<Asset> assetList = new ArrayList<>();
            if (!multipartFile.isEmpty()) {
                File file = getTempFile(multipartFile);
                XSSFWorkbook wb;

                try {
                    wb = new XSSFWorkbook(file);
                    XSSFSheet sheet = wb.getSheetAt(0);
                    Iterator rows = sheet.rowIterator();
                    while (rows.hasNext()) {
                        XSSFRow row = (XSSFRow) rows.next();
                        String inn = row.getCell(0).getStringCellValue();
                        assetList.addAll(assetService.getAllAssetsByInNum(inn));
                    }

                } catch (Exception e) {
                    return "4";
                }
                String reportPath = makeHistoryReportByAssets(assetList);
                model.addAttribute("reportPath", reportPath);

                return "1";
            }
            else return "0";
    }

    @RequestMapping(value = "/uploadIdFile", method = RequestMethod.POST)
    private @ResponseBody
    List uploadIdFile(@RequestParam("file") MultipartFile multipartFile, @RequestParam("idType") int idType) throws IOException {

        File file = getTempFile(multipartFile);
        if (idType == 1) {
            List<Asset> assetList = new ArrayList<>();

            if (!multipartFile.isEmpty()) {

                XSSFWorkbook wb;

                try {
                    wb = new XSSFWorkbook(file);
                    XSSFSheet sheet = wb.getSheetAt(0);
                    Iterator rows = sheet.rowIterator();
                    while (rows.hasNext()) {
                        XSSFRow row = (XSSFRow) rows.next();
                        String inn = row.getCell(0).getStringCellValue();
                        assetList.addAll(assetService.getAllAssetsByInNum(inn));
                    }
                    return assetList;
                } catch (Exception e) {
                    return null;
                }
            } else return null;
        }
        if (idType == 0) {
            List<Credit> creditList = new ArrayList<>();
            if (!multipartFile.isEmpty()) {

                XSSFWorkbook wb = null;

                try {
                    wb = new XSSFWorkbook(file);
                } catch (InvalidFormatException e) {
                    System.out.println("invalid Format");
                }
                XSSFSheet sheet = wb.getSheetAt(0);

                Iterator rows = sheet.rowIterator();
                while (rows.hasNext()) {
                    XSSFRow row = (XSSFRow) rows.next();
                    Double idBars = row.getCell(0).getNumericCellValue();
                    creditList.addAll(creditService.getCreditsByIdBars(idBars.longValue()));

                }
                return creditList;
            } else
                return null;
        } else
            return null;
    }

    @RequestMapping(value = "/setAccPriceByFile", method = RequestMethod.POST)
    private @ResponseBody
    String setAccPriceByFile(HttpSession session,
                             @RequestParam("file") MultipartFile multipartFile,
                             @RequestParam("idType") int idType) throws IOException {
        String login = (String) session.getAttribute("userId");
        List<Asset> assetList;
        File file = getTempFile(multipartFile);
        if (idType == 1) {

            if (!multipartFile.isEmpty()) {

                XSSFWorkbook wb;

                try {
                    wb = new XSSFWorkbook(file);
                    XSSFSheet sheet = wb.getSheetAt(0);
                    Iterator rows = sheet.rowIterator();
                    while (rows.hasNext()) {
                        XSSFRow row = (XSSFRow) rows.next();
                        String inn = row.getCell(0).getStringCellValue();
                        Double accPrice = row.getCell(1).getNumericCellValue();
                        assetList=assetService.getAllAssetsByInNum(inn);
                        assetList.forEach(asset -> asset.setAcceptPrice(BigDecimal.valueOf(accPrice)) );
                        assetList.forEach(asset -> assetService.updateAsset(login, asset) );
                    }
                    return "1";
                } catch (Exception e) {
                    return "0";
                }
            } else return "0";
        }
        if (idType == 0) {
            if (!multipartFile.isEmpty()) {

                XSSFWorkbook wb;

                try {
                    wb = new XSSFWorkbook(file);
                    XSSFSheet sheet = wb.getSheetAt(0);
                    Iterator rows = sheet.rowIterator();
                    while (rows.hasNext()) {
                        XSSFRow row = (XSSFRow) rows.next();
                        String inn = row.getCell(0).getStringCellValue();
                        Double accPrice = row.getCell(1).getNumericCellValue();
                       List<Credit> creditList = creditService.getCreditsByIdBars(Long.parseLong(inn));
                        creditList.forEach(credit -> credit.setAcceptPrice(BigDecimal.valueOf(accPrice)) );
                        creditList.forEach(credit -> creditService.updateCredit(login, credit));
                    }
                    return "1";
                } catch (Exception e) {
                    return "0";
                }
            } else return "0";
        }

        else
            return "0";
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void download(HttpServletResponse response, HttpSession session) throws IOException {
        String objIdToDownload = (String) session.getAttribute("objIdToDownload");
        List<Lot> lotList = new ArrayList<>();
        List<Asset> assetList = new ArrayList<>();

        String[] idMass = objIdToDownload.split(",");
        for (String id : idMass) {
            Lot lot = lotService.getLot(Long.parseLong(id));
            lotList.add(lot);
            assetList.addAll(lotService.getAssetsByLot(lot));
        }
        String filePath = Excel.loadCreditsByList(lotList, assetList);

        File file = new File(filePath);
        InputStream is = new FileInputStream(file);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

        OutputStream os = response.getOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        os.flush();
        is.close();
        os.close();
        file.delete();
    }

    @RequestMapping(value = "/downloadT", method = RequestMethod.GET)
    public void downloadT(HttpServletResponse response, HttpSession session) throws IOException {

        String objIdToDownload = (String) session.getAttribute("objIdToDownload");

        Bid bid = bidService.getBid(Long.parseLong(objIdToDownload));

        List<Lot> lotList ;
        List<Asset> assetList ;

        lotList = lotService.getLotsByBid(bid);
        assetList =bidService.getAssetsByBid(bid);

        String filePath = Excel.loadCreditsByList(lotList, assetList);

        File file = new File(filePath);
        InputStream is = new FileInputStream(file);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

        OutputStream os = response.getOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        os.flush();
        is.close();
        os.close();
        file.delete();
    }

    @RequestMapping(value = "/reportDownload", method = RequestMethod.GET)
    public void reportDownload(HttpServletResponse response, HttpSession session) throws IOException {
        String reportPath = (String) session.getAttribute("reportPath");
        File file = new File(reportPath);
        InputStream is = new FileInputStream(file);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

        OutputStream os = response.getOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        os.flush();
        is.close();
        os.close();
        file.delete();
    }

    @RequestMapping(value = "/downloadDocument", method = RequestMethod.GET)
    public void downloadDocument(HttpServletResponse response, HttpSession session) throws IOException {
        String objId = (String) session.getAttribute("objIdToDownload");
        String docName = (String) session.getAttribute("docName");
        String docType = (String) session.getAttribute("docType");
        File file = null;

        if (docType.equals("lot"))
            file = new File(documentsPath + objId + File.separator + docName);
        if (docType.equals("bid"))
            file = new File(bidDocumentsPath + objId + File.separator + docName);

        InputStream is = new FileInputStream(file);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

        OutputStream os = response.getOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        os.flush();
        is.close();
        os.close();
    }

    @RequestMapping(value = "/downloadOgolosh", method = RequestMethod.GET)
    public void downloadOgoloshennya(HttpServletResponse response, HttpSession session) throws IOException {
        String objId = (String) session.getAttribute("objIdToDownload");

        File file ;
        String docName = makeOgoloshennya(Long.parseLong(objId));
        file = new File(docName);

        InputStream is = new FileInputStream(file);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

        OutputStream os = response.getOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        os.flush();
        is.close();
        os.close();
        file.delete();
    }

    @RequestMapping(value = "/downLotIdListForm", method = RequestMethod.GET)
    public void downLotIdListForm(HttpServletResponse response) throws IOException {

        File file = new File("C:\\projectFiles\\LOT_ID_LIST.xlsx");

        InputStream is = new FileInputStream(file);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

        OutputStream os = response.getOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        os.flush();
        is.close();
        os.close();
    }
    //LOT_ID_LIST.xlsx

    public void setFactPriceFromLotToCredits(Lot lot, BigDecimal factLotPrice, String login){
        List <Credit> credits = lotService.getCRDTSByLot(lot);
        if(lot.getFactPrice() != null && lot.getFactPrice().equals(factLotPrice)){
           return;
        }
        if (factLotPrice == null) {
            for (Credit credit : credits) {
                credit.setFactPrice(null);
                creditService.updateCredit(login, credit);
            }
        }
        else if (!factLotPrice.equals(BigDecimal.valueOf(0.00))) {
            BigDecimal lotAcceptedSum = lotService.lotAcceptedSum(lot);
            BigDecimal creditsTotalFact = new BigDecimal(0.00);

            for (int i = 0; i < credits.size(); i++) {
                Credit credit = credits.get(i);
                BigDecimal factPrice;
                if (i == credits.size() - 1) {
                    factPrice = factLotPrice.subtract(creditsTotalFact);
                } else {
                    factPrice = (credit.getAcceptPrice().divide(lotAcceptedSum, 10, BigDecimal.ROUND_HALF_UP)).multiply(factLotPrice).setScale(2, BigDecimal.ROUND_HALF_UP);
                    creditsTotalFact = creditsTotalFact.add(factPrice);
                }
                credit.setFactPrice(factPrice);
                creditService.updateCredit(login, credit);
            }
        }
    }

    public void setFactPriceFromLotToAssets(Lot lot, BigDecimal factLotPrice, String login){
        List <Asset> assets = lotService.getAssetsByLot(lot);

        if (factLotPrice == null) {
            for (Asset asset : assets) {
                asset.setFactPrice(null);
                assetService.updateAsset(login, asset);
            }
        }
        else if (!factLotPrice.equals(new BigDecimal(0.00))) {
            BigDecimal lotAcceptedSum = lotService.lotAcceptedSum(lot);
            BigDecimal assetsTotalFact = new BigDecimal(0.00);

            for (int i = 0; i < assets.size(); i++) {
                Asset asset = assets.get(i);
                BigDecimal factPrice;
                if (i == assets.size() - 1) {
                    factPrice = factLotPrice.subtract(assetsTotalFact);
                } else {
                    factPrice = (asset.getAcceptPrice().divide(lotAcceptedSum, 10, BigDecimal.ROUND_HALF_UP)).multiply(factLotPrice).setScale(2, BigDecimal.ROUND_HALF_UP);
                    assetsTotalFact = assetsTotalFact.add(factPrice);
                }
                asset.setFactPrice(factPrice);
                assetService.updateAsset(login, asset);
            }
        }
    }

    public void setFirstStartPriceFromLotToCredits(Lot lot, BigDecimal firstStartLotPrice, String login){
        List <Credit> credits = lotService.getCRDTSByLot(lot);
        if(firstStartLotPrice!=null && lot.getFirstStartPrice()!=null && lot.getFirstStartPrice().equals(firstStartLotPrice)){
            return;
        }
        if(firstStartLotPrice == null || firstStartLotPrice.equals(new BigDecimal(0.00))) {
            for (Credit credit : credits) {
                credit.setFirstStartPrice(firstStartLotPrice);
                creditService.updateCredit(login, credit);
            }
        }

        else {
            BigDecimal lotSum = lotService.lotSum(lot);
            BigDecimal crTotalFirstPrice = new BigDecimal(0.00);
            for (int i = 0; i < credits.size(); i++) {
                Credit credit = credits.get(i);
                BigDecimal firstPrice;
                if (i == credits.size() - 1) {
                    firstPrice = firstStartLotPrice.subtract(crTotalFirstPrice);
                }
                else {
                    firstPrice = (credit.getRv().divide(lotSum, 10, BigDecimal.ROUND_HALF_UP)).multiply(firstStartLotPrice).setScale(2, BigDecimal.ROUND_HALF_UP);
                    crTotalFirstPrice = crTotalFirstPrice.add(firstPrice);
                }
                credit.setFirstStartPrice(firstPrice);
                creditService.updateCredit(login, credit);
            }
        }
    }
    //можно улучшить
    public void setStartPriceFromLotToCredits(Lot lot, BigDecimal startLotPrice, String login){
        List <Credit> credits = lotService.getCRDTSByLot(lot);
        if(lot.getStartPrice()!=null && lot.getStartPrice().equals(startLotPrice)){
            return;
        }
        if (startLotPrice == null || startLotPrice.equals(new BigDecimal(0.00))) {
            for (Credit credit : credits) {
                credit.setStartPrice(startLotPrice);
                creditService.updateCredit(login, credit);
            }
        }
        else  {
            BigDecimal lotSum = lotService.lotSum(lot);
            BigDecimal crTotalStartPrice = new BigDecimal(0.00);
            for (int i = 0; i < credits.size(); i++) {
                Credit credit = credits.get(i);
                BigDecimal startPrice;
                if (i == credits.size() - 1) {
                    startPrice = startLotPrice.subtract(crTotalStartPrice);
                } else {
                    startPrice = (credit.getRv().divide(lotSum, 10, BigDecimal.ROUND_HALF_UP)).multiply(startLotPrice).setScale(2, BigDecimal.ROUND_HALF_UP);
                    crTotalStartPrice = crTotalStartPrice.add(startPrice);
                }
                credit.setStartPrice(startPrice);
                creditService.updateCredit(login, credit);
            }
        }
    }

    @RequestMapping(value = "/changeLotParams", method = RequestMethod.POST)
    private @ResponseBody String changeLotParams (HttpSession session,
                            @RequestParam("lotId") String lotId,
                            @RequestParam("lotNum") String lotNum,
                            @RequestParam("workStage") String status,
                            @RequestParam("comment") String comment,
                            @RequestParam("bidStage") String bidStage,
                            @RequestParam("resultStatus") String resultStatus,
                            @RequestParam("customer") String customer,
                            @RequestParam("customerInn") String customerInn,
                            @RequestParam("firstPrice") BigDecimal firstPrice,
                            @RequestParam("startPrice") BigDecimal startPrice,
                            @RequestParam("factPrice") BigDecimal factLotPrice,
                            @RequestParam("isSold") String isSold,
                            @RequestParam("selectedBidId") Long selectedBidId,
                            @RequestParam("countOfParticipants") int countOfParticipants,
                            @RequestParam("bidScenario") short bidScenario)
    {
        String login = (String) session.getAttribute("userId");
        Lot lot = lotService.getLot(Long.parseLong(lotId));
        lot.setLotNum(lotNum);
        lot.setWorkStage(status);
        lot.setComment(comment);
        lot.setBidStage(bidStage);
        lot.setStatus(resultStatus);
        lot.setCustomerName(customer);

        if (customerInn.equals(""))
            lot.setCustomerInn(0);
        else
        lot.setCustomerInn(Integer.parseInt(customerInn));

        lot.setCountOfParticipants(countOfParticipants);
        lot.setBidScenario(bidScenario);
        lot.setFirstStartPrice(firstPrice);
        if (selectedBidId == 0L) {
            lot.setBid(null);
        }
        else
            lot.setBid(bidService.getBid(selectedBidId));
        if(lot.getLotType()==1){
            setFactPriceFromLotToAssets(lot, factLotPrice, login);
        }
        if(lot.getLotType()==0) {
            setFirstStartPriceFromLotToCredits(lot, firstPrice, login);
            setStartPriceFromLotToCredits(lot, startPrice, login);
            setFactPriceFromLotToCredits(lot, factLotPrice, login);
        }
        lot.setStartPrice(startPrice);
        if(lot.getFirstStartPrice() == null) {
            setFirstStartPriceFromLotToCredits(lot, startPrice, login);
            lot.setFirstStartPrice(startPrice);
        }
        lot.setFactPrice(factLotPrice);

        if (isSold.equals("1")) {
            List<Credit> credits = lotService.getCRDTSByLot(lot);
            List<Asset> assetList = lotService.getAssetsByLot(lot);
            lot.setActSignedDate(new Date());
            lot.setItSold(true);
            for (Credit credit : credits) {
                credit.setSold(true);
                creditService.updateCredit(login, credit);
            }
            for(Asset asset: assetList){
                asset.setSold(true);
                assetService.updateAsset(login, asset);
            }
        }
        boolean isitChanged = lotService.updateLot(login, lot);
        if (isitChanged) return "1";
        else return "0";
    }

    @RequestMapping(value = "/reBidByLot", method = RequestMethod.GET)
    private @ResponseBody
    String reBidByLot(HttpSession session,
                      @RequestParam("lotId") String lotId,
                      @RequestParam("reqType") int requestType) {

        String login = (String) session.getAttribute("userId");
        Lot lot = lotService.getLot(Long.parseLong(lotId));

        if(lot.getLotType()==0) {
            if (lot.getFirstStartPrice() == null) {
                setFirstStartPriceFromLotToCredits(lot, lot.getStartPrice(), login);
            }
            setFactPriceFromLotToCredits(lot, null, login);
        }
        if(lot.getLotType()==1) {
            setFactPriceFromLotToAssets(lot, null, login);
        }
        if (lot.getFirstStartPrice() == null) {
            lot.setFirstStartPrice(lot.getStartPrice());
        }

        if (requestType == 1) {

            if (lot.getBidStage().equals(StaticStatus.bidStatusList.get(0))) {
                lot.setBidStage(StaticStatus.bidStatusList.get(1));
            } else if (lot.getBidStage().equals(StaticStatus.bidStatusList.get(1))) {
                lot.setBidStage(StaticStatus.bidStatusList.get(2));
            } else if (lot.getBidStage().equals(StaticStatus.bidStatusList.get(2))) {
                lot.setBidStage(StaticStatus.bidStatusList.get(3));
            } else if (lot.getBidStage().equals(StaticStatus.bidStatusList.get(3))) {
                lot.setBidStage(StaticStatus.bidStatusList.get(4));
            }
            else if (lot.getBidStage().equals(StaticStatus.bidStatusList.get(4))) {
                lot.setBidStage(StaticStatus.bidStatusList.get(5));
            }
            else if (lot.getBidStage().equals(StaticStatus.bidStatusList.get(5))) {
                lot.setBidStage(StaticStatus.bidStatusList.get(6));
            }
            else if (lot.getBidStage().equals(StaticStatus.bidStatusList.get(6))) {
                lot.setBidStage(StaticStatus.bidStatusList.get(7));
            }
        }
        else if(requestType==2){
            //lot.setFirstStartPrice(null);
            lot.setStartPrice(null);
            lot.setBidStage(StaticStatus.bidStatusList.get(0));
            lot.setNeedNewFondDec(true);
        }
        lot.setFactPrice(null);
        lot.setBid(null);
        //lot.setLotNum(null);
        lot.setCountOfParticipants(0);
        lot.setWorkStage("Новий лот");
        lot.setStatus(null);
        lot.setCustomerName(null);
        lotService.updateLot(login, lot);
        return "1";
    }

    @RequestMapping(value = "/changeBidParams", method = RequestMethod.POST)
    private @ResponseBody
    String changeBidParams(@RequestParam("bidId") String bidId,
                           @RequestParam("bidDate") String bidDate,
                           @RequestParam("exId") String exId,
                           @RequestParam("newNP") String newNP,
                           @RequestParam("newND1") String newND1,
                           //  @RequestParam("newND2") String coment,
                           @RequestParam("newRED") String newRED) {
        Date bDate = null, ND1 = null, ND2 = null, RED = null;
        Bid bid = bidService.getBid(Long.parseLong(bidId));
        Exchange exchange = exchangeService.getExchange(Long.parseLong(exId));
        try {
            bDate = sdfshort.parse(bidDate);
        } catch (ParseException e) {
            System.out.println("Неверный формат даты");
        }
        try {
            ND1 = sdfshort.parse(newND1);
        } catch (ParseException e) {
            System.out.println("Неверный формат даты");
        }
        try {
            RED = sdfshort.parse(newRED);
        } catch (ParseException e) {
            System.out.println("Неверный формат даты");
        }
        bid.setExchange(exchange);
        bid.setBidDate(bDate);
        bid.setNewspaper(newNP);
        bid.setNews1Date(ND1);
      //  bid.setComent(coment);
        bid.setRegistrEndDate(RED);
        bidService.updateBid(bid);
        return "1";
    }

    @RequestMapping(value = "/deleteBid", method = RequestMethod.POST)
    private @ResponseBody
    String deleteBid(HttpSession session, @RequestParam("idBid") String bidId) {
        String login = (String) session.getAttribute("userId");
        Bid bid = bidService.getBid(Long.parseLong(bidId));
        List<Lot> lotList = lotService.getLotsByBid(bid);
        for (Lot lot : lotList) {
            lot.setBid(null);
            lotService.updateLot(login, lot);
        }
        bidService.delete(bid);
        return "1";
    }

    @RequestMapping(value = "/setAssetPortionNum", method = RequestMethod.POST)
    private @ResponseBody
    String setAssetPortionNum(HttpSession session, @RequestParam("portion") String portion, Model model) {
        model.addAttribute("assetPortionNum", portion);
        return "1";
    }

    @RequestMapping(value = "/createLotByCheckedAssets", method = RequestMethod.POST)
    private @ResponseBody
    String createLotByAssets(@RequestParam("idList") String idList, HttpSession session) {
        if(idList.equals("")){
            return "0";
        }
        String[] idMass = idList.split(",");
            session.setAttribute("assetsListToLot", idMass);
            return "1";
    }

    @RequestMapping(value = "/createLotByCheckedCredits", method = RequestMethod.POST)
    private @ResponseBody
    String createLotByCheckedCredits(@RequestParam("idList") String idList, HttpSession session) {
        if(idList.equals("")){
            return "0";
        }
        String[] idMass = idList.split(",");

        session.setAttribute("creditsListToLot", idMass);
        return "1";
    }

    @RequestMapping(value = "/creditsByClient", method = RequestMethod.POST)
    private @ResponseBody
    List<Credit> getCreditsByEx(
            @RequestParam("inn") String inn,
            @RequestParam("idBars") Long idBars) {
        return creditService.getCreditsByClient(inn, idBars);
    }
    @RequestMapping(value = "/allCreditsByClient", method = RequestMethod.POST)
    private @ResponseBody
    List<Credit> getAllCreditsByClient(
            @RequestParam("inn") String inn,
            @RequestParam("idBars") Long idBars) {
        return creditService.getAllCreditsByClient(inn, idBars);
    }


    @RequestMapping(value = "/objectsByInNum", method = RequestMethod.POST)
    private @ResponseBody
    List<Asset> getAssetsByInNum(@RequestParam("inn") String inn) {
        return assetService.getAssetsByInNum(inn);
    }

    @RequestMapping(value = "/allObjectsByInNum", method = RequestMethod.POST)
    private @ResponseBody
    List<Asset> getAllAssetsByInNum(@RequestParam("inn") String inn) {
        return assetService.getAllAssetsByInNum(inn);
    }

    @RequestMapping(value = "/getLastAccPriceByInNum", method = RequestMethod.POST)
    private @ResponseBody
    BigDecimal getLastAccPriceByInNum(@RequestParam("id") Long id) {
        return assetService.getLastAccPrice(id);
    }

    @RequestMapping(value = "/sumById", method = RequestMethod.POST)
    private @ResponseBody
    String sumById(@RequestParam("idMass") String ids) {
        Formatter f = new Formatter();
        BigDecimal sum = new BigDecimal(0);
        String[] idm;
        try {
            idm = ids.substring(1).split(",");
        } catch (IndexOutOfBoundsException e) {
            return "0";
        }
        for (String id : idm) {
            sum = sum.multiply(creditService.getCredit(Long.parseLong(id)).getRv());
        }
        return f.format("%,.0f", sum).toString();
    }

    @RequestMapping(value = "/sumByInvs", method = RequestMethod.POST)
    private @ResponseBody
    String sumByInvs(@RequestParam("idMass") String ids) {
        BigDecimal sum = new BigDecimal(0.00);
        String[] idm;
        try {
            idm = ids.substring(1).split(",");
        } catch (IndexOutOfBoundsException e) {
            return "0";
        }
        for (String id : idm) {
            sum = sum.add(assetService.getAsset(Long.parseLong(id)).getRv());
        }
        return sum.toString();
    }

    @RequestMapping(value = "/sumByIDBars", method = RequestMethod.POST)
    private @ResponseBody
    String sumByIDBars (@RequestParam("idMass") String ids) {
        BigDecimal sum = new BigDecimal(0.00);
        String[] idm;
        try {
            idm = ids.substring(1).split(",");
        } catch (IndexOutOfBoundsException e) {
            return "0";
        }
        for (String id : idm) {
            sum = sum.add(creditService.getCredit(Long.parseLong(id)).getRv());
        }
        return sum.toString();
    }

    @RequestMapping(value = "/lotsByBid", method = RequestMethod.POST)
    private @ResponseBody
    List<Lot> lotsByBid(@RequestParam("bidId") String bidId) {
        Bid bid = bidService.getBid(Long.parseLong(bidId));
        return bidService.lotsByBid(bid);
    }

    @RequestMapping(value = "/comentsByLotsFromBid", method = RequestMethod.GET)
    private @ResponseBody
    List<String> getComments(@RequestParam("bidId") String bidId) {
        String aggregatedComment="";
        List<String> resList= new ArrayList<>();
        Bid bid = bidService.getBid(Long.parseLong(bidId));
        for(Lot l: (List<Lot>)bidService.lotsByBid(bid)){
            aggregatedComment+=l.getComment()+" ||";
        }
        resList.add(aggregatedComment);
        return resList;
    }

    @RequestMapping(value = "/getPaySum_Residual", method = RequestMethod.GET)
    private @ResponseBody
    List<BigDecimal> getPaySumResidual(@RequestParam("id") String id) {
        List<BigDecimal> list = new ArrayList<>();
        Asset asset = assetService.getAsset(Long.parseLong(id));
        if (asset.getLot() == null) {
            return list;
        } else {
            Lot lot = asset.getLot();
            BigDecimal coeff = getCoefficient(asset.getFactPrice(), lot.getFactPrice());// asset.getFactPrice().divide(lot.getFactPrice(), 10, BigDecimal.ROUND_HALF_UP);
            BigDecimal paySumByAsset ;
            try {
                paySumByAsset = lotService.paymentsSumByLot(lot).multiply(coeff).setScale(2, BigDecimal.ROUND_HALF_UP);
            }
            catch(NullPointerException npe){
                paySumByAsset = BigDecimal.valueOf(0);
            }
            BigDecimal residualToPay;
            try {
                residualToPay  = asset.getFactPrice().subtract(paySumByAsset);
            }
            catch (NullPointerException npe){
                residualToPay = null;
            }
            list.add(paySumByAsset);
            list.add(residualToPay);
            return list;
        }
    }

    @RequestMapping(value = "/getTotalCountOfObjects", method = RequestMethod.GET)
    private @ResponseBody
    Long getTotalCountOfObjects() {
        return assetService.getTotalCountOfAssets();
    }

    @RequestMapping(value = "/objectsByPortions", method = RequestMethod.POST)
    private @ResponseBody
    List<Asset> objectsByPortions(@RequestParam("num") String portionNumber) {
        return assetService.getAssetsByPortion(Integer.parseInt(portionNumber));
    }

    @RequestMapping(value = "/countCreditsByFilter", method = RequestMethod.POST)
    private @ResponseBody
    Long countCreditsByFilter(@RequestParam("isSold") int isSold,
                              @RequestParam("isInLot") int isInLot,
                              @RequestParam("clientType") int clientType,
                              @RequestParam("isNbu") int isNbu,
                              @RequestParam("isFondDec") int isFondDec,
                              @RequestParam("inIDBarses") String inIDBarses,
                              @RequestParam("inINNs") String inINNs,
                              @RequestParam("inIDLots") String inIDLots) {
        String [] idBarsMass = inIDBarses.split(",");
        String [] innMass = inINNs.split(",");
        String [] idLotMass = inIDLots.split(",");
        return creditService.countOfFilteredCredits(isSold, isInLot, clientType, isNbu, isFondDec, idBarsMass , innMass, idLotMass);
    }

    @RequestMapping(value = "/creditsByPortions", method = RequestMethod.POST)
    private @ResponseBody
    List<String> creditsByPortions(@RequestParam("num") int portionNumber,
                                   @RequestParam("isSold") int isSold,
                                   @RequestParam("isInLot") int isInLot,
                                   @RequestParam("clientType") int clientType,
                                   @RequestParam("isNbu") int isNbu,
                                   @RequestParam("isFondDec") int isFondDec,
                                   @RequestParam("inIDBarses") String inIDBarses,
                                   @RequestParam("inINNs") String inINNs,
                                   @RequestParam("inIDLots") String inIDLots
                                                         ) {
        String [] idBarsMass = inIDBarses.split(",");
        String [] innMass = inINNs.split(",");
        String [] idLotMass = inIDLots.split(",");

        List<Credit> crList = creditService.getCreditsByPortion(portionNumber, isSold, isInLot, clientType, isNbu, isFondDec, idBarsMass , innMass, idLotMass);
        List<String> rezList = new ArrayList<>();
        for (Credit cr : crList) {
            String lotId = "";

            String bidDate="";
            String exchangeName="";

            /*String nbuPledge = "Ні";
            if (cr.getNbuPladge())
                nbuPledge = "Так";*/
            String factPrice = "";
            if (cr.getFactPrice() != null)
                factPrice = String.valueOf(cr.getFactPrice());
            String bidStage = "";
            String bidResult = "";
            String payStatus = "";
            String paySum = "";
            String residualToPay = "";
            String customerName = "";
            String workStage = "";
            String fondDecisionDate = "";
            String fondDecision = "";
            String fondDecisionNumber = "";

            String nbuDecisionDate = "";
            String nbuDecision = "";
            String nbuDecisionNumber = "";

            String acceptedPrice="";
            String acceptedExchange="";

            if(cr.getAcceptPrice()!=null)
                acceptedPrice=String.valueOf(cr.getAcceptPrice());
            String actSignedDate = "";
            if (cr.getLot() != null) {
                lotId = String.valueOf(cr.getLot());
                Lot lot = lotService.getLot(cr.getLot());

                if(lot.getBid()!=null){
                    bidDate=String.valueOf(sdfpoints.format(lot.getBid().getBidDate()));
                    exchangeName=lot.getBid().getExchange().getCompanyName();
                }
                bidStage = lot.getBidStage();
                bidResult = lot.getStatus();
                if (lotService.paymentsSumByLot(lot) != null) {
                    BigDecimal paysSum = lotService.paymentsSumByLot(lot);
                    paySum = String.valueOf(paysSum);
                    if (lot.getFactPrice().compareTo(paysSum) < 0)
                        payStatus = "100% сплата";
                    else if (!paysSum.equals(new BigDecimal(0)))
                        payStatus = "Часткова оплата";
                    residualToPay = String.valueOf(lot.getFactPrice().subtract(paysSum));
                }
                customerName = lot.getCustomerName();
                workStage = lot.getWorkStage();
                if (lot.getFondDecisionDate() != null)
                    fondDecisionDate = String.valueOf(sdfpoints.format(lot.getFondDecisionDate()));
                fondDecision=lot.getFondDecision();
                fondDecisionNumber=lot.getDecisionNumber();
                if (lot.getNbuDecisionDate() != null)
                    nbuDecisionDate = String.valueOf(sdfpoints.format(lot.getNbuDecisionDate()));
                nbuDecision=lot.getNbuDecision();
                nbuDecisionNumber=lot.getNbuDecisionNumber();
                acceptedExchange=lot.getAcceptExchange();

                if (lot.getActSignedDate() != null)
                actSignedDate = sdfpoints.format(lot.getActSignedDate());
            }
            String planSaleDate="";
            if(cr.getPlanSaleDate()!=null)
                planSaleDate=yearMonthFormat.format(cr.getPlanSaleDate());

            rezList.add(lotId
                    + "||" + cr.getNd()
                    + "||" + cr.getInn()
                    + "||" + cr.getContractNum()
                    + "||" + bidDate
                    + "||" + exchangeName
                    + "||" + cr.getClientType()
                    + "||" + cr.getFio()
                    + "||" + cr.getProduct()
                    + "||" + cr.getNbuPladge()
                    + "||" + cr.getRegion()
                    + "||" + cr.getCurr()
                    + "||" + cr.getZb()
                    + "||" + cr.getDpd()
                    + "||" + cr.getRv()
                    + "||" + bidStage
                    + "||" + factPrice
                    + "||" + bidResult
                    + "||" + payStatus
                    + "||" + paySum
                    + "||" + residualToPay
                    + "||" + customerName
                    + "||" + workStage
                    + "||" + fondDecisionDate
                    + "||" + fondDecision
                    + "||" + fondDecisionNumber
                    + "||" + acceptedPrice
                    + "||" + acceptedExchange
                    + "||" + actSignedDate
                    + "||" + planSaleDate
                    + "||" + nbuDecisionDate
                    + "||" + nbuDecision
                    + "||" + nbuDecisionNumber
            );
        }
        return rezList;
    }

    @RequestMapping(value = "/countSumLotsByBid", method = RequestMethod.POST)
    private @ResponseBody
    List<String> countSumLotsByBid(@RequestParam("bidId") String bidId) {
        Long id = Long.parseLong(bidId);
        Bid bid = bidService.getBid(id);
        List<String> list = new ArrayList<>();
        Long count = bidService.countOfLots(bid);
        BigDecimal sum = bidService.sumByBid(bid);
        list.add(count.toString());
        list.add(sum.toString());
        return list;
    }

    @RequestMapping(value = "/countSumLotsByExchange", method = RequestMethod.POST)
    private @ResponseBody
    List<String> countSumLotsByExchange(@RequestParam("exId") Long exId) {
        Exchange exchange = exchangeService.getExchange(exId);
        List<Lot> lotsList = lotService.getLotsByExchange(exchange);
        List<String> list = new ArrayList<>();
        BigDecimal lotRV = new BigDecimal(0.00);
        for (Lot lot : lotsList) {
            lotRV = lotRV.add(lotService.lotSum(lot));
        }
        list.add(String.valueOf(lotsList.size()));
        list.add(String.valueOf(lotRV));
        return list;
    }

    @RequestMapping(value = "/countBidsByExchange", method = RequestMethod.GET)
    private @ResponseBody
    int countBidsByExchange(@RequestParam("exId") Long exId) {
        Exchange exchange = exchangeService.getExchange(exId);
        return bidService.getBidsByExchange(exchange).size();
    }

    @RequestMapping(value = "/selectedCRD", method = RequestMethod.POST)
    private @ResponseBody
    List<String> selectCrd(
            @RequestParam("types") String types,
            @RequestParam("regions") String regs,
            @RequestParam("curs") String curs,
            @RequestParam("dpdmin") int dpdmin,
            @RequestParam("dpdmax") int dpdmax,
            @RequestParam("zbmin") double zbmin,
            @RequestParam("zbmax") double zbmax) {
        String[] typesMass = types.split(",");
        String[] regsMass = regs.split(",");
        String[] curMass = curs.split(",");
        List<Credit> crList = creditService.selectCredits(typesMass, regsMass, curMass, dpdmin, dpdmax, zbmin, zbmax);
        List<String> rezList = new ArrayList<>();
        rezList.add("ID" + '|' + "ІНН" + '|' + "Номер договору" + '|' + "ФІО" + '|' + "Регіон" + '|' + "Код типу активу" + '|'
                + "Код групи активу" + '|' + "Тип клієнта" + '|' + "Дата видачі" + '|'
                + "Дата закінчення" + '|' + "Валюта" + '|' + "Продукт" + '|'
                + "Загальний борг, грн." + '|' + "dpd" + '|' + "Вартість об'єкту, грн.");

        for (Credit cr : crList) {
            rezList.add(cr.getNd() + cr.toShotString());
        }
        return rezList;
    }

    @RequestMapping(value = "/selectAssetsbyLot", method = RequestMethod.POST)
    private @ResponseBody
    List<Asset> selectAssetsbyLot(@RequestParam("lotId") Long lotId) {
        return lotService.getAssetsByLot(lotId);
    }

    @RequestMapping(value = "/selectCreditsLot", method = RequestMethod.POST)
    private @ResponseBody
    List<Credit> selectCreditsLot(@RequestParam("lotId") Long lotId) {
        return creditService.getCrditsByLotId(lotId);
    }

    @RequestMapping(value = "/delObjectFromLot", method = RequestMethod.POST)
    private @ResponseBody
    String delObjectFromLot(HttpSession session,
                            @RequestParam("objId") Long objId,
                            @RequestParam("lotId") Long lotId) {
        Lot lot = lotService.getLot(lotId);
        String login = (String) session.getAttribute("userId");
        boolean isitUpdated = true;
        if (lot.getLotType() == 0) {
            Credit credit = creditService.getCredit(objId);
            credit.setLot(null);
            isitUpdated = creditService.updateCredit(login, credit);
        } else if (lot.getLotType() == 1) {
            Asset asset = assetService.getAsset(objId);
            asset.setLot(null);
            isitUpdated = assetService.updateAsset(login, asset);
        }
        if (isitUpdated)
            return "1";
        else
            return "0";
    }

    @RequestMapping(value = "/changeObjAccPrice", method = RequestMethod.POST)
    private @ResponseBody
    String changeObjAccPrice(HttpSession session,
                             @RequestParam("objId") Long objId,
                             @RequestParam("objAccPrice") BigDecimal accPrice,
                             @RequestParam("lotId") Long lotId) {
        Lot lot = lotService.getLot(lotId);
        String login = (String) session.getAttribute("userId");
        boolean isitUpdated = false;
        if (lot.getLotType() == 0) {
            Credit credit = creditService.getCredit(objId);
            credit.setAcceptPrice(accPrice);
            isitUpdated = creditService.updateCredit(login, credit);
        } else if (lot.getLotType() == 1) {
            Asset asset = assetService.getAsset(objId);
            asset.setAcceptPrice(accPrice);
            isitUpdated = assetService.updateAsset(login, asset);
        }
        if (isitUpdated)
            return "1";
        else
            return "0";
    }

    @RequestMapping(value = "/setAcceptedPrice", method = RequestMethod.POST)
    private @ResponseBody
    String setAcceptedPrice(HttpSession session,
                            @RequestParam("assetId") String assetId,
                            @RequestParam("acceptPrice") BigDecimal acceptPrice) {
        String login = (String) session.getAttribute("userId");
        Asset asset = assetService.getAsset(Long.parseLong(assetId));
        asset.setAcceptPrice(acceptPrice);
        assetService.updateAsset(login, asset);
        return "1";
    }

    @RequestMapping(value = "/setAcceptEx", method = RequestMethod.POST)
    private @ResponseBody
    String setAcceptEx(HttpSession session,
                       @RequestParam("lotId") String lotId,
                       @RequestParam("acceptEx") Long exId) {
        String login = (String) session.getAttribute("userId");
        Lot lot = lotService.getLot(Long.parseLong(lotId));
        try {
            lot.setAcceptExchange(exchangeService.getExchange(exId).getCompanyName());
        }
        catch (NullPointerException e){
            lot.setAcceptExchange(null);
        }
        lotService.updateLot(login, lot);
        return "1";
    }

    @RequestMapping(value = "/changeFondDec", method = RequestMethod.POST)
    private @ResponseBody
    String changeFondDec(HttpSession session,
                         @RequestParam("lotId") Long lotId,
                         @RequestParam("fondDecDate") String fondDecDate,
                         @RequestParam("fondDec") String fondDec,
                         @RequestParam("decNum") String decNum)
    {

        String login = (String) session.getAttribute("userId");

        Date date = null;
        try {
            date = sdfshort.parse(fondDecDate);
        } catch (ParseException e) {
            System.out.println("Халепа!");
        }

        Lot lot = lotService.getLot(lotId);
        lot.setFondDecisionDate(date);
        lot.setFondDecision(fondDec);
        lot.setDecisionNumber(decNum);
        lot.setNeedNewFondDec(false); //убираем необходимость пересогласования
        lotService.updateLot(login, lot);
        return "1";
    }

    @RequestMapping(value = "/changeNBUDec", method = RequestMethod.POST)
    private @ResponseBody
    String changeNBUDec (HttpSession session,
                         @RequestParam("lotId") Long lotId,
                         @RequestParam("NBUDecDate") String nbuDecDate,
                         @RequestParam("NBUDec") String nbuDec,
                         @RequestParam("decNum") String decNum)
    {

        String login = (String) session.getAttribute("userId");

        Date date = null;
        try {
            date = sdfshort.parse(nbuDecDate);
        } catch (ParseException e) {
            System.out.println("Халепа!");
        }

        Lot lot = lotService.getLot(lotId);
        lot.setNbuDecisionDate(date);
        lot.setNbuDecision(nbuDec);
        lot.setNbuDecisionNumber(decNum);
        lotService.updateLot(login, lot);
        return "1";
    }

    @RequestMapping(value = "/updateCreditsInLot", method = RequestMethod.POST)
    private @ResponseBody
    String updateCreditsInLot(HttpSession session,
                              @RequestParam("newPricesId") String newPricesId,
                              @RequestParam("newPrice") String newPrices,
                              @RequestParam("factPricesId") String factPricesId,
                              @RequestParam("factPrice") String factPrices,
                              @RequestParam("soldId") String soldId) {
        String login = (String) session.getAttribute("userId");
        if (!newPricesId.equals("")) {
            String[] newPricesIdMass = newPricesId.split(",");
            String[] newPricesMass = newPrices.split(",");
            for (int i = 0; i < newPricesIdMass.length; i++) {
                Credit credit = creditService.getCredit(Long.parseLong(newPricesIdMass[i]));
                credit.setDiscountPrice(BigDecimal.valueOf(Double.valueOf(newPricesMass[i])));
                creditService.updateCredit(login, credit);

            }
        }

        if (!factPricesId.equals("")) {
            String[] factPricesIdMass = factPricesId.split(",");
            String[] factPricesMass = factPrices.split(",");
            for (int i = 0; i < factPricesIdMass.length; i++) {
                Credit credit = creditService.getCredit(Long.parseLong(factPricesIdMass[i]));
                credit.setFactPrice(BigDecimal.valueOf(Double.parseDouble(factPricesMass[i])));
                creditService.updateCredit(login, credit);

            }
        }

        if (!soldId.equals("")) {
            String[] soldIdMass = soldId.split(",");
            for (String sId : soldIdMass) {
                Credit credit = creditService.getCredit(Long.parseLong(sId));
                credit.setSold(true);
                creditService.updateCredit(login, credit);

            }
        }
        return "1";
    }

    @RequestMapping(value = "/selectedParam", method = RequestMethod.POST)
    private @ResponseBody
    List<String> getParam(@RequestParam("types") String types,
                          @RequestParam("regions") String regs,
                          @RequestParam("curs") String curs,
                          @RequestParam("dpdmin") int dpdmin,
                          @RequestParam("dpdmax") int dpdmax,
                          @RequestParam("zbmin") double zbmin,
                          @RequestParam("zbmax") double zbmax) {
        String[] typesMass = types.split(",");
        String[] regsMass = regs.split(",");
        String[] curMass = curs.split(",");
        List<String> paramList = creditService.getCreditsResults(typesMass, regsMass, curMass, dpdmin, dpdmax, zbmin, zbmax);
        return paramList;
    }

    @RequestMapping(value = "/createSLot", method = RequestMethod.POST)
    private @ResponseBody
    String createLot(HttpSession session, Model model,
                     @RequestParam("idMass") String ids,
                     @RequestParam("comment") String comment) {
        String[] idm;
        try {
            idm = ids.substring(1).split(",");
        } catch (IndexOutOfBoundsException e) {
            return "0";
        }
        String userLogin = (String) session.getAttribute("userId");
        User user = userService.getByLogin(userLogin);
        BigDecimal startPrice = new BigDecimal(0);
        Lot newlot = new Lot("" + comment, user, new Date(), 1);
        Long lotRid = lotService.createLot(userLogin, newlot);
        for (String id : idm) {
            Asset asset = assetService.getAsset(Long.parseLong(id));
            if (asset.getAcceptPrice() != null)
                startPrice = startPrice.add(asset.getAcceptPrice());
            else
                startPrice = startPrice.add(asset.getRv());
            if (asset.getLot() == null) asset.setLot(newlot);
            assetService.updateAsset(userLogin, asset);
        }
        newlot.setStartPrice(startPrice);
        newlot.setFirstStartPrice(startPrice);

        lotService.updateLot(newlot);

        model.addAttribute("lotRid", lotRid.toString());
        return "1";
    }

    @RequestMapping(value = "/createCreditLot", method = RequestMethod.POST)
    private @ResponseBody
    String createCreditLot(HttpSession session, Model model,
                           @RequestParam("idMass") String ids,
                           @RequestParam("comment") String comment) {
        String[] idm;
        try {
            idm = ids.substring(1).split(",");
        } catch (IndexOutOfBoundsException e) {
            return "0";
        }
        String userLogin = (String) session.getAttribute("userId");
        User user = userService.getByLogin(userLogin);
        BigDecimal startPrice = new BigDecimal(0);
        Lot newlot = new Lot("" + comment, user, new Date(), 0);
        Long lotRid = lotService.createLot(userLogin, newlot);
        for (String id : idm) {
            Credit crdt = creditService.getCredit(Long.parseLong(id));
            BigDecimal acceptedPr =crdt.getAcceptPrice();
            BigDecimal rv =crdt.getRv();
            if (acceptedPr != null) {
                startPrice = startPrice.add(acceptedPr);
                crdt.setFirstStartPrice(acceptedPr);
                crdt.setStartPrice(acceptedPr);
             //   creditService.updateCredit(crdt);
            }
            else {
                startPrice = startPrice.add(rv);
                crdt.setFirstStartPrice(rv);
                crdt.setStartPrice(rv);
              //  creditService.updateCredit(crdt);
            }
            if (crdt.getLot() == null) crdt.setLot(lotRid);
            creditService.updateCredit(userLogin, crdt);
        }
        newlot.setStartPrice(startPrice);
        newlot.setFirstStartPrice(startPrice);
        lotService.updateLot(newlot);

        model.addAttribute("lotRid", lotRid.toString());
        return "1";
    }

    @RequestMapping(value = "/createBid", method = RequestMethod.GET)
    private @ResponseBody
    String createBid(@RequestParam("exId") String exId,
                     @RequestParam("bidDate") String bidD,
                     @RequestParam("newspaper") String newspaper,
                     @RequestParam("newsDate1") String newsD1,
                     @RequestParam("registrEnd") String regEnd) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date bidDate, newsDate1, registrEnd;
        try {
            bidDate = bidD.equals("") ? null : format.parse(bidD);
            newsDate1 = newsD1.equals("") ? null : format.parse(newsD1);
            registrEnd = regEnd.equals("") ? null : format.parse(regEnd);
        } catch (ParseException e) {
            e.printStackTrace();
            return "0";
        }
        Exchange exchange = exchangeService.getExchange(Long.parseLong(exId));
        Bid bid = new Bid(bidDate, exchange, newspaper, newsDate1, registrEnd);
        bidService.createBid(bid);
        return "1";
    }

    @RequestMapping(value = "/getAssetHistory", method = RequestMethod.POST)
    private @ResponseBody
    List assetHistory(@RequestParam("inn") String inn) {
        List<String> rezList = new ArrayList<>();
        String temp;
        Asset asset = (Asset) assetService.getAllAssetsByInNum(inn).get(0);

        List<Long> lotIdList = assetService.getLotIdHistoryByAsset(asset.getId());
        for(Long lotId: lotIdList){
            List<Bid> bidList = lotService.getLotHistoryAggregatedByBid(lotId);
            Collections.sort(bidList);
            for(Bid bid: bidList){
                temp = asset.getInn() + "||" + lotId+ "||" +bid.getExchange().getCompanyName()+ "||" +sdfshort.format(bid.getBidDate()) + "||" +assetService.getAccPriceByLotIdHistory(asset.getId(), lotId);
                rezList.add(temp);
            }
        }
            return rezList;
    }

    @RequestMapping(value = "/getAccPriceHistory", method = RequestMethod.POST)
    private @ResponseBody
    List getDateAndAccPriceHistoryByAsset(@RequestParam("inn") String inn) {
        Asset asset = (Asset) assetService.getAllAssetsByInNum(inn).get(0);
        List<AcceptPriceHistory> acceptPriceHistoryList = assetService.getDateAndAccPriceHistoryByAsset(asset.getId());
        return acceptPriceHistoryList;
    }

    @RequestMapping(value = "/getCreditsHistory", method = RequestMethod.POST)
    private @ResponseBody
    List creditHistory(@RequestParam("inn") String inn, @RequestParam("idBars") Long idBars) {
        List<String> rezList = new ArrayList<>();
        String temp;
        Credit credit = (Credit) creditService.getAllCreditsByClient(inn, idBars).get(0);
        List <Long> lotIdList = creditService.getLotIdHistoryByCredit(credit.getNd());
        for(Long lotId: lotIdList){
            List<Bid> bidList = lotService.getLotHistoryAggregatedByBid(lotId);
            Collections.sort(bidList);
            for(Bid bid: bidList){
                temp = credit.getInn() + "||" + lotId+ "||" +bid.getExchange().getCompanyName()+ "||" +sdfshort.format(bid.getBidDate()) + "||" +creditService.getPriceByLotIdHistory(credit.getId(), lotId);
                rezList.add(temp);
            }
        }
        return rezList;
    }

    @RequestMapping(value = "/getCrPriceHistory", method = RequestMethod.POST)
    private @ResponseBody
    List getCrPriceHistory(@RequestParam("inn") String inn, @RequestParam("idBars") Long idBars) {
        Credit credit = (Credit) creditService.getAllCreditsByClient(inn, idBars).get(0);
        List<CreditAccPriceHistory> creditPriceHistoryList = creditService.getDateAndAccPriceHistoryByCredit(credit.getId());
        return creditPriceHistoryList;
    }
}