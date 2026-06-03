package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelLoader {

    public static TableContext loadTable(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new RuntimeException("Excel файл пуст!");
            }

            int colCount = headerRow.getLastCellNum();
            String[] columns = new String[colCount];
            for (int i = 0; i < colCount; i++) {
                Cell cell = headerRow.getCell(i);
                columns[i] = cell != null ? cell.getStringCellValue().trim() : "col" + i;
            }

            List<Object[]> dataList = new ArrayList<>();
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                Object[] rowData = new Object[colCount];
                boolean isEmptyRow = true;

                for (int c = 0; c < colCount; c++) {
                    Cell cell = row.getCell(c);
                    if (cell == null) {
                        rowData[c] = null;
                        continue;
                    }

                    switch (cell.getCellType()) {
                        case STRING -> rowData[c] = cell.getStringCellValue();
                        case NUMERIC -> rowData[c] = cell.getNumericCellValue();
                        case BOOLEAN -> rowData[c] = cell.getBooleanCellValue();
                        case BLANK -> rowData[c] = null;
                        default -> rowData[c] = cell.toString();
                    }

                    if (rowData[c] != null) {
                        isEmptyRow = false;
                    }
                }

                if (!isEmptyRow) {
                    dataList.add(rowData);
                }
            }

            return new TableContext(columns, dataList.toArray(new Object[0][]));

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при чтении Excel файла: " + filePath, e);
        }
    }
}