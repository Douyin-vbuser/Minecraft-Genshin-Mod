package com.vbuser.database.operate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Update {

    public static void updateTable(File database, String tableName, String[] columns, String[] values, String[] conditions) {
        File tableFile = new File(database, "\\tables\\" + tableName + ".txt");

        if (!tableFile.exists() || !tableFile.isFile()) {
            System.out.println("Table not found.");
            return;
        }

        List<String> lines = readLines(tableFile);
        Map<String, Integer> headerMap = parseHeader(lines.get(0));

        List<Integer> rowsToUpdate = findRowsToUpdate(lines, conditions, headerMap);

        for (Integer rowIndex : rowsToUpdate) {
            String[] row = lines.get(rowIndex).split(">");
            for (int i = 0; i < columns.length; i++) {
                row[headerMap.get(columns[i])] = values[i];
            }
            lines.set(rowIndex, String.join(">", row));
        }

        writeLines(tableFile, lines);
        System.out.println("[] Table updated successfully.");
    }

    private static List<String> readLines(File file) {
        List<String> lines;
        try {
            lines = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return lines;
    }

    private static Map<String, Integer> parseHeader(String headerLine) {
        Map<String, Integer> headerMap = new HashMap<>();
        String[] headers = headerLine.split(">");
        for (int i = 0; i < headers.length; i++) {
            headerMap.put(headers[i], i);
        }
        return headerMap;
    }

    private static List<Integer> findRowsToUpdate(List<String> lines, String[] conditions, Map<String, Integer> headerMap) {
        List<Integer> rowsToUpdate = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String[] row = lines.get(i).split(">");
            if (matchesConditions(row, conditions, headerMap)) {
                rowsToUpdate.add(i);
            }
        }
        return rowsToUpdate;
    }

    private static boolean matchesConditions(String[] row, String[] conditions, Map<String, Integer> headerMap) {
        for (String condition : conditions) {
            String[] parts = condition.split("=", 2);
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid condition format: " + condition);
            }

            String fieldName = parts[0].trim();
            String value = parts[1].trim();
            int columnIndex = headerMap.get(fieldName);

            if (!value.equals(row[columnIndex])) {
                return false;
            }
        }
        return true;
    }

    private static void writeLines(File file, List<String> lines) {
        try {
            Files.write(file.toPath(), lines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
