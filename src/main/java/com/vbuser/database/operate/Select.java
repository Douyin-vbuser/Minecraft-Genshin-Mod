package com.vbuser.database.operate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Select {

    private static final Map<String, Integer> columnIndices = new HashMap<>();

    private static void initColumnIndices(String headerLine) {
        String[] headers = headerLine.split(">");
        for (int i = 0; i < headers.length; i++) {
            columnIndices.put(headers[i].trim(), i);
        }
    }

    private static boolean matchesConditions(String line, String[] conditions) {
        if (columnIndices.isEmpty()) {
            throw new IllegalStateException("Column indices have not been initialized.");
        }

        String[] fields = line.split(">");
        for (String condition : conditions) {
            String[] parts = condition.split("=");
            String columnName = parts[0].trim();
            int index = columnIndices.get(columnName);
            if (index == -1 || !fields[index].equals(parts[1].trim())) {
                return false;
            }
        }
        return true;
    }

    public static List<String> queryTable(File database, String tableName, String[] conditions) {
        List<String> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(database, "\\tables\\" + tableName + ".txt")))) {
            String header = reader.readLine();
            result.add(header);
            initColumnIndices(header);

            String line;
            while ((line = reader.readLine()) != null) {
                if (matchesConditions(line, conditions)) {
                    result.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("[!] Error reading file: " + e.getMessage());
        }
        System.out.println(result.size() + " result(s) found from the table " + tableName);
        return result;
    }
}
