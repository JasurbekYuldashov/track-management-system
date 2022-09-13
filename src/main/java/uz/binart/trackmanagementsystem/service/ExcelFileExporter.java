package uz.binart.trackmanagementsystem.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import uz.binart.trackmanagementsystem.dto.AccountingDto;
import uz.binart.trackmanagementsystem.dto.excel.Customer;

public class ExcelFileExporter {

    public static ByteArrayInputStream contactListToExcelFile(List<Customer> customers) {
        try(Workbook workbook = new XSSFWorkbook()){
            Sheet sheet = workbook.createSheet("Customers");

            Row row = sheet.createRow(0);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            // Creating header
            Cell cell = row.createCell(0);
            cell.setCellValue("First Name");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(1);
            cell.setCellValue("Last Name");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(2);
            cell.setCellValue("Mobile");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(3);
            cell.setCellValue("Email");
            cell.setCellStyle(headerCellStyle);

            // Creating data rows for each customer
            for(int i = 0; i < customers.size(); i++) {
                Row dataRow = sheet.createRow(i + 1);
                dataRow.createCell(0).setCellValue(customers.get(i).getFirstName());
                dataRow.createCell(1).setCellValue(customers.get(i).getLastName());
                dataRow.createCell(2).setCellValue(customers.get(i).getMobileNumber());
                dataRow.createCell(3).setCellValue(customers.get(i).getEmail());
            }

            // Making size of column auto resize to fit with data
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static ByteArrayInputStream accountingInfoDto(List<AccountingDto> accountingDtoS, Long timeStart, Long timeEnd, Boolean weekly, Boolean isDispatcher){

        try(Workbook workbook = new XSSFWorkbook()){
            Sheet sheet = workbook.createSheet("accounting_information");
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);


            List<String> headers = getHeaders(timeStart, timeEnd);
            int rowsToAdd = weekly ? headers.size() : 0;
            Row firstRow = sheet.createRow(0);

            Cell firstRowCell = firstRow.createCell(0);
            firstRowCell.setCellValue("NO");
            firstRowCell.setCellStyle(headerCellStyle);

            firstRowCell = firstRow.createCell(1);
            firstRowCell.setCellValue("CARRIER");
            firstRowCell.setCellStyle(headerCellStyle);

            firstRowCell = firstRow.createCell(2);
            firstRowCell.setCellValue("BOOKED");
            firstRowCell.setCellStyle(headerCellStyle);

            firstRowCell = firstRow.createCell(3);
            firstRowCell.setCellStyle(headerCellStyle);

            firstRowCell = firstRow.createCell(4);
            firstRowCell.setCellValue("START");
            firstRowCell.setCellStyle(headerCellStyle);

            firstRowCell = firstRow.createCell(5);
            firstRowCell.setCellStyle(headerCellStyle);

            firstRowCell = firstRow.createCell(6);
            firstRowCell.setCellValue("FINISH");
            firstRowCell.setCellStyle(headerCellStyle);

            firstRowCell = firstRow.createCell(7);
            firstRowCell.setCellStyle(headerCellStyle);

            firstRowCell = firstRow.createCell(8);
            firstRowCell.setCellValue("TRUCK");
            firstRowCell.setCellStyle(headerCellStyle);

            firstRowCell = firstRow.createCell(9);
            firstRowCell.setCellStyle(headerCellStyle);

            firstRowCell = firstRow.createCell(10);
            firstRowCell.setCellValue("PRICE");
            firstRowCell.setCellStyle(headerCellStyle);

            firstRowCell = firstRow.createCell(11);
            firstRowCell.setCellStyle(headerCellStyle);

            firstRowCell = firstRow.createCell(12);
            firstRowCell.setCellStyle(headerCellStyle);

            firstRowCell = firstRow.createCell(13);
            firstRowCell.setCellStyle(headerCellStyle);

            firstRowCell = firstRow.createCell(14);
            firstRowCell.setCellStyle(headerCellStyle);

            firstRowCell = firstRow.createCell(15);
            firstRowCell.setCellStyle(headerCellStyle);

            if(weekly)
            for(int i = 0; i < headers.size(); i++){
                firstRowCell = firstRow.createCell(16 + i);
                firstRowCell.setCellStyle(headerCellStyle);
            }

            firstRowCell = firstRow.createCell(16 + rowsToAdd);
            firstRowCell.setCellStyle(headerCellStyle);

            int accountingAndAdminRows = 3;

            if(isDispatcher)
                accountingAndAdminRows = 0;

            if(!isDispatcher) {
                firstRowCell = firstRow.createCell(17 + rowsToAdd);
                firstRowCell.setCellValue("FACTORING");
                firstRowCell.setCellStyle(headerCellStyle);

                firstRowCell = firstRow.createCell(18 + rowsToAdd);
                firstRowCell.setCellStyle(headerCellStyle);

                firstRowCell = firstRow.createCell(19 + rowsToAdd);
                firstRowCell.setCellStyle(headerCellStyle);
            }


            firstRowCell = firstRow.createCell(17 + rowsToAdd + accountingAndAdminRows);
            firstRowCell.setCellValue("TEAM");
            firstRowCell.setCellStyle(headerCellStyle);

            firstRowCell = firstRow.createCell(18 + rowsToAdd + accountingAndAdminRows);
            firstRowCell.setCellValue("NOTE");
            firstRowCell.setCellStyle(headerCellStyle);

            Row secondRow = sheet.createRow(1);

            Cell cell = secondRow.createCell(0);
            cell.setCellValue("A");
            cell.setCellStyle(headerCellStyle);

            cell = secondRow.createCell(1);
            cell.setCellValue("COMPANY");
            cell.setCellStyle(headerCellStyle);

            cell = secondRow.createCell(2);
            cell.setCellValue("RC NO");
            cell.setCellStyle(headerCellStyle);

            cell = secondRow.createCell(3);
            cell.setCellValue("COMPANY");
            cell.setCellStyle(headerCellStyle);

            cell = secondRow.createCell(4);
            cell.setCellValue("DATE/TIME");
            cell.setCellStyle(headerCellStyle);

            cell = secondRow.createCell(5);
            cell.setCellValue("LOCATION");
            cell.setCellStyle(headerCellStyle);

            cell = secondRow.createCell(6);
            cell.setCellValue("DATE/TIME");
            cell.setCellStyle(headerCellStyle);

            cell = secondRow.createCell(7);
            cell.setCellValue("LOCATION");
            cell.setCellStyle(headerCellStyle);

            cell = secondRow.createCell(8);
            cell.setCellValue("NO");
            cell.setCellStyle(headerCellStyle);

            cell = secondRow.createCell(9);
            cell.setCellValue("COMPANY");
            cell.setCellStyle(headerCellStyle);

            cell = secondRow.createCell(10);
            cell.setCellValue("BOOKED");
            cell.setCellStyle(headerCellStyle);

            cell = secondRow.createCell(11);
            cell.setCellValue("DISPUTE");
            cell.setCellStyle(headerCellStyle);

            cell = secondRow.createCell(12);
            cell.setCellValue("DETENTION");
            cell.setCellStyle(headerCellStyle);

            cell = secondRow.createCell(13);
            cell.setCellValue("ADDITIONAL");
            cell.setCellStyle(headerCellStyle);

            cell = secondRow.createCell(14);
            cell.setCellValue("FINE");
            cell.setCellStyle(headerCellStyle);

            cell = secondRow.createCell(15);
            cell.setCellValue("REVISED/INVOICE");
            cell.setCellStyle(headerCellStyle);

            if(weekly)
            for(int i = 0; i < headers.size(); i++){
                cell = secondRow.createCell(16 + i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerCellStyle);
            }


            cell = secondRow.createCell(16 + rowsToAdd);
            cell.setCellValue("K-O");
            cell.setCellStyle(headerCellStyle);

            if(!isDispatcher) {
                cell = secondRow.createCell(17 + rowsToAdd);
                cell.setCellValue("FACTORING");
                cell.setCellStyle(headerCellStyle);

                cell = secondRow.createCell(18 + rowsToAdd);
                cell.setCellValue("TAFS");
                cell.setCellStyle(headerCellStyle);

                cell = secondRow.createCell(19 + rowsToAdd);
                cell.setCellValue("NET PAID");
                cell.setCellStyle(headerCellStyle);
            }

            cell = secondRow.createCell(17 + rowsToAdd + accountingAndAdminRows);
            cell.setCellValue("NAME");
            cell.setCellStyle(headerCellStyle);

            cell = secondRow.createCell(18 + rowsToAdd + accountingAndAdminRows);
            cell.setCellValue("NOTE");
            cell.setCellStyle(headerCellStyle);

            for(int i = 0; i < accountingDtoS.size(); i++){
                Row dataRow = sheet.createRow(i + 2);
                AccountingDto acc = accountingDtoS.get(i);
                dataRow.createCell(0).setCellValue(acc.getSerialNumber());
                dataRow.createCell(1).setCellValue(acc.getCarrierName());
                dataRow.createCell(2).setCellValue(acc.getRc());
                dataRow.createCell(3).setCellValue(acc.getCompany());
                dataRow.createCell(4).setCellValue(acc.getTimeStart());
                dataRow.createCell(5).setCellValue(acc.getShipperCompanyLocation());
                dataRow.createCell(6).setCellValue(acc.getEndTime());
                dataRow.createCell(7).setCellValue(acc.getEndLocation());
                if(acc.getTruckNumber() != null) {
                    Map<String, String> number = splitTruckNumber(acc.getTruckNumber());
                    dataRow.createCell(8).setCellValue(number.get("number"));
                    dataRow.createCell(9).setCellValue(number.get("company"));
                }
                dataRow.createCell(10).setCellValue(Math.round(acc.getBooked() * 100.0) / 100.0);
                dataRow.createCell(11).setCellValue(Math.round(acc.getDispute() * 100.0) / 100.0);
                dataRow.createCell(12).setCellValue(Math.round(acc.getDetention() * 100.0) / 100.0);
                dataRow.createCell(13).setCellValue(Math.round(acc.getAdditional() * 100.0) / 100.0);
                dataRow.createCell(14).setCellValue(Math.round(acc.getFine() * 100.0) / 100.0);
                dataRow.createCell(15).setCellValue(Math.round(acc.getRevisedInvoice() * 100.0) / 100.0);

                if(weekly)
                for(int j = 0; j < acc.getSegmentedPrices().length; j++){
                    dataRow.createCell(16 + j).setCellValue(acc.getSegmentedPrices()[j]);
                }

                dataRow.createCell(16 + rowsToAdd).setCellValue(Math.round(acc.getKo() * 100.0) / 100.0);

                if(!isDispatcher) {
                    dataRow.createCell(17 + rowsToAdd).setCellValue(Math.round(acc.getFactoring() * 100.0) / 100.0);
                    dataRow.createCell(18 + rowsToAdd).setCellValue(Math.round(acc.getTafs() * 100.0) / 100.0);
                    dataRow.createCell(19 + rowsToAdd).setCellValue(Math.round(acc.getNetPaid() * 100.0) / 100.0);
                }
                dataRow.createCell(17 + rowsToAdd + accountingAndAdminRows).setCellValue(acc.getTeam());
                dataRow.createCell(18 + rowsToAdd + accountingAndAdminRows).setCellValue(acc.getNote());

            }

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);
            sheet.autoSizeColumn(5);
            sheet.autoSizeColumn(6);
            sheet.autoSizeColumn(7);
            sheet.autoSizeColumn(8);
            sheet.autoSizeColumn(9);
            sheet.autoSizeColumn(10);
            sheet.autoSizeColumn(11);
            sheet.autoSizeColumn(12);
            sheet.autoSizeColumn(13);
            sheet.autoSizeColumn(14);
            sheet.autoSizeColumn(15);
            sheet.autoSizeColumn(16);
            if(!isDispatcher) {
                sheet.autoSizeColumn(17);
                sheet.autoSizeColumn(18);
                sheet.autoSizeColumn(19);
            }
            sheet.autoSizeColumn(17 + accountingAndAdminRows);
            sheet.autoSizeColumn(18 + accountingAndAdminRows);
            sheet.autoSizeColumn(19 + accountingAndAdminRows);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        }catch(IOException ex){
            ex.printStackTrace();
            return null;
        }
    }

    static Map<String, String> splitTruckNumber(String number){

        Map<String, String> map = new HashMap<>();

        String[] numberParts = number.split("-");

        if(numberParts.length >= 2){
            map.put("number", numberParts[0]);
            map.put("company", numberParts[1]);
        }else if(numberParts.length == 1){
            map.put("number", numberParts[0]);
            map.put("company", "");
        }else{
            map.put("number", "");
            map.put("company", "");
        }

        return map;

    }

    static String castToCentralAndFormatTime(Long time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-YYYY HH:mm");
        Date result = new Date(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(result);
        calendar.add(Calendar.HOUR_OF_DAY, -2);
        return simpleDateFormat.format(calendar.getTime());
    }

    static List<String> getHeaders(Long timeStart, Long timeEnd){

        List<String> headers = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStart);

        if(calendar.get(Calendar.DAY_OF_WEEK) >= 6)
            calendar.add(Calendar.WEEK_OF_MONTH, 1);

        calendar.set(Calendar.DAY_OF_WEEK, 6);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-YYYY");

        do{
            Date date = new Date(calendar.getTimeInMillis());
            headers.add(simpleDateFormat.format(date));
            calendar.add(Calendar.DAY_OF_WEEK, 7);
        }while(calendar.getTimeInMillis() <= timeEnd);
        headers.add("AFTER\n" + headers.get(headers.size() - 1));
        return headers;
    }


}
