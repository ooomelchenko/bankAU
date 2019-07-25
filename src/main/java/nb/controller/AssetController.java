package nb.controller;

import nb.additional.SignerBank;
import nb.domain.*;
import nb.queryDomain.AcceptPriceHistory;
import nb.queryDomain.CreditAccPriceHistory;
import nb.queryDomain.FondDecisionsByLotHistory;
import nb.service.*;
import nb.util.CustomDateFormats;
import nb.util.Excel;
import nb.util.MathCalculationUtil;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.util.DateFormatConverter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ReportService reportService;

    private static final String documentsPath = "C:\\SCAN\\DocumentsByLots\\";
    private static final String bidDocumentsPath = "C:\\SCAN\\DocumentsByBid\\";

    private String replaceRunText(String text, String kd, String year, String fio, String address, String inn, String prn, String prd, String pmb, String fpr, String knd, String subscr) {
        text = text.replace("kd", kd);
        text = text.replace("year", year);
        text = text.replace("fio", fio);
        text = text.replace("address", address);
        text = text.replace("inn", inn);
        text = text.replace("prn", prn);
        text = text.replace("prd", prd);
        text = text.replace("pmb", pmb);
        text = text.replace("fpr", fpr);
        text = text.replace("knd", knd);
        if (subscr.equals("null"))
            text = text.replace("sub", fio);
        else
            text = text.replace("sub", subscr);
        return text;
    }

    private String replaceRunTextAssets(String text, String bidDAte, int count, String kd, String year, String fio, String address, String inn,
                                        String prn, String prd, String pmb, String fpr, String subscr, String pass_seria,
                                        String pass_num, String pass_vidano, String pass_vidano_date, String operates_basis, String account_bank,
                                        String bid_enter, String bid_client, String signer_bank, String signer_bank_text,
                                        String property_fp, String property_fp_pdv, String elseAssets_fp, String elseAssets_pdv) {

        String factPrice_pdv;
        try {
            factPrice_pdv = String.valueOf(BigDecimal.valueOf(Double.parseDouble(fpr) / 6).setScale(2, RoundingMode.UP));
        } catch (Exception e) {
            factPrice_pdv = "";
        }

        text = text.replace("audat", bidDAte);

        text = text.replace("count", String.valueOf(count));
        text = text.replace("kd", kd);
        text = text.replace("year", year);
        text = text.replace("fio", fio);
        text = text.replace("address", address);
        text = text.replace("inn", inn);
        text = text.replace("prn", prn);
        text = text.replace("prd", prd);
        text = text.replace("pmb", pmb);
        text = text.replace("fpr", fpr);
        text = text.replace("pdv", factPrice_pdv);
        text = text.replace("ps", pass_seria);
        text = text.replace("pn", pass_num);
        text = text.replace("pv", pass_vidano);
        text = text.replace("pdat", pass_vidano_date);
        text = text.replace("ob", operates_basis);
        text = text.replace("bank", account_bank);

        text = text.replace("bide", bid_enter);
        text = text.replace("bidc", bid_client);
        text = text.replace("sbf", signer_bank);
        text = text.replace("sbt", signer_bank_text);
        text = text.replace("nfp", property_fp);
        text = text.replace("npd", property_fp_pdv);
        text = text.replace("nefp", elseAssets_fp);
        text = text.replace("nepd", elseAssets_pdv);

        if (subscr.equals("null"))
            text = text.replace("sub", fio);
        else
            text = text.replace("sub", subscr);
        return text;
    }

    private String makeHistoryReportByAssets(List<Asset> assetList) throws IOException {
        InputStream ExcelFileToRead = new FileInputStream("C:\\projectFiles\\History.xlsx");
        XSSFWorkbook wb = new XSSFWorkbook(ExcelFileToRead);
        XSSFSheet sheet1 = wb.getSheetAt(0);
        XSSFSheet sheet2 = wb.getSheetAt(1);
        XSSFSheet sheet3 = wb.getSheetAt(2);

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
        int numRow3 = 0;
        // int i = 0;
        for (Asset asset : assetList) {

            List<Long> lotIdList = assetService.getLotIdHistoryByAsset(asset.getId());
            for (Long lotId : lotIdList) {


                List<FondDecisionsByLotHistory> fondDecisionsByLotHistory = lotService.getFondDecisionsByLotHistory(lotId);
                for (FondDecisionsByLotHistory fondDecision : fondDecisionsByLotHistory) {
                    numRow3++;
                    XSSFRow row = sheet3.createRow(numRow3);
                    row.createCell(0).setCellValue(lotId);
                    try {
                        row.createCell(1).setCellValue(CustomDateFormats.sdfshort.format(fondDecision.getFondDecisionDate()));
                    } catch (Exception e) {

                    }
                    row.createCell(2).setCellValue(fondDecision.getDecisionNumber());
                    row.createCell(3).setCellValue(fondDecision.getFondDecision());
                }

                List<Bid> bidList = lotService.getLotHistoryAggregatedByBid(lotId);
                Collections.sort(bidList);
                for (Bid bid : bidList) {
                    numRow1++;
                    XSSFRow row = sheet1.createRow(numRow1);
                    row.createCell(0).setCellValue(asset.getInn());
                    row.createCell(1).setCellValue(lotId);
                    row.createCell(2).setCellValue(bid.getExchange().getCompanyName());
                    row.createCell(3).setCellValue(CustomDateFormats.sdfshort.format(bid.getBidDate()));
                    try {
                        row.createCell(4).setCellValue(assetService.getAccPriceByLotIdHistory(asset.getId(), lotId).doubleValue());
                    } catch (NullPointerException e) {
                    }

                }
            }
            List<AcceptPriceHistory> acceptPriceHistoryList = assetService.getDateAndAccPriceHistoryByAsset(asset.getId());
            for (AcceptPriceHistory acceptPriceHistory : acceptPriceHistoryList) {
                numRow2++;
                XSSFRow row2 = sheet2.createRow(numRow2);
                row2.createCell(0).setCellValue(asset.getInn());
                row2.createCell(1).setCellValue(CustomDateFormats.sdfshort.format(acceptPriceHistory.getDate()));
                row2.createCell(2).setCellValue(acceptPriceHistory.getAcceptedPrice().doubleValue());
            }
        }

        String fileName = "C:\\projectFiles\\" + ("History " + CustomDateFormats.sdfshort.format(new Date()) + ".xlsx");

        try(OutputStream fileOut = new FileOutputStream(fileName)){
            wb.write(fileOut);
        }

        return fileName;
    }

    private String creditContract(Lot lot, String contract_year, String contract_address, String contract_protokol_num, String contract_protokol_date, String protocol_made_by, String subscriber) throws IOException {

        List<Credit> creditList = lotService.getCRDTSByLot(lot);

        InputStream fs = new FileInputStream("C:\\projectFiles\\Lot_Credits_Docs\\Dogovir.docx");

        XWPFDocument docx = new XWPFDocument(fs);

        for (XWPFParagraph p : docx.getParagraphs()) {

            List<XWPFRun> runs = p.getRuns();
            if (runs != null) {
                for (XWPFRun r : runs) {
                    String text = r.getText(0);
                    if (text != null) {
                        StringBuilder tempText = new StringBuilder();
                        if (!creditList.isEmpty() && text.contains("knd")) {
                            for (Credit credit : creditList) {
                                tempText.append("№")
                                        .append(credit.getContractNum())
                                        .append(" від ")
                                        .append(CustomDateFormats.sdfpoints.format(credit.getContractStart()))
                                        .append("року,");
                            }
                        }
                        r.setText(replaceRunText(text, String.valueOf(creditList.get(0).getContractNum()), contract_year, String.valueOf((lot.getCustomer()==null) ?  "" : lot.getCustomer().shortDescription()),
                                String.valueOf(contract_address), String.valueOf((lot.getCustomer()==null) ?  "" : lot.getCustomer().getCustomerInn()),
                                String.valueOf(contract_protokol_num), String.valueOf(contract_protokol_date),
                                String.valueOf(protocol_made_by), String.valueOf(lot.getFactPrice()), tempText.toString(), subscriber), 0);
                    }
                }
            }
        }
        for (XWPFTable tbl : docx.getTables()) {

            for (XWPFTableRow row : tbl.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {

                        List<XWPFRun> runs = p.getRuns();
                        if (runs != null) {
                            for (XWPFRun r : runs) {
                                String text = r.getText(0);
                                if (text != null) {
                                    StringBuilder tempText = new StringBuilder();
                                    if (!creditList.isEmpty() && text.contains("knd")) {
                                        for (Credit credit : creditList) {
                                            tempText
                                                    .append("№")
                                                    .append(credit.getContractNum())
                                                    .append(" від ")
                                                    .append(CustomDateFormats.sdfpoints.format(credit.getContractStart()))
                                                    .append("року,");
                                        }
                                    }

                                    r.setText(replaceRunText(text, String.valueOf(creditList.get(0).getContractNum()), contract_year, String.valueOf((lot.getCustomer()==null) ?  "" : lot.getCustomer().shortDescription()),
                                            String.valueOf(contract_address), String.valueOf((lot.getCustomer()==null) ?  "" : lot.getCustomer().getCustomerInn()),
                                            String.valueOf(contract_protokol_num), String.valueOf(contract_protokol_date),
                                            String.valueOf(protocol_made_by), String.valueOf(lot.getFactPrice()), tempText.toString(), subscriber), 0);
                                }
                            }
                        }
                    }
                }
            }
        }

        String fileName = "C:\\projectFiles\\Lot_Credits_Docs\\Dogovir_" + lot.getId() + ".docx";
        OutputStream fileOut = new FileOutputStream(fileName);

        docx.write(fileOut);
        fileOut.close();
        return fileName;
    }

    private String assetContract(Lot lot, String contract_year, String contract_address, String contract_protokol_num, String contract_protokol_date, String protocol_made_by, String subscriber,
                                 String pass_seria, String pass_num, String pass_vidano, String pass_vidano_date, String operates_basis, String account_bank,
                                 String bid_enter, String bid_client, String signer_bank_fio, String signer_bank_text) throws IOException {

        List<Asset> assetList = lotService.getAssetsByLot(lot);

        String bidDate;
        try {
            bidDate = CustomDateFormats.sdfpoints.format(lot.getBid().getBidDate());
        } catch (NullPointerException e) {
            bidDate = "дата торгів";
        }

        InputStream fs = new FileInputStream("C:\\projectFiles\\Lot_Assets_Docs\\Dogovir.docx");

        XWPFDocument docx = new XWPFDocument(fs);

        List<XWPFTable> tableList = docx.getTables();

        XWPFTable objTable = tableList.get(0);
        int i = assetList.size();
        BigDecimal propertyPrice = new BigDecimal(0);


        XWPFTableRow totalRow = objTable.getRow(1);
        totalRow.getCell(0).setText("Основні засоби в кількості " + i + " одиниць загальною вартістю");
        try {
            totalRow.getCell(1).setText(String.valueOf(lot.getFactPrice()));
        } catch (NullPointerException npl) {
            totalRow.getCell(1).setText("0");
        }

        for (Asset asset : assetList) {
            if (asset.getFactPrice() != null && (asset.getAssetGroupCode().equals("101") || asset.getAssetGroupCode().equals("102")))
                propertyPrice = propertyPrice.add(asset.getFactPrice());
            XWPFTableRow newRow = objTable.insertNewTableRow(1);
            newRow.createCell().setText(i + ".");
            newRow.createCell().setText(asset.getInn());
            newRow.createCell().setText(asset.getAsset_name());
            newRow.createCell().setText(asset.getAsset_descr());
            newRow.createCell().setText(asset.getAddress());
            if (asset.getFactPrice() != null)
                newRow.createCell().setText(String.valueOf(asset.getFactPrice()));
            i--;
        }

        BigDecimal propertyPDV = propertyPrice.divide(new BigDecimal(6), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal notPropertyPrice;
        BigDecimal notPropertyPDV;
        try {
            notPropertyPrice = lot.getFactPrice().subtract(propertyPrice);
            notPropertyPDV = (lot.getFactPrice().divide(new BigDecimal(6), 2, BigDecimal.ROUND_HALF_UP)).subtract(propertyPDV);
        } catch (Exception npe) {
            notPropertyPrice = new BigDecimal(0);
            notPropertyPDV = new BigDecimal(0);
        }

        objTable.setInsideVBorder(XWPFTable.XWPFBorderType.SINGLE, 2, 0, "000000");
        objTable.setInsideHBorder(XWPFTable.XWPFBorderType.SINGLE, 2, 0, "000000");
        //objTable.removeRow(1);

        for (XWPFParagraph p : docx.getParagraphs()) {

            List<XWPFRun> runs = p.getRuns();
            if (runs != null) {
                for (XWPFRun r : runs) {
                    String text = r.getText(0);
                    if (text != null) {

                        r.setText(replaceRunTextAssets(text, bidDate, assetList.size(), lot.getLotNum(), contract_year, String.valueOf((lot.getCustomer()==null) ?  "" : lot.getCustomer().shortDescription()),
                                String.valueOf(contract_address), String.valueOf((lot.getCustomer()==null) ?  "" : lot.getCustomer().getCustomerInn()),
                                String.valueOf(contract_protokol_num), String.valueOf(contract_protokol_date),
                                String.valueOf(protocol_made_by), String.valueOf(lot.getFactPrice()), subscriber,
                                pass_seria, pass_num, pass_vidano, pass_vidano_date, operates_basis, account_bank,
                                bid_enter, bid_client, signer_bank_fio, signer_bank_text,
                                String.valueOf(propertyPrice), String.valueOf(propertyPDV),
                                String.valueOf(notPropertyPrice), String.valueOf(notPropertyPDV)), 0);
                    }
                }
            }
        }

        for (XWPFTable tbl : tableList) {

            for (XWPFTableRow row : tbl.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {

                        List<XWPFRun> runs = p.getRuns();
                        if (runs != null) {
                            for (XWPFRun r : runs) {
                                String text = r.getText(0);
                                if (text != null) {

                                    r.setText(replaceRunTextAssets(text, bidDate, assetList.size(), lot.getLotNum(), contract_year, String.valueOf((lot.getCustomer()==null) ?  "" : lot.getCustomer().shortDescription()),
                                            String.valueOf(contract_address), String.valueOf((lot.getCustomer()==null) ?  "" : lot.getCustomer().getCustomerInn()),
                                            String.valueOf(contract_protokol_num), String.valueOf(contract_protokol_date),
                                            String.valueOf(protocol_made_by), String.valueOf(lot.getFactPrice()), subscriber,
                                            pass_seria, pass_num, pass_vidano, pass_vidano_date, operates_basis,
                                            account_bank, bid_enter, bid_client, signer_bank_fio, signer_bank_text,
                                            String.valueOf(propertyPrice), String.valueOf(propertyPDV),
                                            String.valueOf(notPropertyPrice), String.valueOf(notPropertyPDV)), 0);
                                }
                            }
                        }
                    }
                }
            }
        }

        String fileName = "C:\\projectFiles\\Lot_Assets_Docs\\Dogovir_" + lot.getLotNum() + " (" + lot.getId() + ").docx";
        OutputStream fileOut = new FileOutputStream(fileName);

        docx.write(fileOut);
        fileOut.close();
        return fileName;
    }

    private String creditContract_Akt(Lot lot, String contract_year, String contract_address, String contract_protokol_num, String contract_protokol_date, String protocol_made_by, String subscriber) throws IOException {

        List<Credit> creditList = lotService.getCRDTSByLot(lot);

        InputStream fs = new FileInputStream("C:\\projectFiles\\Lot_Credits_Docs\\Dogovir_Akt.docx");

        XWPFDocument docx = new XWPFDocument(fs);

        for (XWPFParagraph p : docx.getParagraphs()) {

            List<XWPFRun> runs = p.getRuns();
            if (runs != null) {
                for (XWPFRun r : runs) {
                    String text = r.getText(0);
                    if (text != null) {
                        String tempText = "";
                        if (!creditList.isEmpty() && text.contains("knd")) {
                            for (Credit credit : creditList) {
                                tempText += "№" + credit.getContractNum() + " від " + CustomDateFormats.sdfpoints.format(credit.getContractStart()) + "року,";
                            }
                        }
                        r.setText(replaceRunText(text, String.valueOf(creditList.get(0).getContractNum()), contract_year, String.valueOf((lot.getCustomer()==null) ?  "" : lot.getCustomer().shortDescription()),
                                String.valueOf(contract_address), String.valueOf((lot.getCustomer()==null) ?  "" : lot.getCustomer().getCustomerInn()),
                                String.valueOf(contract_protokol_num), String.valueOf(contract_protokol_date),
                                String.valueOf(protocol_made_by), String.valueOf(lot.getFactPrice()), tempText, subscriber), 0);
                    }
                }
            }
        }
        List<XWPFTable> tableList = docx.getTables();

        for (XWPFTable tbl : tableList) {

            for (XWPFTableRow row : tbl.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {

                        List<XWPFRun> runs = p.getRuns();
                        if (runs != null) {
                            for (XWPFRun r : runs) {
                                String text = r.getText(0);
                                if (text != null) {
                                    String tempText = "";
                                    if (!creditList.isEmpty() && text.contains("knd")) {
                                        for (Credit credit : creditList) {
                                            tempText += "№" + credit.getContractNum() + " від " + CustomDateFormats.sdfpoints.format(credit.getContractStart()) + "року,";
                                        }
                                    }

                                    r.setText(replaceRunText(text, String.valueOf(creditList.get(0).getContractNum()), contract_year, String.valueOf((lot.getCustomer()==null) ?  "" : lot.getCustomer().shortDescription()),
                                            String.valueOf(contract_address), String.valueOf((lot.getCustomer()==null) ?  "" : lot.getCustomer().getCustomerInn()),
                                            String.valueOf(contract_protokol_num), String.valueOf(contract_protokol_date),
                                            String.valueOf(protocol_made_by), String.valueOf(lot.getFactPrice()), tempText, subscriber), 0);
                                }
                            }
                        }
                    }
                }
            }
        }

        XWPFTable objTable = tableList.get(0);
        int i = 0;
        for (Credit credit : creditList) {
            i++;
            String creditStartDate = "";
            try {
                creditStartDate = CustomDateFormats.sdfpoints.format(credit.getContractStart());
            } catch (NullPointerException e) {
            }
            XWPFTableRow newRow = objTable.createRow();
            newRow.getCell(0).setText(i + ".");
            newRow.getCell(1).setText("Кредитний договір №"+credit.getContractNum()+ " від " + creditStartDate + " року");

        }
        objTable.setInsideVBorder(XWPFTable.XWPFBorderType.SINGLE, 2, 0, "000000");
        objTable.setInsideHBorder(XWPFTable.XWPFBorderType.SINGLE, 2, 0, "000000");
        objTable.removeRow(1);

        String fileName = "C:\\projectFiles\\Lot_Credits_Docs\\Dogovir_Akt_" + lot.getId() + ".docx";
        OutputStream fileOut = new FileOutputStream(fileName);

        docx.write(fileOut);
        fileOut.close();
        return fileName;
    }

    private String assetContract_Akt(Lot lot, String contract_year, String contract_address, String contract_protokol_num,
                                     String contract_protokol_date, String protocol_made_by, String subscriber, String pass_seria, String pass_num, String pass_vidano, String pass_vidano_date, String operates_basis, String account_bank,
                                     String bid_enter, String bid_client, String signer_bank_fio, String signer_bank_text) throws IOException {

        List<Asset> assetList = lotService.getAssetsByLot(lot);

        String bidDate;
        try {
            bidDate = CustomDateFormats.sdfpoints.format(lot.getBid().getBidDate());
        } catch (NullPointerException e) {
            bidDate = "дата торгів";
        }

        InputStream fs = new FileInputStream("C:\\projectFiles\\Lot_Assets_Docs\\Dogovir_Akt.docx");

        XWPFDocument docx = new XWPFDocument(fs);

        List<XWPFTable> tableList = docx.getTables();

        XWPFTable objTable = tableList.get(0);
        int i = assetList.size();

        XWPFTableRow totalRow = objTable.getRow(1);
        totalRow.getCell(0).setText("Основні засоби в кількості " + i + " одиниць загальною вартістю");
        try {
            totalRow.getCell(1).setText(String.valueOf(lot.getFactPrice()));
        } catch (NullPointerException npl) {
            totalRow.getCell(1).setText("0");
        }

        for (Asset asset : assetList) {

            XWPFTableRow newRow = objTable.insertNewTableRow(1);
            newRow.createCell().setText(i + ".");
            newRow.createCell().setText(asset.getInn());
            newRow.createCell().setText(asset.getAsset_name());
            newRow.createCell().setText(asset.getAsset_descr());
            newRow.createCell().setText(asset.getAddress());
            if (asset.getFactPrice() != null)
                newRow.createCell().setText(String.valueOf(asset.getFactPrice()));
            i--;
        }
//

        objTable.setInsideVBorder(XWPFTable.XWPFBorderType.SINGLE, 2, 0, "000000");
        objTable.setInsideHBorder(XWPFTable.XWPFBorderType.SINGLE, 2, 0, "000000");
        //objTable.removeRow(1);

        for (XWPFParagraph p : docx.getParagraphs()) {

            List<XWPFRun> runs = p.getRuns();
            if (runs != null) {
                for (XWPFRun r : runs) {
                    String text = r.getText(0);
                    if (text != null) {
                        r.setText(replaceRunTextAssets(text, bidDate, assetList.size(), lot.getLotNum(), contract_year, String.valueOf((lot.getCustomer()==null) ?  "" : lot.getCustomer().shortDescription()),
                                String.valueOf(contract_address), String.valueOf((lot.getCustomer()==null) ?  "" : lot.getCustomer().getCustomerInn()),
                                String.valueOf(contract_protokol_num), String.valueOf(contract_protokol_date),
                                String.valueOf(protocol_made_by), String.valueOf(lot.getFactPrice()), subscriber,
                                pass_seria, pass_num, pass_vidano, pass_vidano_date, operates_basis, account_bank,
                                bid_enter,
                                bid_client,
                                signer_bank_fio,
                                signer_bank_text,
                                "0", "0", "0", "0"), 0);
                    }
                }
            }
        }

        for (XWPFTable tbl : tableList) {

            for (XWPFTableRow row : tbl.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {

                        List<XWPFRun> runs = p.getRuns();
                        if (runs != null) {
                            for (XWPFRun r : runs) {
                                String text = r.getText(0);
                                if (text != null) {
                                    r.setText(replaceRunTextAssets(text, bidDate, assetList.size(), lot.getLotNum(), contract_year, String.valueOf((lot.getCustomer()==null) ?  "" : lot.getCustomer().shortDescription()),
                                            String.valueOf(contract_address), String.valueOf((lot.getCustomer()==null) ?  "" : lot.getCustomer().getCustomerInn()),
                                            String.valueOf(contract_protokol_num), String.valueOf(contract_protokol_date),
                                            String.valueOf(protocol_made_by), String.valueOf(lot.getFactPrice()), subscriber,
                                            pass_seria, pass_num, pass_vidano, pass_vidano_date, operates_basis,
                                            account_bank, bid_enter, bid_client, signer_bank_fio, signer_bank_text,
                                            "0", "0", "0", "0"), 0);
                                }
                            }
                        }
                    }
                }
            }
        }

        String fileName = "C:\\projectFiles\\Lot_Assets_Docs\\Dogovir_Akt_" + lot.getLotNum() + " (" + lot.getId() + ").docx";
        OutputStream fileOut = new FileOutputStream(fileName);

        docx.write(fileOut);
        fileOut.close();
        return fileName;
    }

    private String creditContract_Dodatok1(Lot lot, String contract_year, String contract_address, String contract_protokol_num, String contract_protokol_date, String protocol_made_by, String subscriber) throws IOException {

        List<Credit> creditList = lotService.getCRDTSByLot(lot);

        InputStream fs = new FileInputStream("C:\\projectFiles\\Lot_Credits_Docs\\Dogovir_Dodatok1.docx");

        XWPFDocument docx = new XWPFDocument(fs);

        for (XWPFParagraph p : docx.getParagraphs()) {

            List<XWPFRun> runs = p.getRuns();
            if (runs != null) {
                for (XWPFRun r : runs) {
                    String text = r.getText(0);
                    if (text != null) {
                        String tempText = "";
                        if (!creditList.isEmpty() && text.contains("knd")) {
                            for (Credit credit : creditList) {
                                tempText += "№" + credit.getContractNum() + " від " + CustomDateFormats.sdfpoints.format(credit.getContractStart()) + "року,";
                            }
                        }
                        r.setText(replaceRunText(text, String.valueOf(creditList.get(0).getContractNum()), contract_year, String.valueOf((lot.getCustomer()==null) ?  "" : lot.getCustomer().shortDescription()),
                                String.valueOf(contract_address), String.valueOf((lot.getCustomer()==null) ?  "" : lot.getCustomer().getCustomerInn()),
                                String.valueOf(contract_protokol_num), String.valueOf(contract_protokol_date),
                                String.valueOf(protocol_made_by), String.valueOf(lot.getFactPrice()), tempText, subscriber), 0);
                    }
                }
            }
        }
        List<XWPFTable> tableList = docx.getTables();

        for (XWPFTable tbl : tableList) {

            for (XWPFTableRow row : tbl.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {

                        List<XWPFRun> runs = p.getRuns();
                        if (runs != null) {
                            for (XWPFRun r : runs) {
                                String text = r.getText(0);
                                if (text != null) {
                                    String tempText = "";
                                    if (!creditList.isEmpty() && text.contains("knd")) {
                                        for (Credit credit : creditList) {
                                            tempText += "№" + credit.getContractNum() + " від " + CustomDateFormats.sdfpoints.format(credit.getContractStart()) + "року,";
                                        }
                                    }

                                    r.setText(replaceRunText(text, String.valueOf(creditList.get(0).getContractNum()), contract_year, String.valueOf((lot.getCustomer()==null) ?  "" : lot.getCustomer().shortDescription()),
                                            String.valueOf(contract_address), String.valueOf((lot.getCustomer()==null) ?  "" : lot.getCustomer().getCustomerInn()),
                                            String.valueOf(contract_protokol_num), String.valueOf(contract_protokol_date),
                                            String.valueOf(protocol_made_by), String.valueOf(lot.getFactPrice()), tempText, subscriber), 0);
                                }
                            }
                        }
                    }
                }
            }
        }

        XWPFTable objTable = tableList.get(0);
        int i = creditList.size();
        for (Credit credit : creditList) {
            XWPFTableRow newRow = objTable.insertNewTableRow(2);
            newRow.createCell().setText(i + ".");
            newRow.createCell().setText("Позичальник – " + credit.getFio() + ", РНОКПП " + credit.getInn());
            String creditStartDate = "";
            try {
                creditStartDate = CustomDateFormats.sdfpoints.format(credit.getContractStart());
            } catch (NullPointerException e) {
            }
            newRow.createCell().setText("Кредитний договір " + credit.getContractNum() + " від " + creditStartDate);
            i--;
        }
        objTable.removeRow(1);

        String fileName = "C:\\projectFiles\\Lot_Credits_Docs\\Dogovir_Dodatok1_" + lot.getId() + ".docx";
        try(OutputStream fileOut = new FileOutputStream(fileName)){
            docx.write(fileOut);
        }
        return fileName;
    }

    private String creditContract_Dodatok2(Lot lot, String contract_year, String contract_address, String contract_protokol_num, String contract_protokol_date, String protocol_made_by, String subscriber) throws IOException {

        List<Credit> creditList = lotService.getCRDTSByLot(lot);

        InputStream fs = new FileInputStream("C:\\projectFiles\\Lot_Credits_Docs\\Dogovir_Dodatok2.docx");

        XWPFDocument docx = new XWPFDocument(fs);

        for (XWPFParagraph p : docx.getParagraphs()) {

            List<XWPFRun> runs = p.getRuns();
            if (runs != null) {
                for (XWPFRun r : runs) {
                    String text = r.getText(0);
                    if (text != null) {
                        StringBuilder tempText = new StringBuilder();
                        if (!creditList.isEmpty() && text.contains("knd")) {
                            for (Credit credit : creditList) {
                                tempText
                                        .append("№")
                                        .append(credit.getContractNum())
                                        .append(" від ")
                                        .append(CustomDateFormats.sdfpoints.format(credit.getContractStart()))
                                        .append("року,");
                            }
                        }
                        r.setText(replaceRunText(text, String.valueOf(creditList.get(0).getContractNum()), contract_year, String.valueOf((lot.getCustomer()==null) ?  "" : lot.getCustomer().shortDescription()),
                                String.valueOf(contract_address), String.valueOf((lot.getCustomer()==null) ?  "" : lot.getCustomer().getCustomerInn()),
                                String.valueOf(contract_protokol_num), String.valueOf(contract_protokol_date),
                                String.valueOf(protocol_made_by), String.valueOf(lot.getFactPrice()), tempText.toString(), subscriber), 0);
                    }
                }
            }
        }
        List<XWPFTable> tableList = docx.getTables();

        for (XWPFTable tbl : tableList) {

            for (XWPFTableRow row : tbl.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {

                        List<XWPFRun> runs = p.getRuns();
                        if (runs != null) {
                            for (XWPFRun r : runs) {
                                String text = r.getText(0);
                                if (text != null) {
                                    String tempText = "";
                                    if (!creditList.isEmpty() && text.contains("knd")) {
                                        for (Credit credit : creditList) {
                                            tempText += "№" + credit.getContractNum() + " від " + CustomDateFormats.sdfpoints.format(credit.getContractStart()) + "року,";
                                        }
                                    }

                                    r.setText(replaceRunText(text, String.valueOf(creditList.get(0).getContractNum()), contract_year, String.valueOf((lot.getCustomer()==null) ?  "" : lot.getCustomer().shortDescription()),
                                            String.valueOf(contract_address), String.valueOf((lot.getCustomer()==null) ?  "" : lot.getCustomer().getCustomerInn()),
                                            String.valueOf(contract_protokol_num), String.valueOf(contract_protokol_date),
                                            String.valueOf(protocol_made_by), String.valueOf(lot.getFactPrice()), tempText, subscriber), 0);
                                }
                            }
                        }
                    }
                }
            }
        }

        String fileName = "C:\\projectFiles\\Lot_Credits_Docs\\Dogovir_Dodatok2_" + lot.getId() + ".docx";

        try(OutputStream fileOut = new FileOutputStream(fileName)){
            docx.write(fileOut);
        }

        return fileName;
    }

    private String makeContract(Long lotId, String contract_year, String contract_address, String contract_protokol_num, String contract_protokol_date, String protocol_made_by, String subscriber) throws Exception {
        Lot lot = lotService.getLot(lotId);
        return creditContract(lot, contract_year, contract_address, contract_protokol_num, contract_protokol_date, protocol_made_by, subscriber);
    }

    private String makeAssetContract(Long lotId, String contract_year, String contract_address, String contract_protokol_num, String contract_protokol_date, String protocol_made_by, String subscriber,
                                     String pass_seria, String pass_num, String pass_vidano, String pass_vidano_date, String operates_basis, String account_bank,
                                     String bid_enter, String bid_client, String signer_bank) throws Exception {
        Lot lot = lotService.getLot(lotId);
        SignerBank signerBank;
        switch (signer_bank) {
            case "1":
                signerBank = SignerBank.Kulibaba;
                break;
            case "2":
                signerBank = SignerBank.Glushchenko;
                break;
            case "3":
                signerBank = SignerBank.Strukova;
                break;
            default:
                signerBank = SignerBank.Glushchenko;
                break;
        }
        return assetContract(lot, contract_year, contract_address, contract_protokol_num, contract_protokol_date, protocol_made_by, subscriber,
                pass_seria, pass_num, pass_vidano, pass_vidano_date, operates_basis, account_bank,
                bid_enter, bid_client, signerBank.getFio(), signerBank.getText());// assetContract();
    }

    private String makeContract_Akt(Long lotId, String contract_year, String contract_address, String contract_protokol_num, String contract_protokol_date, String protocol_made_by, String subscriber) throws Exception {
        Lot lot = lotService.getLot(lotId);
        return creditContract_Akt(lot, contract_year, contract_address, contract_protokol_num, contract_protokol_date, protocol_made_by, subscriber);
    }

    private String makeAssetContract_Akt(Long lotId, String contract_year, String contract_address, String contract_protokol_num, String contract_protokol_date, String protocol_made_by, String subscriber,
                                         String pass_seria, String pass_num, String pass_vidano, String pass_vidano_date, String operates_basis, String account_bank,
                                         String bid_enter, String bid_client, String signer_bank) throws Exception {
        Lot lot = lotService.getLot(lotId);
        SignerBank signerBank;
        switch (signer_bank) {
            case "1":
                signerBank = SignerBank.Kulibaba;
                break;
            case "2":
                signerBank = SignerBank.Glushchenko;
                break;
            case "3":
                signerBank = SignerBank.Strukova;
                break;
            default:
                signerBank = SignerBank.Glushchenko;
                break;
        }
        return assetContract_Akt(lot, contract_year, contract_address, contract_protokol_num, contract_protokol_date,
                protocol_made_by, subscriber, pass_seria, pass_num, pass_vidano, pass_vidano_date, operates_basis, account_bank,
                bid_enter, bid_client, signerBank.getFio(), signerBank.getText());
    }

    private String makeContract_Dodatok1(Long lotId, String contract_year, String contract_address, String contract_protokol_num, String contract_protokol_date, String protocol_made_by, String subscriber) throws Exception {
        Lot lot = lotService.getLot(lotId);
        return creditContract_Dodatok1(lot, contract_year, contract_address, contract_protokol_num, contract_protokol_date, protocol_made_by, subscriber);
    }

    private String makeContract_Dodatok2(Long lotId, String contract_year, String contract_address, String contract_protokol_num, String contract_protokol_date, String protocol_made_by, String subscriber) throws Exception {
        Lot lot = lotService.getLot(lotId);
        return creditContract_Dodatok2(lot, contract_year, contract_address, contract_protokol_num, contract_protokol_date, protocol_made_by, subscriber);
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
        if (count != null)
            countSumList.add(count.toString());
        if (sum != null)
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

    @RequestMapping(value = "/addPayToLot/{lotId}", method = RequestMethod.POST)
    private @ResponseBody
    String addPayToLot(HttpSession session,
                       @PathVariable("lotId") Long idLot,
                       @RequestParam("payDate") String payDate,
                       @RequestParam("pay") BigDecimal pay,
                       @RequestParam("paySource") String paySource) {
        String login = (String) session.getAttribute("userId");
        Date date;
        try {
            date = CustomDateFormats.sdfshort.parse(payDate);
        } catch (ParseException e) {
            return "0";
        }
        Lot lot = lotService.getLot(idLot);
        Pay payment = new Pay(lot, date, pay, paySource);
        payService.createPay(payment);

        BigDecimal totalLotSum = paySource.equals("Біржа") ? payService.sumByLotFromBid(idLot) : payService.sumByLotFromCustomer(idLot);

        if (lot.getLotType() == 1) {
            List<Asset> assetsByLot = lotService.getAssetsByLot(idLot);
            BigDecimal lotFactPrice = lot.getFactPrice();

            BigDecimal assetsTotalPays = new BigDecimal(0.00);

            for (int i = 0; i < assetsByLot.size(); i++) {

                Asset asset = assetsByLot.get(i);

                BigDecimal coeff = MathCalculationUtil.getCoefficient(asset.getFactPrice(), lotFactPrice); //виправити!!!!

                BigDecimal payByAsset = (i == assetsByLot.size() - 1) ? totalLotSum.subtract(assetsTotalPays) : totalLotSum.multiply(coeff).setScale(2, BigDecimal.ROUND_HALF_UP);

                if (paySource.equals("Біржа")) {
                    asset.setPaysBid(payByAsset);
                    asset.setBidPayDate(date);
                } else {
                    asset.setPaysCustomer(payByAsset);
                    asset.setCustomerPayDate(date);
                }
                assetsTotalPays = assetsTotalPays.add(payByAsset);

                assetService.updateAsset(login, asset);
            }
            return "1";

        } else if (lot.getLotType() == 0) {
            List<Credit> creditsByLot = lotService.getCRDTSByLot(lot);
            BigDecimal lotFactPrice = lot.getFactPrice();

            BigDecimal assetsTotalPays = new BigDecimal(0.00);

            for (int i = 0; i < creditsByLot.size(); i++) {

                Credit credit = creditsByLot.get(i);

                BigDecimal coeff = MathCalculationUtil.getCoefficient(credit.getFactPrice(), lotFactPrice);

                BigDecimal payByAsset = (i == creditsByLot.size() - 1) ? totalLotSum.subtract(assetsTotalPays) : totalLotSum.multiply(coeff).setScale(2, BigDecimal.ROUND_HALF_UP);

                if (paySource.equals("Біржа")) {
                    credit.setPaysBid(payByAsset);
                    credit.setBidPayDate(date);
                } else {
                    credit.setPaysCustomer(payByAsset);
                    credit.setCustomerPayDate(date);
                }
                assetsTotalPays = assetsTotalPays.add(payByAsset);

                creditService.updateCredit(login, credit);
            }
            return "1";
        } else return "0";
    }

    @RequestMapping(value = "/delPay/{lotId}/{payId}", method = RequestMethod.POST)
    private @ResponseBody
    String delPay(HttpSession session, @PathVariable("payId") Long payId, @PathVariable("lotId") Long lotId) {
        String login = (String) session.getAttribute("userId");
        Pay pay = payService.getPay(payId);
        Lot lot = lotService.getLot(lotId);

        pay.setHistoryLotId(pay.getLotId());
        pay.setLotId(null);
        payService.updatePay(pay);

        BigDecimal totalLotSum = pay.getPaySource().equals("Біржа") ? payService.sumByLotFromBid(lotId) : payService.sumByLotFromCustomer(lotId);

        if (totalLotSum == null)
            totalLotSum = new BigDecimal(0);

        if (lot.getLotType() == 1) {
            List<Asset> assetsByLot = lotService.getAssetsByLot(lot);
            BigDecimal lotFactPrice = lot.getFactPrice();

            BigDecimal assetsTotalPays = new BigDecimal(0.00);

            for (int i = 0; i < assetsByLot.size(); i++) {

                Asset asset = assetsByLot.get(i);

                BigDecimal coeff = MathCalculationUtil.getCoefficient(asset.getFactPrice(), lotFactPrice); //виправити!!!!

                BigDecimal payByAsset = (i == assetsByLot.size() - 1) ? totalLotSum.subtract(assetsTotalPays) : totalLotSum.multiply(coeff).setScale(2, BigDecimal.ROUND_HALF_UP);

                if (pay.getPaySource().equals("Біржа")) {
                    asset.setPaysBid(payByAsset);
                } else {
                    asset.setPaysCustomer(payByAsset);
                }
                assetsTotalPays = assetsTotalPays.add(payByAsset);

                assetService.updateAsset(login, asset);
            }
            return "1";
        } else if (lot.getLotType() == 0) {
            List<Credit> creditsByLot = lotService.getCRDTSByLot(lot);
            BigDecimal lotFactPrice = lot.getFactPrice();

            BigDecimal assetsTotalPays = new BigDecimal(0.00);

            for (int i = 0; i < creditsByLot.size(); i++) {

                Credit credit = creditsByLot.get(i);

                BigDecimal coeff = MathCalculationUtil.getCoefficient(credit.getFactPrice(), lotFactPrice);

                BigDecimal payByAsset = (i == creditsByLot.size() - 1) ? totalLotSum.subtract(assetsTotalPays) : totalLotSum.multiply(coeff).setScale(2, BigDecimal.ROUND_HALF_UP);

                if (pay.getPaySource().equals("Біржа")) {
                    credit.setPaysBid(payByAsset);

                } else {
                    credit.setPaysCustomer(payByAsset);

                }
                assetsTotalPays = assetsTotalPays.add(payByAsset);

                creditService.updateCredit(login, credit);
            }
            return "1";
        } else return "0";
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

    @RequestMapping(value = "/getReport/{reportNum}/{start}/{end}", method = RequestMethod.GET)
    public void getReport(HttpServletResponse response, @PathVariable String start, @PathVariable String end,
                          @PathVariable int reportNum) throws IOException {

        File file = null;

        if (reportNum == 4) {
            file = reportService.fillAssTab();
        }

        Date startDate = null;
        Date endDate = null;
        try {
            startDate = CustomDateFormats.sdfshort.parse(start);
        } catch (ParseException e) {
        }
        try {
            endDate = CustomDateFormats.sdfshort.parse(end);
        } catch (ParseException e) {
        }

        List<Credit> crList = creditService.getCredits_SuccessBids(startDate, endDate);

        if (reportNum == 3) {
                file = reportService.fillSoldedCrdTab(crList);
        }

        else if (reportNum == 1) {
            file = reportService.makeDodatok(assetService.findAllSuccessBids(startDate, endDate), crList, start, end);
        }
        else if (reportNum == 2) {
            file = new File("C:\\projectFiles\\Dodatok 2_14.xls");
        }
        else if (reportNum == 5) {
            file = reportService.makePaymentsReport(payService.getPaysByDates(startDate, endDate), start, end);
        }
        else if (reportNum == 6) {
            file = reportService.makeBidsSumReport(lotService.getLotsHistoryByBidDates(startDate, endDate), lotService.getLotsHistoryAggregatedByBid(startDate, endDate));
        }
        else if (reportNum == 7) {

        }
        else if (reportNum == 8) {
            file = reportService.fillCreditsReestr(lotService.getSoldedWithoutDealLots(0, startDate, endDate));
        }

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

        try(InputStream is = new FileInputStream(file);
            OutputStream os = response.getOutputStream()){
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        }
        finally {
            file.delete();
        }


    }

    /*@RequestMapping(value = "/getReport/{reportNum}/{start}/{end}", method = RequestMethod.GET)
    public void getReport(HttpServletResponse response, @PathVariable String start, @PathVariable String end,
                          @PathVariable int reportNum) throws IOException {
        String reportPath = "";

        if (reportNum == 4) {
            try {
                reportPath = fillAssTab();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        *//*if(reportNum==3){
            try {
                reportPath=fillCrdTab(creditService.getCreditsByPortion(1));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*//*

        Date startDate = null;
        Date endDate = null;
        try {
            startDate = CustomDateFormats.sdfshort.parse(start);
        } catch (ParseException e) {
        }
        try {
            endDate = CustomDateFormats.sdfshort.parse(end);
        } catch (ParseException e) {
        }
        List<Asset> assetList = assetService.findAllSuccessBids(startDate, endDate);
        List<Credit> crList = creditService.getCredits_SuccessBids(startDate, endDate);

        if (reportNum == 3) {
            try {
                reportPath = fillSoldedCrdTab(crList);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else if (reportNum == 1) {
            try {
                reportPath = reportService.makeDodatok(assetList, crList, start, end);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (reportNum == 2) {
            reportPath = "C:\\projectFiles\\Dodatok 2_14.xls";
        }
        else if (reportNum == 5) {
            reportPath = makePaymentsReport(payService.getPaysByDates(startDate, endDate), start, end);
        }
        else if (reportNum == 6) {
            try {
                reportPath = makeBidsSumReport(lotService.getLotsHistoryByBidDates(startDate, endDate), lotService.getLotsHistoryAggregatedByBid(startDate, endDate));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (reportNum == 7) {
            *//*try {
                reportPath = makeBidsSumReport(lotService.getLotsHistoryByBidDates(startDate, endDate), lotService.getLotsHistoryAggregatedByBid(startDate, endDate));
            } catch (IOException e) {
                e.printStackTrace();
            }*//*
        }
        else if (reportNum == 8) {
            reportPath = fillCreditsReestr(lotService.getSoldedWithoutDealLots(0, startDate, endDate));
        }

        File file = new File(reportPath);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

        try(InputStream is = new FileInputStream(file);
            OutputStream os = response.getOutputStream()){
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        }
        finally {
            file.delete();
        }

    }*/

    @RequestMapping(value = "/getSalesReport/{portion}/{start}/{end}", method = RequestMethod.GET)
    public void getSalesReport(HttpServletResponse response, @PathVariable String start, @PathVariable String end,
                               @PathVariable int portion) throws IOException {
        String reportPath = "";

        Date startDate = null;
        Date endDate = null;
        try {
            startDate = CustomDateFormats.sdfshort.parse(start);
        } catch (ParseException e) {
        }
        try {
            endDate = CustomDateFormats.sdfshort.parse(end);
        } catch (ParseException e) {
        }
        List<Asset> assetList = assetService.findAllSuccessBids(startDate, endDate, portion);
        List<Credit> crList = creditService.getCredits_SuccessBids(startDate, endDate);

        File file = reportService.makeDodatok(assetList, crList, start, end);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

        try(InputStream is = new FileInputStream(file);
            OutputStream os = response.getOutputStream()){
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        }
        finally {
            file.delete();
        }

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
        if (!file.isEmpty()) try {
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

            try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(uploadedFile))) {
                stream.write(bytes);
            }

            return "File " + name + " zavantajeno";

        } catch (Exception e) {
            return "Download error " + name + " => " + e.getMessage();
        }
        else {
            return "Error. File not choosen.";
        }
    }

    @RequestMapping(value = "/uploadIdFileForHistory", method = RequestMethod.POST)
    private @ResponseBody
    String uploadIdFileForHistory(@RequestParam("file") MultipartFile multipartFile, Model model) throws IOException {

        List<Asset> assetList = new ArrayList<>();
        if (!multipartFile.isEmpty()) {
            File file = reportService.getTempFile(multipartFile);
            XSSFWorkbook wb;

            try {
                wb = new XSSFWorkbook(file);
                XSSFSheet sheet = wb.getSheetAt(0);
                Iterator rows = sheet.rowIterator();
                while (rows.hasNext()) {

                    XSSFRow row = (XSSFRow) rows.next();
                    XSSFCell cell = row.getCell(0);

                    DataFormatter formatter = new DataFormatter();
                    String inn = formatter.formatCellValue(cell);

                    assetList.addAll(assetService.getAllAssetsByInNum(inn));
                }

            } catch (Exception e) {
                return "4";
            }
            String reportPath = makeHistoryReportByAssets(assetList);
            model.addAttribute("reportPath", reportPath);

            return "1";
        } else return "0";
    }

    @RequestMapping(value = "/uploadIdFile", method = RequestMethod.POST)
    private @ResponseBody
    List uploadIdFile(@RequestParam("file") MultipartFile multipartFile,
                      @RequestParam("idType") int idType) throws IOException {

        File file = reportService.getTempFile(multipartFile);
        if (idType == 1) {
            List<Asset> assetList = new ArrayList<>();

            if (!multipartFile.isEmpty()) {

                XSSFWorkbook wb;

                try {
                    wb = new XSSFWorkbook(file);
                } catch (Exception e) {
                    return null;
                }
                XSSFSheet sheet = wb.getSheetAt(0);
                Iterator rows = sheet.rowIterator();
                while (rows.hasNext()) {

                    XSSFRow row = (XSSFRow) rows.next();
                    XSSFCell cell = row.getCell(0);

                    DataFormatter formatter = new DataFormatter();
                    String inn = formatter.formatCellValue(cell);

                    assetList.addAll(assetService.getAllAssetsByInNum(inn));
                }
                return assetList;

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
        File file = reportService.getTempFile(multipartFile);
        if (idType == 1) {
            List<Asset> assetList;

            if (!multipartFile.isEmpty()) {
                XSSFWorkbook wb;

                try {
                    wb = new XSSFWorkbook(file);
                } catch (Exception e) {
                    return null;
                }
                XSSFSheet sheet = wb.getSheetAt(0);
                Iterator rows = sheet.rowIterator();
                while (rows.hasNext()) {

                    XSSFRow row = (XSSFRow) rows.next();
                    XSSFCell cell = row.getCell(0);

                    DataFormatter formatter = new DataFormatter();
                    String inn = formatter.formatCellValue(cell);

                    Double accPrice = row.getCell(1).getNumericCellValue();
                    assetList = assetService.getAllAssetsByInNum(inn);
                    assetList.forEach(asset -> asset.setAcceptPrice(BigDecimal.valueOf(accPrice)));
                    assetList.forEach(asset -> assetService.updateAsset(login, asset));
                }
                return "1";

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

                        XSSFCell cell = row.getCell(0);

                        DataFormatter formatter = new DataFormatter();
                        String inn = formatter.formatCellValue(cell);

                        Double accPrice = row.getCell(1).getNumericCellValue();
                        List<Credit> creditList = creditService.getCreditsByIdBars(Long.parseLong(inn));
                        creditList.forEach(credit -> credit.setAcceptPrice(BigDecimal.valueOf(accPrice)));
                        creditList.forEach(credit -> creditService.updateCredit(login, credit));
                    }
                    return "1";
                } catch (FileNotFoundException fnfe) {
                    return "0";
                } catch (IOException ioe) {
                    return "0";
                } catch (Exception e) {
                    return "0";
                }
            } else return "0";
        } else
            return "0";
    }

    @RequestMapping(value = "/{bidN}/setLotsToBid", method = RequestMethod.POST)
    private @ResponseBody
    String setLotsToBid(HttpSession session,
                        @RequestParam("file_lots") MultipartFile multipartFile,
                        @PathVariable("bidN") long bidId)
        /*@RequestParam("bidN") long bidId)*/ throws IOException {

        Bid bid = bidService.getBid(bidId);
        System.out.println(bid);

        String login = (String) session.getAttribute("userId");
        File file = reportService.getTempFile(multipartFile);

        if (!multipartFile.isEmpty()) {
            XSSFWorkbook wb;

            try {
                wb = new XSSFWorkbook(file);
            } catch (Exception e) {
                return "-1";
            }
            XSSFSheet sheet = wb.getSheetAt(0);
            Iterator rows = sheet.rowIterator();

            int i = 0;
            while (rows.hasNext()) {

                XSSFRow row = (XSSFRow) rows.next();

                String lotNum = row.getCell(0).getStringCellValue();

                Lot lot = lotService.getLotByLotNum(lotNum);
                lot.setBid(bid);
                lotService.updateLot(login, lot);
                i++;

            }
            return String.valueOf(i);

        } else return "0";

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

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

        try(
        InputStream is = new FileInputStream(file);
        OutputStream os = response.getOutputStream()
        ){
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        }
        finally {
            file.delete();
        }
    }

    @RequestMapping(value = "/unitsByLot/{id}", method = RequestMethod.GET)
    public void getCreditsByLot(HttpServletResponse response, @PathVariable Long id) throws IOException {

        Lot lot = lotService.getLot(id);

        File file = null;

        if (lot.getLotType() == 0) {
            file = Excel.loadCreditsByLot(lot, creditService.getCrditsByLotId(id));
        } else if (lot.getLotType() == 1) {
            file = Excel.loadAssetsByList(lot, lotService.getAssetsByLot(lot));
        }

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

        try(InputStream is = new FileInputStream(file);
            OutputStream os = response.getOutputStream()){
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        }
        finally {
            file.delete();
        }

    }

    @RequestMapping(value = "/downloadT/{id}", method = RequestMethod.GET)
    public void downloadT(HttpServletResponse response, @PathVariable Long id) throws IOException {

        Bid bid = bidService.getBid(id);

        List<Lot> lotList;
        List<Asset> assetList;

        lotList = lotService.getLotsByBid(bid);
        assetList = bidService.getAssetsByBid(bid);

        String filePath = Excel.loadCreditsByList(lotList, assetList);

        File file = new File(filePath);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

        try(InputStream is = new FileInputStream(file);
            OutputStream os = response.getOutputStream()){
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        }
        finally {
            file.delete();
        }

    }

    @RequestMapping(value = "/reportDownload", method = RequestMethod.GET)
    public void reportDownload(HttpServletResponse response, HttpSession session) throws IOException {
        String reportPath = (String) session.getAttribute("reportPath");
        File file = new File(reportPath);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

        try(InputStream is = new FileInputStream(file);
            OutputStream os = response.getOutputStream()){
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        }
        finally {
            file.delete();
        }
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

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

        try(InputStream is = new FileInputStream(file);
            OutputStream os = response.getOutputStream()){
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        }
        finally {
            file.delete();
        }
    }

    @RequestMapping(value = "/downloadOgolosh/{id}", method = RequestMethod.GET)
    public void downloadOgoloshennya(HttpServletResponse response, @PathVariable Long id) throws IOException {

        File file;
        String docName = reportService.makeOgoloshennya(id);
        file = new File(docName);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

        try(InputStream is = new FileInputStream(file);
            OutputStream os = response.getOutputStream()){
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        }
        finally {
            file.delete();
        }
    }

    @RequestMapping(value = "/downloadCreditContract/{lotId}/{contract_year}/{contract_address}/{contract_protokol_num}/{contract_protokol_date}/{protocol_made_by}/{subscriber}",
            method = RequestMethod.GET)
    public void downloadCreditContract(HttpServletResponse response,
                                       @PathVariable Long lotId,
                                       @PathVariable String contract_year,
                                       @PathVariable String contract_address,
                                       @PathVariable String contract_protokol_num,
                                       @PathVariable String contract_protokol_date,
                                       @PathVariable String protocol_made_by,
                                       @PathVariable String subscriber) throws Exception {

        File file;
        file = new File(makeContract(lotId, contract_year, contract_address, contract_protokol_num, contract_protokol_date, protocol_made_by, subscriber));

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

        try(InputStream is = new FileInputStream(file);
            OutputStream os = response.getOutputStream()){
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        }
        finally {
            file.delete();
        }
    }

    @RequestMapping(value = "/downloadAssetContract/{lotId}/{contract_year}/{contract_address}/{contract_protokol_num}/{contract_protokol_date}/{protocol_made_by}/{subscriber}/{pass_seria}/{pass_num}/{pass_vidano}/{pass_vidano_date}/{operates_basis}/{account_bank}/{bid_enter}/{bid_client}/{signer_bank}",
            method = RequestMethod.GET)
    public void downloadAssetContract(HttpServletResponse response,
                                      @PathVariable Long lotId,
                                      @PathVariable String contract_year,
                                      @PathVariable String contract_address,
                                      @PathVariable String contract_protokol_num,
                                      @PathVariable String contract_protokol_date,
                                      @PathVariable String protocol_made_by,
                                      @PathVariable String subscriber,
                                      @PathVariable String pass_seria,
                                      @PathVariable String pass_num,
                                      @PathVariable String pass_vidano,
                                      @PathVariable String pass_vidano_date,
                                      @PathVariable String operates_basis,
                                      @PathVariable String account_bank,
                                      @PathVariable String bid_enter,
                                      @PathVariable String bid_client,
                                      @PathVariable String signer_bank) throws Exception {
        File file;
        file = new File(makeAssetContract(lotId, contract_year, contract_address, contract_protokol_num, contract_protokol_date, protocol_made_by, subscriber,
                pass_seria,
                pass_num,
                pass_vidano,
                pass_vidano_date,
                operates_basis,
                account_bank,
                bid_enter,
                bid_client,
                signer_bank
        ));

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

        try(InputStream is = new FileInputStream(file);
            OutputStream os = response.getOutputStream()){
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        }
        finally {
            file.delete();
        }
    }

    @RequestMapping(value = "/downloadContract_Akt/{lotId}/{contract_year}/{contract_address}/{contract_protokol_num}/{contract_protokol_date}/{protocol_made_by}/{subscriber}",
            method = RequestMethod.GET)
    public void downloadContract_Akt(HttpServletResponse response,
                                     @PathVariable Long lotId,
                                     @PathVariable String contract_year,
                                     @PathVariable String contract_address,
                                     @PathVariable String contract_protokol_num,
                                     @PathVariable String contract_protokol_date,
                                     @PathVariable String protocol_made_by,
                                     @PathVariable String subscriber) throws Exception {

        File file;
        file = new File(makeContract_Akt(lotId, contract_year, contract_address, contract_protokol_num, contract_protokol_date, protocol_made_by, subscriber));

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

        try(InputStream is = new FileInputStream(file);
            OutputStream os = response.getOutputStream()){
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        }
        finally {
            file.delete();
        }
    }

    @RequestMapping(value = "/downloadAssetContract_Akt/{lotId}/{contract_year}/{contract_address}/{contract_protokol_num}/{contract_protokol_date}/{protocol_made_by}/{subscriber}/{pass_seria}/{pass_num}/{pass_vidano}/{pass_vidano_date}/{operates_basis}/{account_bank}/{bid_enter}/{bid_client}/{signer_bank}",
            method = RequestMethod.GET)
    public void downloadAssetContract_Akt(HttpServletResponse response,
                                          @PathVariable Long lotId,
                                          @PathVariable String contract_year,
                                          @PathVariable String contract_address,
                                          @PathVariable String contract_protokol_num,
                                          @PathVariable String contract_protokol_date,
                                          @PathVariable String protocol_made_by,
                                          @PathVariable String subscriber,
                                          @PathVariable String pass_seria,
                                          @PathVariable String pass_num,
                                          @PathVariable String pass_vidano,
                                          @PathVariable String pass_vidano_date,
                                          @PathVariable String operates_basis,
                                          @PathVariable String account_bank,
                                          @PathVariable String bid_enter,
                                          @PathVariable String bid_client,
                                          @PathVariable String signer_bank
    ) throws Exception {

        File file;
        file = new File(makeAssetContract_Akt(lotId, contract_year, contract_address, contract_protokol_num, contract_protokol_date, protocol_made_by, subscriber,
                pass_seria, pass_num, pass_vidano, pass_vidano_date, operates_basis, account_bank,
                bid_enter, bid_client, signer_bank));

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

        try(InputStream is = new FileInputStream(file);
            OutputStream os = response.getOutputStream()){
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        }
        finally {
            file.delete();
        }
    }

    @RequestMapping(value = "/downloadContract_Dodatok/{dodatok_num}/{lotId}/{contract_year}/{contract_address}/{contract_protokol_num}/{contract_protokol_date}/{protocol_made_by}/{subscriber}",
            method = RequestMethod.GET)
    public void downloadContractDodatok(HttpServletResponse response,
                                        @PathVariable Long dodatok_num,
                                        @PathVariable Long lotId,
                                        @PathVariable String contract_year,
                                        @PathVariable String contract_address,
                                        @PathVariable String contract_protokol_num,
                                        @PathVariable String contract_protokol_date,
                                        @PathVariable String protocol_made_by,
                                        @PathVariable String subscriber) throws Exception {

        File file;
        if (dodatok_num == 1)
            file = new File(makeContract_Dodatok1(lotId, contract_year, contract_address, contract_protokol_num, contract_protokol_date, protocol_made_by, subscriber));
        else if (dodatok_num == 2)
            file = new File(makeContract_Dodatok2(lotId, contract_year, contract_address, contract_protokol_num, contract_protokol_date, protocol_made_by, subscriber));
        else return;

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

        try(InputStream is = new FileInputStream(file);
            OutputStream os = response.getOutputStream()){
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        }
        finally {
            file.delete();
        }
    }

    @RequestMapping(value = "/downLotIdListForm", method = RequestMethod.GET)
    public void downLotIdListForm(HttpServletResponse response) throws IOException {

        File file = new File("C:\\projectFiles\\LOT_ID_LIST.xlsx");

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

        try(InputStream is = new FileInputStream(file);
            OutputStream os = response.getOutputStream()){
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        }
    }
    //LOT_ID_LIST.xlsx

    public void setFactPriceFromLotToCredits(Lot lot, BigDecimal factLotPrice, String login) {
        List<Credit> credits = lotService.getCRDTSByLot(lot);
        if (lot.getFactPrice() != null && lot.getFactPrice().equals(factLotPrice)) {
            return;
        }
        if (factLotPrice == null) {
            for (Credit credit : credits) {
                credit.setFactPrice(null);
                creditService.updateCredit(login, credit);
            }
        } else if (!factLotPrice.equals(BigDecimal.valueOf(0.00))) {
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

    public void setFactPriceFromLotToAssets(Lot lot, BigDecimal factLotPrice, String login) {
        List<Asset> assets = lotService.getAssetsByLot(lot);

        if (factLotPrice == null) {
            for (Asset asset : assets) {
                asset.setFactPrice(null);
                assetService.updateAsset(login, asset);
            }
        } else if (!factLotPrice.equals(new BigDecimal(0.00))) {
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

    public void setFirstStartPriceFromLotToCredits(Lot lot, BigDecimal firstStartLotPrice, String login) {
        List<Credit> credits = lotService.getCRDTSByLot(lot);
        if (firstStartLotPrice != null && lot.getFirstStartPrice() != null && lot.getFirstStartPrice().equals(firstStartLotPrice)) {
            return;
        }
        if (firstStartLotPrice == null || firstStartLotPrice.equals(new BigDecimal(0.00))) {
            for (Credit credit : credits) {
                credit.setFirstStartPrice(firstStartLotPrice);
                creditService.updateCredit(login, credit);
            }
        } else {
            BigDecimal lotSum = lotService.lotSum(lot);
            BigDecimal crTotalFirstPrice = new BigDecimal(0.00);
            for (int i = 0; i < credits.size(); i++) {
                Credit credit = credits.get(i);
                BigDecimal firstPrice;
                if (i == credits.size() - 1) {
                    firstPrice = firstStartLotPrice.subtract(crTotalFirstPrice);
                } else {
                    firstPrice = (credit.getRv().divide(lotSum, 10, BigDecimal.ROUND_HALF_UP)).multiply(firstStartLotPrice).setScale(2, BigDecimal.ROUND_HALF_UP);
                    crTotalFirstPrice = crTotalFirstPrice.add(firstPrice);
                }
                credit.setFirstStartPrice(firstPrice);
                creditService.updateCredit(login, credit);
            }
        }
    }

    //можно улучшить
    public void setStartPriceFromLotToCredits(Lot lot, BigDecimal startLotPrice, String login) {
        List<Credit> credits = lotService.getCRDTSByLot(lot);
        if (lot.getStartPrice() != null && lot.getStartPrice().equals(startLotPrice)) {
            return;
        }
        if (startLotPrice == null || startLotPrice.equals(new BigDecimal(0.00))) {
            for (Credit credit : credits) {
                credit.setStartPrice(startLotPrice);
                creditService.updateCredit(login, credit);
            }
        } else {
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
    private @ResponseBody
    String changeLotParams(HttpSession session,
                           @RequestParam("lotId") String lotId,
                           @RequestParam("lotNum") String lotNum,
                           @RequestParam("workStage") String status,
                           @RequestParam("comment") String comment,
                           @RequestParam("bidStage") String bidStage,
                           @RequestParam("resultStatus") String resultStatus,
                           /*@RequestParam("customer") String customer,
                           @RequestParam("customerInn") String customerInn,*/
                           @RequestParam("firstPrice") BigDecimal firstPrice,
                           @RequestParam("startPrice") BigDecimal startPrice,
                           @RequestParam("factPrice") BigDecimal factLotPrice,
                           @RequestParam("isSold") String isSold,
                           @RequestParam("selectedBidId") Long selectedBidId,
                           @RequestParam("countOfParticipants") int countOfParticipants,
                           @RequestParam("bidScenario") short bidScenario) {
        String login = (String) session.getAttribute("userId");
        Lot lot = lotService.getLot(Long.parseLong(lotId));
        lot.setLotNum(lotNum);
        lot.setWorkStage(status);
        lot.setComment(comment);
        lot.setBidStage(bidStage);
        lot.setStatus(resultStatus);
      /*  lot.setCustomerName(customer);

        if (customerInn.equals(""))
            lot.setCustomerInn(0L);
        else
            lot.setCustomerInn(Long.parseLong(customerInn));*/

        lot.setCountOfParticipants(countOfParticipants);
        lot.setBidScenario(bidScenario);

        if (selectedBidId == 0L) {
            lot.setBid(null);
        } else
            lot.setBid(bidService.getBid(selectedBidId));

        if (lot.getLotType() == 1 /*&& lot.getFactPrice()!=null && !lot.getFactPrice().equals(factLotPrice)*/) {

            setFactPriceFromLotToAssets(lot, factLotPrice, login);
        }
        if (lot.getLotType() == 0) {
            //   if(lot.getFirstStartPrice()!=null && !lot.getFirstStartPrice().equals(firstPrice))
            setFirstStartPriceFromLotToCredits(lot, firstPrice, login);
            //   if(lot.getStartPrice()!=null && !lot.getStartPrice().equals(startPrice))
            setStartPriceFromLotToCredits(lot, startPrice, login);
            //    if(lot.getFactPrice()!=null && !lot.getFactPrice().equals(factLotPrice))
            setFactPriceFromLotToCredits(lot, factLotPrice, login);
        }

        lot.setFirstStartPrice(firstPrice);
        lot.setStartPrice(startPrice);
        lot.setFactPrice(factLotPrice);

        if (lot.getFirstStartPrice() == null) {
            setFirstStartPriceFromLotToCredits(lot, startPrice, login);
            lot.setFirstStartPrice(startPrice);
        }


        if (isSold.equals("1")) {
            List<Credit> credits = lotService.getCRDTSByLot(lot);
            List<Asset> assetList = lotService.getAssetsByLot(lot);
            lot.setActSignedDate(new Date());
            lot.setItSold(true);
            for (Credit credit : credits) {
                credit.setSold(true);
                creditService.updateCredit(login, credit);
            }
            for (Asset asset : assetList) {
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

        if (lot.getLotType() == 0) {
            if (lot.getFirstStartPrice() == null) {
                setFirstStartPriceFromLotToCredits(lot, lot.getStartPrice(), login);
            }
            setFactPriceFromLotToCredits(lot, null, login);
        }
        if (lot.getLotType() == 1) {
            setFactPriceFromLotToAssets(lot, null, login);
        }
        if (lot.getFirstStartPrice() == null) {
            lot.setFirstStartPrice(lot.getStartPrice());
        }
//Исправить говнокод
        if (requestType == 1) {

            if (lot.getBidStage().equals(StaticStatus.bidStatusList.get(0))) {
                lot.setBidStage(StaticStatus.bidStatusList.get(1));
            } else if (lot.getBidStage().equals(StaticStatus.bidStatusList.get(1))) {
                lot.setBidStage(StaticStatus.bidStatusList.get(2));
            } else if (lot.getBidStage().equals(StaticStatus.bidStatusList.get(2))) {
                lot.setBidStage(StaticStatus.bidStatusList.get(3));
            } else if (lot.getBidStage().equals(StaticStatus.bidStatusList.get(3))) {
                lot.setBidStage(StaticStatus.bidStatusList.get(4));
            } else if (lot.getBidStage().equals(StaticStatus.bidStatusList.get(4))) {
                lot.setBidStage(StaticStatus.bidStatusList.get(5));
            } else if (lot.getBidStage().equals(StaticStatus.bidStatusList.get(5))) {
                lot.setBidStage(StaticStatus.bidStatusList.get(6));
            } else if (lot.getBidStage().equals(StaticStatus.bidStatusList.get(6))) {
                lot.setBidStage(StaticStatus.bidStatusList.get(7));
            } else if (lot.getBidStage().equals(StaticStatus.bidStatusList.get(7))) {
                lot.setBidStage(StaticStatus.bidStatusList.get(8));
            }
        } else if (requestType == 2) {
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
       // lot.setCustomerName(null);
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
            bDate = CustomDateFormats.sdfshort.parse(bidDate);
        } catch (ParseException e) {
            System.out.println("Неверный формат даты");
        }
        try {
            ND1 = CustomDateFormats.sdfshort.parse(newND1);
        } catch (ParseException e) {
            System.out.println("Неверный формат даты");
        }
        try {
            RED = CustomDateFormats.sdfshort.parse(newRED);
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
        if (idList.equals("")) {
            return "0";
        }
        String[] idMass = idList.split(",");
        session.setAttribute("assetsListToLot", idMass);
        return "1";
    }

    @RequestMapping(value = "/createLotByCheckedCredits", method = RequestMethod.POST)
    private @ResponseBody
    String createLotByCheckedCredits(@RequestParam("idList") String idList, HttpSession session) {
        if (idList.equals("")) {
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
    String sumByIDBars(@RequestParam("idMass") String ids) {
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
        String aggregatedComment = "";
        List<String> resList = new ArrayList<>();
        Bid bid = bidService.getBid(Long.parseLong(bidId));
        for (Lot l : (List<Lot>) bidService.lotsByBid(bid)) {
            aggregatedComment += l.getComment() + " ||";
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
            BigDecimal coeff = MathCalculationUtil.getCoefficient(asset.getFactPrice(), lot.getFactPrice());// asset.getFactPrice().divide(lot.getFactPrice(), 10, BigDecimal.ROUND_HALF_UP);
            BigDecimal paySumByAsset;
            try {
                paySumByAsset = lotService.paymentsSumByLot(lot).multiply(coeff).setScale(2, BigDecimal.ROUND_HALF_UP);
            } catch (NullPointerException npe) {
                paySumByAsset = BigDecimal.valueOf(0);
            }
            BigDecimal residualToPay;
            try {
                residualToPay = asset.getFactPrice().subtract(paySumByAsset);
            } catch (NullPointerException npe) {
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
        String[] idBarsMass;
        String[] innMass;
        String[] idLotMass;
        if (inIDBarses.equals("")) {
            idBarsMass = new String[0];
        } else idBarsMass = inIDBarses.split(",");

        if (inINNs.equals("")) {
            innMass = new String[0];
        } else innMass = inINNs.split(",");

        if (inIDLots.equals("")) {
            idLotMass = new String[0];
        } else idLotMass = inIDLots.split(",");
        return creditService.countOfFilteredCredits(isSold, isInLot, clientType, isNbu, isFondDec, idBarsMass, innMass, idLotMass);
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
        String[] idBarsMass;
        String[] innMass;
        String[] idLotMass;
        if (inIDBarses.equals("")) {
            idBarsMass = new String[0];
        } else idBarsMass = inIDBarses.split(",");

        if (inINNs.equals("")) {
            innMass = new String[0];
        } else innMass = inINNs.split(",");

        if (inIDLots.equals("")) {
            idLotMass = new String[0];
        } else idLotMass = inIDLots.split(",");

        List<Credit> crList = creditService.getCreditsByPortion(portionNumber, isSold, isInLot, clientType, isNbu, isFondDec, idBarsMass, innMass, idLotMass);
        List<String> rezList = new ArrayList<>();
        for (Credit cr : crList) {
            String lotId = "";

            String bidDate = "";
            String exchangeName = "";

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

            String acceptedPrice = "";
            String acceptedExchange = "";

            if (cr.getAcceptPrice() != null)
                acceptedPrice = String.valueOf(cr.getAcceptPrice());
            String actSignedDate = "";
            if (cr.getLot() != null) {
                lotId = String.valueOf(cr.getLot());
                Lot lot = lotService.getLot(cr.getLot());

                if (lot.getBid() != null) {
                    bidDate = String.valueOf(CustomDateFormats.sdfpoints.format(lot.getBid().getBidDate()));
                    exchangeName = lot.getBid().getExchange().getCompanyName();
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

                customerName = (lot.getCustomer()==null) ?  "" : lot.getCustomer().shortDescription();

                workStage = lot.getWorkStage();
                if (lot.getFondDecisionDate() != null)
                    fondDecisionDate = String.valueOf(CustomDateFormats.sdfpoints.format(lot.getFondDecisionDate()));
                fondDecision = lot.getFondDecision();
                fondDecisionNumber = lot.getDecisionNumber();
                if (lot.getNbuDecisionDate() != null)
                    nbuDecisionDate = String.valueOf(CustomDateFormats.sdfpoints.format(lot.getNbuDecisionDate()));
                nbuDecision = lot.getNbuDecision();
                nbuDecisionNumber = lot.getNbuDecisionNumber();
                acceptedExchange = lot.getAcceptExchange();

                if (lot.getActSignedDate() != null)
                    actSignedDate = CustomDateFormats.sdfpoints.format(lot.getActSignedDate());
            }
            String planSaleDate = "";
            if (cr.getPlanSaleDate() != null)
                planSaleDate = CustomDateFormats.yearMonthFormat.format(cr.getPlanSaleDate());

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
        } catch (NullPointerException e) {
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
                         @RequestParam("decNum") String decNum) {

        String login = (String) session.getAttribute("userId");

        Date date = null;
        try {
            date = CustomDateFormats.sdfshort.parse(fondDecDate);
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
    String changeNBUDec(HttpSession session,
                        @RequestParam("lotId") Long lotId,
                        @RequestParam("NBUDecDate") String nbuDecDate,
                        @RequestParam("NBUDec") String nbuDec,
                        @RequestParam("decNum") String decNum) {

        String login = (String) session.getAttribute("userId");

        Date date = null;
        try {
            date = CustomDateFormats.sdfshort.parse(nbuDecDate);
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

        return lotRid.toString();
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
            BigDecimal acceptedPr = crdt.getAcceptPrice();
            BigDecimal rv = crdt.getRv();
            if (acceptedPr != null) {
                startPrice = startPrice.add(acceptedPr);
                crdt.setFirstStartPrice(acceptedPr);
                crdt.setStartPrice(acceptedPr);
                //   creditService.updateCredit(crdt);
            } else {
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

        return lotRid.toString();
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
        for (Long lotId : lotIdList) {
            List<Bid> bidList = lotService.getLotHistoryAggregatedByBid(lotId);
            Collections.sort(bidList);
            for (Bid bid : bidList) {
                temp = asset.getInn() + "||" + lotId + "||" + bid.getExchange().getCompanyName() + "||" + CustomDateFormats.sdfshort.format(bid.getBidDate()) + "||" + assetService.getAccPriceByLotIdHistory(asset.getId(), lotId);
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
        List<Long> lotIdList = creditService.getLotIdHistoryByCredit(credit.getNd());
        for (Long lotId : lotIdList) {
            List<Bid> bidList = lotService.getLotHistoryAggregatedByBid(lotId);
            Collections.sort(bidList);
            for (Bid bid : bidList) {
                temp = credit.getInn() + "||" + lotId + "||" + bid.getExchange().getCompanyName() + "||" + CustomDateFormats.sdfshort.format(bid.getBidDate()) + "||" + creditService.getPriceByLotIdHistory(credit.getId(), lotId);
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

    @RequestMapping(value = "/{lotId}/customer/update", method = RequestMethod.POST)
    private @ResponseBody Customer updateCustomer(HttpSession session, @PathVariable Long lotId,
                                  @RequestParam("inn") Long inn,
                                  @RequestParam("name") String name,
                                  @RequestParam("middleName") String middleName,
                                  @RequestParam("lastName") String lastName,
                                  @RequestParam("isMerried") boolean isMerried,
                                  @RequestParam("type") String type,
                                                  @RequestParam("legalType") String legalType

                                  ) {

        String login = (String) session.getAttribute("userId");

        Lot lot = lotService.getLot(lotId);

        Customer customer = customerService.getCustomerByInn(inn);

        if(customer==null){

            customer = new Customer();

            customer.setCustomerInn(inn);
            customer.setCustomerName(name);
            customer.setMiddleName(middleName);
            customer.setLastName(lastName);
            customer.setMerried(isMerried);
            customer.setType(Customer.SubscriberType.valueOf(type));
            customer.setLegalType(Customer.LegalType.valueOf(legalType));

            customerService.createCustomer(customer);

            lot.setCustomer(customer);

            lotService.updateLot(login, lot);
        }
        else {
            customer.setCustomerInn(inn);
            customer.setCustomerName(name);
            customer.setMiddleName(middleName);
            customer.setLastName(lastName);
            customer.setMerried(isMerried);
            customer.setType(Customer.SubscriberType.valueOf(type));
            customer.setLegalType(Customer.LegalType.valueOf(legalType));

            customerService.updateCustomer(customer);

            if(lot.getCustomer()==null || !lot.getCustomer().equals(customer)){

                lot.setCustomer(customer);
                lotService.updateLot(login, lot);

            }
        }

        return customer;

    }

    @RequestMapping(value = "/customer/{id}", method = RequestMethod.POST)
    private @ResponseBody Customer getCustomerFromLot(@PathVariable Long id) {
        return customerService.getCustomerByInn(id);
    }

    @RequestMapping(value = "/{lotId}/customer/delete", method = RequestMethod.POST)
    private @ResponseBody String updateCustomer(@PathVariable Long lotId) {

        Lot lot = lotService.getLot(lotId);

        lot.setCustomer(null);
        lotService.updateLot(lot);

        return "успішно видалено!";
    }

    @RequestMapping(value = "/{lotId}/setDates", method = RequestMethod.POST)
    private @ResponseBody String setDates(HttpSession session, @PathVariable Long lotId,
                                          @RequestParam("dateProzoro") String dateProzoro,
                                          @RequestParam("dateDeadline") String dateDeadline ) {

        Lot lot = lotService.getLot(lotId);

        Date datePro;
        Date dateDead;

        try{
            datePro = CustomDateFormats.sdfshort.parse(dateProzoro);
        }
        catch (NullPointerException | ParseException e) {
            datePro=null;
        }
        try{
            dateDead = CustomDateFormats.sdfshort.parse(dateDeadline);
        }
        catch (NullPointerException | ParseException e) {
            dateDead=null;
        }

        lot.setProzoroDate(datePro);
        lot.setDeadlineDate(dateDead);

        lotService.updateLot(lot);

        return "ok";
    }
}