package com.lb.book_scribe.enrichment.nameparser;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class FamilyNamePrefixParser {

    public static void main(String[] args) throws IOException {
        Set<String> prefixSet = new TreeSet<>(); // sorted output

        try (InputStream is = FamilyNamePrefixParser.class
                .getClassLoader()
                .getResourceAsStream("prefixes.xlsx");
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // skip row 0
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Cell cell = row.getCell(1); // Column B = index 1
                if (cell == null || cell.getCellType() != CellType.STRING) continue;

                String raw = cell.getStringCellValue().trim().toLowerCase();
                if (!raw.isEmpty()) {
                    prefixSet.add(raw);
                }
            }
        }

        System.out.println("private static final Set<String> PREFIXES = Set.of(");
        int i = 0;
        for (String prefix : prefixSet) {
            System.out.print("    \"" + prefix + "\"");
            if (++i < prefixSet.size()) System.out.print(",");
            System.out.println();
        }
        System.out.println(");");
    }


}
