package nb.controller;

import nb.domain.Asset;
import nb.domain.Credit;
import nb.domain.Lot;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.util.DateFormatConverter;

import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

public class Excel implements Serializable {
    private static final SimpleDateFormat sdf;
    static{
        sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
    }

    public Excel() {
    }

     static String loadCreditsByList(List<Lot> lotList, List<Asset> assetList) throws IOException {
        Set<Date> bidDateSet = new TreeSet<>();
        Set<String> exNamesSet = new TreeSet<>();
        Set<Long> lotNumsSet = new TreeSet<>();

        for(Lot lot: lotList){
            if(lot.getBid()!=null&&lot.getBid().getBidDate()!=null) {
                bidDateSet.add(lot.getBid().getBidDate());
                exNamesSet.add(lot.getBid().getExchange().getCompanyName());
            }
            lotNumsSet.add(lot.getId());
        }
        String bidDates="";
        for(Date date: bidDateSet){
            bidDates +=", "+sdf.format(date);
        }
        String exNames =exNamesSet.toString();
        String lotNums =lotNumsSet.toString();

        POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream("C:\\projectFiles\\Shablon.xls"));
        HSSFWorkbook wb = new HSSFWorkbook(fs);
        HSSFSheet sheet = wb.getSheetAt(0);
        int shiftCount = assetList.size() + 6;

        sheet.getRow(1).getCell(0).setCellValue("Інформація про активи ПАТ «КБ «НАДРА», що пропонуються на продаж на аукціоні "
                +(bidDates.equals("") ? "" : bidDates.substring(2))+" р. на електронному торговому майданчику "+exNames.substring(1, exNames.length()-1));

        //задаем формат дати
        String excelFormatter = DateFormatConverter.convert(Locale.ENGLISH , "yyyy-MM-dd");
        CellStyle cellStyle = wb.createCellStyle();
        DataFormat poiFormat = wb.createDataFormat();
        cellStyle.setDataFormat(poiFormat.getFormat(excelFormatter));
        //end
        sheet.shiftRows(7, 7, assetList.size() - 1);
        int numRow = 6;
        for (Asset asset : assetList) {
            HSSFRow row = sheet.createRow(numRow);
            numRow++;
            int j = 0;
            while (j < 14) {
                row.createCell(j);
                j++;
            }
            row.getCell(0).setCellValue(asset.getId());
            row.getCell(1).setCellValue(asset.getLot().getLotNum());
            row.getCell(2).setCellValue(asset.getAsset_name());
            row.getCell(3).setCellValue(asset.getInn());
            row.getCell(4).setCellValue(asset.getRv().doubleValue());
            try {
                row.getCell(5).setCellValue(asset.getEksplDate());
            }
            catch (NullPointerException e){
            }
            row.getCell(5).setCellStyle(cellStyle);
            row.getCell(6).setCellValue(asset.getOriginalPrice().doubleValue());
            try {
                row.getCell(7).setCellValue(asset.getZb().doubleValue());
            }
            catch(Exception e){
            }
            if(asset.getLot().getBidStage()!=null){
                int bidStage = 0;
                switch (asset.getLot().getBidStage()) {
                    case "Перші торги":
                        bidStage = 1;
                        break;
                    case "Другі торги":
                        bidStage = 2;
                        break;
                    case "Треті торги":
                        bidStage = 3;
                        break;
                    case "Четверті торги":
                        bidStage = 4;
                        break;
                    case "П'яті торги":
                        bidStage = 5;
                        break;
                }
                row.getCell(8).setCellValue(bidStage);
            }
            try{
                BigDecimal discount = (asset.getLot().getFirstStartPrice()).divide(asset.getLot().getStartPrice(), 2);
                row.getCell(9).setCellValue(1-discount.doubleValue());
            }
            catch (NullPointerException e){
                row.getCell(9).setCellValue(0);
            }

            if(asset.getAcceptPrice()!=null)
                row.getCell(10).setCellFormula("L"+(numRow)+"/6*5" );
            if(asset.getAcceptPrice()!=null)
            {row.getCell(11).setCellValue(asset.getAcceptPrice().doubleValue());} //изменить!!!
            row.getCell(12).setCellValue(asset.getLot().getCountOfParticipants());
            if(asset.getFactPrice()!=null)
            {row.getCell(13).setCellValue(asset.getFactPrice().doubleValue());}
        }
        HSSFRow sumRow = sheet.getRow(6+assetList.size());
        sumRow.getCell(4).setCellFormula("SUM(E7:E" + shiftCount + ")");
        sumRow.getCell(6).setCellFormula("SUM(G7:G" + shiftCount + ")");
        sumRow.getCell(7).setCellFormula("SUM(H7:H" + shiftCount + ")");
        //sumRow.getCell(8).setCellFormula("SUM(I7:I" + shiftCount + ")");
        sumRow.getCell(10).setCellFormula("SUM(K7:K" + shiftCount + ")");
        sumRow.getCell(11).setCellFormula("SUM(L7:L" + shiftCount + ")");
        sumRow.getCell(12).setCellFormula("SUM(M7:M" + shiftCount + ")");
        sumRow.getCell(13).setCellFormula("SUM(N7:N" + shiftCount + ")");

        String fileName = "C:\\projectFiles\\"+(bidDates.equals("") ? "" : bidDates.substring(2)) + " Lot N"+lotNums.substring(1, lotNums.length()-1)+ ".xls";
        OutputStream fileOut = new FileOutputStream(fileName);

        wb.write(fileOut);
        fileOut.close();
        return fileName;
    }

    static File loadAssetsByList(Lot lot, List<Asset> assetList) throws IOException {

        POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream("C:\\projectFiles\\Shablon_1.xls"));
        HSSFWorkbook wb = new HSSFWorkbook(fs);
        HSSFSheet sheet = wb.getSheetAt(0);
        int shiftCount = assetList.size() + 6;

        try {
            sheet.getRow(1).getCell(0).setCellValue("Інформація про кредити ПАТ «КБ «НАДРА», що пропонуються на продаж на аукціоні "
                    + (sdf.format(lot.getBid().getBidDate())) + " р. на електронному торговому майданчику " + lot.getBid().getExchange().getCompanyName());
        }
        catch (NullPointerException npe){
            sheet.getRow(1).getCell(0).setCellValue("Інформація про кредити ПАТ «КБ «НАДРА», що пропонуються на продаж на аукціоні ");
        }

        //задаем формат дати
        String excelFormatter = DateFormatConverter.convert(Locale.ENGLISH , "yyyy-MM-dd");
        CellStyle cellStyle = wb.createCellStyle();
        DataFormat poiFormat = wb.createDataFormat();
        cellStyle.setDataFormat(poiFormat.getFormat(excelFormatter));
        //end
        sheet.shiftRows(7, 7, assetList.size() - 1);
        int numRow = 6;
        for (Asset asset : assetList) {
            HSSFRow row = sheet.createRow(numRow);
            numRow++;
            int j = 0;
            while (j < 14) {
                row.createCell(j);
                j++;
            }
            row.getCell(0).setCellValue(asset.getId());
            row.getCell(1).setCellValue(lot.getLotNum());
            row.getCell(2).setCellValue(asset.getAsset_name());
            row.getCell(3).setCellValue(asset.getInn());
            row.getCell(4).setCellValue(asset.getRv().doubleValue());
            try {
                row.getCell(5).setCellValue(asset.getEksplDate());
            }
            catch (NullPointerException e){
            }
            row.getCell(5).setCellStyle(cellStyle);
            row.getCell(6).setCellValue(asset.getOriginalPrice().doubleValue());
            try {
                row.getCell(7).setCellValue(asset.getZb().doubleValue());
            }
            catch(Exception e){
            }
            if(lot.getBidStage()!=null){
                int bidStage = 0;
                switch (lot.getBidStage()) {
                    case "Перші торги":
                        bidStage = 1;
                        break;
                    case "Другі торги":
                        bidStage = 2;
                        break;
                    case "Треті торги":
                        bidStage = 3;
                        break;
                    case "Четверті торги":
                        bidStage = 4;
                        break;
                    case "П'яті торги":
                        bidStage = 5;
                        break;
                    case "Шості торги":
                        bidStage = 6;
                        break;
                    case "Сьомі торги":
                        bidStage = 7;
                        break;
                    case "Восьмі торги":
                        bidStage = 8;
                        break;
                }
                row.getCell(8).setCellValue(bidStage);
            }
            try{
                BigDecimal discount = (lot.getFirstStartPrice()).divide(lot.getStartPrice(), 2);
                row.getCell(9).setCellValue(1-discount.doubleValue());
            }
            catch (NullPointerException e){
                row.getCell(9).setCellValue(0);
            }

            if(asset.getAcceptPrice()!=null)
                row.getCell(10).setCellFormula("L"+(numRow)+"/6*5" );
            if(asset.getAcceptPrice()!=null)
            {row.getCell(11).setCellValue(asset.getAcceptPrice().doubleValue());} //изменить!!!
            row.getCell(12).setCellValue(lot.getCountOfParticipants());
            if(asset.getFactPrice()!=null)
            {row.getCell(13).setCellValue(asset.getFactPrice().doubleValue());}
        }
        HSSFRow sumRow = sheet.getRow(6+assetList.size());
        sumRow.getCell(4).setCellFormula("SUM(E7:E" + shiftCount + ")");
        sumRow.getCell(6).setCellFormula("SUM(G7:G" + shiftCount + ")");
        sumRow.getCell(7).setCellFormula("SUM(H7:H" + shiftCount + ")");
        //sumRow.getCell(8).setCellFormula("SUM(I7:I" + shiftCount + ")");
        sumRow.getCell(10).setCellFormula("SUM(K7:K" + shiftCount + ")");
        sumRow.getCell(11).setCellFormula("SUM(L7:L" + shiftCount + ")");
        sumRow.getCell(12).setCellFormula("SUM(M7:M" + shiftCount + ")");
        sumRow.getCell(13).setCellFormula("SUM(N7:N" + shiftCount + ")");

        String fileName = "C:\\projectFiles\\"+"Credits_lot_"+lot.getId()+" ("+lot.getLotNum()+ ").xls";
        File file = new File(fileName);

        OutputStream fileOut = new FileOutputStream(file);

        wb.write(fileOut);
        fileOut.close();

        return file;
    }

    static File loadCreditsByLot(Lot lot, List<Credit> creditList) throws IOException {

        POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream("C:\\projectFiles\\Shablon_0.xls"));
        HSSFWorkbook wb = new HSSFWorkbook(fs);
        HSSFSheet sheet = wb.getSheetAt(0);
        int shiftCount = creditList.size() + 6;

        try {
            sheet.getRow(1).getCell(0).setCellValue("Інформація про кредити ПАТ «КБ «НАДРА», що пропонуються на продаж на аукціоні "
                    + (sdf.format(lot.getBid().getBidDate())) + " р. на електронному торговому майданчику " + lot.getBid().getExchange().getCompanyName());
        }
        catch (NullPointerException npe){
            sheet.getRow(1).getCell(0).setCellValue("Інформація про кредити ПАТ «КБ «НАДРА», що пропонуються на продаж на аукціоні ");
        }
        //задаем формат даты
        String excelFormatter = DateFormatConverter.convert(Locale.ENGLISH , "yyyy-MM-dd");
        CellStyle cellStyle = wb.createCellStyle();
        DataFormat poiFormat = wb.createDataFormat();
        cellStyle.setDataFormat(poiFormat.getFormat(excelFormatter));
        //end
        sheet.shiftRows(7, 7, creditList.size() - 1);
        int numRow = 6;
        for (Credit credit : creditList) {
            HSSFRow row = sheet.createRow(numRow);
            numRow++;
            int j = 0;
            while (j < 14) {
                row.createCell(j);
                j++;
            }
            row.getCell(0).setCellValue(credit.getNd());
            row.getCell(1).setCellValue(lot.getLotNum());
            row.getCell(2).setCellValue(credit.getFio());
            row.getCell(3).setCellValue(credit.getInn());
            row.getCell(4).setCellValue(credit.getRv().doubleValue());
            try {
                row.getCell(5).setCellValue(credit.getContractStart());
            }
            catch (NullPointerException e){
            }
            row.getCell(5).setCellStyle(cellStyle);

            row.getCell(6).setCellValue(credit.getBodyUAH().doubleValue());
            try {
                row.getCell(7).setCellValue(credit.getZb().doubleValue());
            }
            catch(Exception e){
            }
            if(lot.getBidStage()!=null){
                int bidStage = 0;
                switch (lot.getBidStage()) {
                    case "Перші торги":
                        bidStage = 1;
                        break;
                    case "Другі торги":
                        bidStage = 2;
                        break;
                    case "Треті торги":
                        bidStage = 3;
                        break;
                    case "Четверті торги":
                        bidStage = 4;
                        break;
                    case "П'яті торги":
                        bidStage = 5;
                        break;
                    case "Шості торги":
                        bidStage = 6;
                        break;
                    case "Сьомі торги":
                        bidStage = 7;
                        break;
                    case "Восьмі торги":
                        bidStage = 8;
                        break;
                }
                row.getCell(8).setCellValue(bidStage);
            }
            try{
                BigDecimal discount = (lot.getFirstStartPrice()).divide(lot.getStartPrice(), 2);
                row.getCell(9).setCellValue(1-discount.doubleValue());
            }
            catch (NullPointerException e){
                row.getCell(9).setCellValue(0);
            }

            if(credit.getAcceptPrice()!=null)
                row.getCell(10).setCellFormula("L"+(numRow)+"/6*5" );
            if(credit.getAcceptPrice()!=null)
            {row.getCell(11).setCellValue(credit.getAcceptPrice().doubleValue());} //изменить!!!
            row.getCell(12).setCellValue(lot.getCountOfParticipants());
            if(credit.getFactPrice()!=null)
            {row.getCell(13).setCellValue(credit.getFactPrice().doubleValue());}
        }
        HSSFRow sumRow = sheet.getRow(6+creditList.size());
        sumRow.getCell(4).setCellFormula("SUM(E7:E" + shiftCount + ")");
        sumRow.getCell(6).setCellFormula("SUM(G7:G" + shiftCount + ")");
        sumRow.getCell(7).setCellFormula("SUM(H7:H" + shiftCount + ")");
        //sumRow.getCell(8).setCellFormula("SUM(I7:I" + shiftCount + ")");
        sumRow.getCell(10).setCellFormula("SUM(K7:K" + shiftCount + ")");
        sumRow.getCell(11).setCellFormula("SUM(L7:L" + shiftCount + ")");
        sumRow.getCell(12).setCellFormula("SUM(M7:M" + shiftCount + ")");
        sumRow.getCell(13).setCellFormula("SUM(N7:N" + shiftCount + ")");

        String fileName = "C:\\projectFiles\\"+"Credits_lot_"+lot.getId()+" ("+lot.getLotNum()+ ").xls";
        File file = new File(fileName);

        OutputStream fileOut = new FileOutputStream(file);

        wb.write(fileOut);
        fileOut.close();

        return file;
    }
}