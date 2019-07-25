package nb.service;

import nb.domain.*;
import nb.queryDomain.BidDetails;
import nb.util.CustomDateFormats;
import nb.util.MathCalculationUtil;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private AssetService assetService;
    @Autowired
    private BidService bidService;
    @Autowired
    private CreditService creditService;
    @Autowired
    private LotService lotService;
    @Autowired
    private PayService payService;

    public ReportServiceImpl() {
    }

    @Override
    public File makeDodatok(List<Asset> assetList, List<Credit> creditList, String startDate, String endDate) throws IOException {

        HSSFWorkbook wb;

        try(POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream("C:\\projectFiles\\Table prodaj.xls"))){
            wb = new HSSFWorkbook(fs);
        }

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
        sheet.shiftRows(8, 8, assetList.size() + creditList.size() - 1);
        int numRow = 7;
        int i = 0;
        for (Asset asset : assetList) {
            HSSFRow row = sheet.createRow(numRow);
            i++;
            numRow++;
            int j = 0;
            while (j < 67) {
                row.createCell(j);
                j++;
            }
            Lot lot = asset.getLot();
            Bid bid = lot.getBid();
            //   BigDecimal coeffRV = getCoefficient(asset.getRv(), lotService.lotSum(lot));// asset.getRv().divide(lotService.lotSum(lot), 10, BigDecimal.ROUND_HALF_UP);
            BigDecimal coeffAcc = MathCalculationUtil.getCoefficient(asset.getAcceptPrice(), lotService.lotAcceptedSum(lot));
            //
            row.getCell(0).setCellValue(i);
            row.getCell(1).setCellValue(380764);
            if (lot != null && lot.getFondDecisionDate() != null) {
                row.getCell(2).setCellValue(lot.getFondDecisionDate());
                row.getCell(2).setCellStyle(cellStyle);
            }
            if (lot != null) {
                row.getCell(3).setCellValue(lot.getLotNum());
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
            if (asset.getZb() != null)
                row.getCell(11).setCellValue(asset.getZb().doubleValue());
            row.getCell(12).setCellValue(asset.getRv().doubleValue());
            if (lot.getFirstStartPrice() != null)
                // row.getCell(13).setCellValue(lot.getFirstStartPrice().multiply(coeffRV).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());// Початкова ціна реалізації активу, з ПДВ, грн.
                //  row.getCell(13).setCellValue(asset.getAcceptPrice().doubleValue());// Початкова ціна реалізації активу, з ПДВ, грн.
                try {
                    row.getCell(13).setCellValue(assetService.getFirstAccPrice(asset.getId()).doubleValue());
                } catch (NullPointerException e) {
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
            row.getCell(49).setCellFormula("(1-AZ" + numRow + "/N" + numRow + ")*100");//Зниження початкової ціни реалізації активу
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
            if (asset.getPaysBid() != null) {
                row.getCell(58).setCellValue(asset.getPaysBid().doubleValue());
                row.getCell(58).setCellStyle(numStyle);
            }
            if (asset.getPaysBid() != null) {
                row.getCell(57).setCellValue(asset.getPaysBid().doubleValue());
                row.getCell(57).setCellStyle(numStyle);
            }
            try {
                row.getCell(65).setCellValue(lot.getCustomer().shortDescription());
            } catch (NullPointerException npe) {
            }
            try {
                row.getCell(66).setCellValue(lot.getCustomer().getCustomerInn());
            } catch (NullPointerException npe) {
            }
        }
        for (Credit credit : creditList) {
            HSSFRow row = sheet.createRow(numRow);
            i++;
            numRow++;
            int j = 0;
            while (j < 67) {
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
            row.getCell(6).setCellValue(credit.getNd());
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
            if (credit.getFirstStartPrice() != null)
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
            try {
                row.getCell(65).setCellValue(lot.getCustomer().shortDescription());
            } catch (NullPointerException npe) {
            }
            try {
                row.getCell(66).setCellValue(lot.getCustomer().getCustomerInn());
            } catch (NullPointerException npe) {
            }
        }

        int tableEnd = 7 + assetList.size() + creditList.size();
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

        File file = new File("C:\\projectFiles\\Table prodaj " + startDate + " по " + endDate + ".xls");

        try(OutputStream outputStream = new FileOutputStream(file)){
            wb.write(outputStream);
        }

        return file;
    }
    @Override
    public String makeOgoloshennya(Long bidId) throws IOException {

        Bid bid = bidService.getBid(bidId);
        List<Lot> lotsByBidList = lotService.getLotsByBid(bid);
        Set<String> decisionsSet = new TreeSet<>();
        for (Lot lot : lotsByBidList) {
            if (lot.getFondDecisionDate() != null)
                decisionsSet.add(lot.getDecisionNumber() + " від " + CustomDateFormats.sdfpoints.format(lot.getFondDecisionDate()));
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
            StringBuilder assetName = new StringBuilder();
            StringBuilder assetDesc = new StringBuilder();
            Lot lot = lotsByBidList.get(i);

            List<Asset> notTMCList = lotService.getNotTMCAssetsByLot(lot);
            List<Asset> TMCList = lotService.getTMCAssetsByLot(lot);
            for (Asset asset : notTMCList) {
                assetName.append(asset.getAsset_name()).append(" ");
                assetDesc.append(asset.getAsset_descr()).append(" ");
            }
            if (TMCList.size() > 0) {
                assetDesc.append(" +").append(TMCList.size()).append(" од. ТМЦ");
            }
            XWPFTableRow row = tab1.getRow(i + 1);
            row.getCell(0).setText(lot.getLotNum());
            row.getCell(1).setText(assetName.toString());
            row.getCell(2).setText(assetDesc.toString());
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

        tab2.getRow(12).getCell(1).setText(String.valueOf(CustomDateFormats.sdfpoints.format(bid.getBidDate())) + " року");
        tab2.getRow(14).getCell(1).setText(String.valueOf(bid.getExchange().getEmail()));

        if (bid.getRegistrEndDate() != null) {
            tab2.getRow(16).getCell(1).setText(String.valueOf("до 17 год. 00 хв. " + CustomDateFormats.sdfpoints.format(bid.getRegistrEndDate())) + " року");
            tab2.getRow(17).getCell(1).setText(String.valueOf("до 17 год. 00 хв. " + CustomDateFormats.sdfpoints.format(bid.getRegistrEndDate())) + " року; ");
            tab2.getRow(17).getCell(1).setText(String.valueOf("до 17 год. 00 хв. " + CustomDateFormats.sdfpoints.format(bid.getRegistrEndDate())) + " року");
        }

        //Таблица 3 заполнение
        tab3.getRow(3).getCell(1).setText(String.valueOf(CustomDateFormats.sdfpoints.format(bid.getBidDate())) + " р.");

        String fileName = "C:\\projectFiles\\Dodatok 2 (" + String.valueOf(CustomDateFormats.sdfshort.format(bid.getBidDate())) + ").docx";

        try(OutputStream fileOut = new FileOutputStream(fileName)){
            docx.write(fileOut);
        }

        return fileName;
    }
    @Override
    public String fillCrdTab(List<Credit> creditList) throws IOException {
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
        for (Credit credit : creditList) {
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

        String fileName = "C:\\projectFiles\\" + ("Credits " + CustomDateFormats.sdfshort.format(new Date()) + ".xlsx");

        try(OutputStream fileOut = new FileOutputStream(fileName)){
            wb.write(fileOut);
        }
        return fileName;
    }
    @Override
    public File fillSoldedCrdTab(List<Credit> creditList) throws IOException {

        XSSFWorkbook wb;

        try(InputStream ExcelFileToRead = new FileInputStream("C:\\projectFiles\\CREDITS_solded.xlsx")){
            wb = new XSSFWorkbook(ExcelFileToRead);
        }

        XSSFSheet sheet = wb.getSheetAt(0);

        //задаем формат даты
        String excelFormatter = DateFormatConverter.convert(Locale.ENGLISH, "yyyy-MM-dd");
        CellStyle dateStyle = wb.createCellStyle();

        DataFormat poiFormat = wb.createDataFormat();
        dateStyle.setDataFormat(poiFormat.getFormat(excelFormatter));

        //end

        int numRow = 1;
        // int i = 0;
        for (Credit credit : creditList) {
            XSSFRow row = sheet.createRow(numRow);
            numRow++;
            Lot tLot = lotService.getLot(credit.getLot());
            try {
                row.createCell(2).setCellValue(tLot.getLotNum());
                row.createCell(7).setCellValue(tLot.getCustomer().shortDescription());
                try {
                    row.createCell(0).setCellValue(tLot.getBid().getBidDate());
                    row.getCell(0).setCellStyle(dateStyle);
                } catch (NullPointerException npl) {
                }
            } catch (NullPointerException npl) {
            }

            row.createCell(3).setCellValue(credit.getFio());
            row.createCell(4).setCellValue(credit.getInn());
            row.createCell(5).setCellValue(credit.getContractNum());
            row.createCell(6).setCellValue("S" + credit.getNd());
            try {
                row.createCell(8).setCellValue("S" + (credit.getFactPrice().multiply(new BigDecimal(100))).longValue());
            } catch (NullPointerException npe) {
                row.createCell(8);
            } catch (NumberFormatException nfe) {
                row.createCell(8).setCellValue("S" + 0);
            }
        }

        //    String fileName = "" + CustomDateFormats.sdfshort.format(new Date());
        File file = new File("C:\\projectFiles\\CREDITS_solded "+ CustomDateFormats.sdfshort.format(new Date())+".xlsx");

        try(OutputStream fileOut = new FileOutputStream(file)){
            wb.write(fileOut);
        }
        return file;
    }
    @Override
    public File fillCreditsReestr(List<Lot> lotList) throws IOException {

        XSSFWorkbook wb;

        try(InputStream ExcelFileToRead = new FileInputStream("C:\\projectFiles\\Reestr.xlsx"))
        {
            wb = new XSSFWorkbook(ExcelFileToRead);
        }

        XSSFSheet sheet = wb.getSheetAt(0);

        //задаем формат даты
        String excelFormatter = DateFormatConverter.convert(Locale.ENGLISH, "yyyy-MM-dd");
        CellStyle dateStyle = wb.createCellStyle();

        DataFormat poiFormat = wb.createDataFormat();
        dateStyle.setDataFormat(poiFormat.getFormat(excelFormatter));

        int rowNum=0;
        for(Lot lot: lotList){

            for(Credit credit: creditService.getCrditsByLotId(lot.getId())){
                XSSFRow row = sheet.createRow(++rowNum+1);
                row.createCell(0).setCellValue(rowNum);
                row.createCell(1).setCellValue("");
                if(lot.getBid()!=null){
                    row.createCell(2).setCellValue(lot.getBid().getBidDate());
                    row.getCell(2).setCellStyle(dateStyle);
                }
                row.createCell(3).setCellValue(lot.getDecisionNumber());

                row.createCell(4).setCellValue(""); //№ Протоколу електронного аукціону

                row.createCell(5).setCellValue(credit.getProduct());
                row.createCell(6).setCellValue(lot.getLotNum());
                row.createCell(7).setCellValue(credit.getFio());
                row.createCell(8).setCellValue(credit.getInn());
                row.createCell(9).setCellValue(credit.getContractNum());
                row.createCell(10).setCellValue(credit.getZast());
                if(lot.getCustomer()!=null){
                    row.createCell(11).setCellValue(lot.getCustomer().shortDescription());
                    String isMerried = lot.getCustomer().isMerried() ? "одружений" : "неодружений";
                    row.createCell(12).setCellValue(isMerried);
                    row.createCell(13).setCellValue("");//Контактні дані Покупця
                    row.createCell(15).setCellValue(lot.getCustomer().shortDescription());

                }
                row.createCell(14); //Погоджена з покупцем дата вчинення правочинів

                row.createCell(16).setCellValue(lot.getDeadlineDate());
                row.getCell(16).setCellStyle(dateStyle);
                row.createCell(17).setCellValue(lot.getProzoroDate());
                row.getCell(17).setCellStyle(dateStyle);

            }
        }

        File file = new File("C:\\projectFiles\\Credits_reestr " + CustomDateFormats.sdfshort.format(new Date()) + ".xlsx");
        try(OutputStream fileOut = new FileOutputStream(file)){
            wb.write(fileOut);
        }
        return file;
    }
    @Override
    public File fillAssTab() throws IOException {
       // List<Lot> lotList = lotService.getLotsByType(1);

        XSSFWorkbook xwb;

        try(InputStream ExcelFileToRead = new FileInputStream("C:\\projectFiles\\Temp.xlsx")){
            xwb = new XSSFWorkbook(ExcelFileToRead);
        }

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
       /* SXSSFRow lotTitleRow = lotSheet.createRow(0);
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

        int lotN = 2;
        for (Lot lot : lotList) {
            SXSSFRow lotDataRow = lotSheet.createRow(lotN);
            lotN++;
            lotDataRow.createCell(0).setCellValue(lot.getId());
            lotDataRow.createCell(1).setCellValue(lot.getLotNum());
            lotDataRow.createCell(2).setCellValue(lot.getWorkStage());
            lotDataRow.createCell(3).setCellValue(lot.getItSold());
            lotDataRow.createCell(4).setCellValue(lot.getComment());
            try {
                lotDataRow.createCell(5).setCellValue(lot.getLotDate());
                lotDataRow.getCell(5).setCellStyle(dateStyle);
            } catch (NullPointerException e) {
            }
            lotDataRow.createCell(6).setCellValue(lot.getBidStage());
            lotDataRow.createCell(7).setCellValue(lot.getCountOfParticipants());
            try {
                lotDataRow.createCell(8).setCellValue(lot.getStartPrice().doubleValue());
            } catch (NullPointerException e) {
            }
            try {
                lotDataRow.createCell(9).setCellValue(lot.getFirstStartPrice().doubleValue());
            } catch (NullPointerException e) {
            }
            try {
                lotDataRow.createCell(10).setCellValue(lot.getFactPrice().doubleValue());
            } catch (NullPointerException e) {
            }
            try{
                lotDataRow.createCell(11).setCellValue(lot.getCustomer()==null ? "":lot.getCustomer().shortDescription());
            }
            catch (Exception e){
            }

            lotDataRow.createCell(12).setCellValue(lot.getStatus());
            try {
                lotDataRow.createCell(13).setCellValue(lot.getActSignedDate());
                lotDataRow.getCell(13).setCellStyle(dateStyle);
            } catch (NullPointerException e) {
            }
            try {
                lotDataRow.createCell(14).setCellValue(lot.getBid().getBidDate());
                lotDataRow.getCell(14).setCellStyle(dateStyle);
            } catch (NullPointerException e) {
            }
            try {
                lotDataRow.createCell(15).setCellValue(lot.getBid().getExchange().getCompanyName());
            } catch (NullPointerException e) {
            }
        }
*/
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
                if (lot.isNeedNewFondDec())
                    row.createCell(21).setCellValue("Так");
                else {
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

        File file = new File("C:\\projectFiles\\Assets " + CustomDateFormats.sdfshort.format(new Date()) + ".xlsx");

        try(OutputStream fileOut = new FileOutputStream(file)){
            wb.write(fileOut);
        }

        return file;
    }
    @Override
    public File getTempFile(MultipartFile multipartFile) throws IOException {

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
    @Override
    public File makePaymentsReport(List<Pay> payList, String start, String end) throws IOException {

        XSSFWorkbook wb;

        try(InputStream ExcelFileToRead = new FileInputStream("C:\\projectFiles\\Pays.xlsx")) {

            wb = new XSSFWorkbook(ExcelFileToRead);
            XSSFSheet sheet = wb.getSheetAt(0);

            String excelFormatter = DateFormatConverter.convert(Locale.ENGLISH, "yyyy-MM-dd");
            CellStyle cellStyle = wb.createCellStyle();
            cellStyle.setDataFormat(wb.createDataFormat().getFormat(excelFormatter));

            for (int i = 0; i < payList.size(); i++) {
                Pay pay = payList.get(i);
                XSSFRow payRow = sheet.createRow(i + 1);
                payRow.createCell(0).setCellValue(pay.getDate());
                payRow.getCell(0).setCellStyle(cellStyle);
                payRow.createCell(1).setCellValue(pay.getPaySum().doubleValue());
                payRow.createCell(2).setCellValue(pay.getPaySource());
                payRow.createCell(3).setCellValue(pay.getLotId());
                payRow.createCell(4).setCellValue(lotService.getLot(pay.getLotId()).getLotNum());
            }
        }

        File file = new File("C:\\projectFiles\\Payments_" + start + "_" + end + ".xlsx") ;

        try(OutputStream fileOut = new FileOutputStream(file)){
            wb.write(fileOut);
        }

        return file;
    }
    @Override
    public File makeBidsSumReport(List<LotHistory> lotList, List<BidDetails> aggregatedLotList) throws IOException {

        XSSFWorkbook xwb;
        try(InputStream ExcelFileToRead = new FileInputStream("C:\\projectFiles\\Temp1.xlsx")){
            xwb = new XSSFWorkbook(ExcelFileToRead);
        }

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

        int rowNum = 0;

        for (LotHistory lot : lotList) {
            rowNum++;
            SXSSFRow row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(lot.getBidId());
            Bid bid = bidService.getBid(lot.getBidId());
            try {
                row.createCell(1).setCellValue(bid.getExchange().getCompanyName() + "_" + CustomDateFormats.sdfshort.format(bid.getBidDate()));
            } catch (NullPointerException e) {
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

        int rowN = 0;

        for (BidDetails aggregatedLot : aggregatedLotList) {
            rowN++;
            SXSSFRow row = sheetST.createRow(rowN);
            row.createCell(0).setCellValue(aggregatedLot.getBidId());
            Bid bid = bidService.getBid(aggregatedLot.getBidId());
            try {
                row.createCell(1).setCellValue(bid.getExchange().getCompanyName() + "_" + CustomDateFormats.sdfshort.format(bid.getBidDate()));
            } catch (NullPointerException e) {
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

        File file = new File ("C:\\projectFiles\\Bids_report.xlsx");
        try(OutputStream fileOut = new FileOutputStream(file)){
            wb.write(fileOut);
            //fileOut.flush();
        }
        return file;
    }
}