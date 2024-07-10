package com.vbuser.database.operate;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Delete {

    private static final Map<String, Integer> columnIndices = new HashMap<>();

    public static void deleteFrom(File database, String tableName, String[] conditions) throws IOException {
        File file = new File(database, "\\tables\\" + tableName + ".txt");

        StringBuilder filteredContent = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    initColumnIndices(line);
                    filteredContent.append(line).append("\n");
                    isFirstLine = false;
                    continue;
                }

                if (!matchesConditions(line, conditions)) {
                    filteredContent.append(line).append("\n");
                }
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(filteredContent.toString());
        }
        System.out.println("[] Deleted rows from table " + tableName);
    }

    private static void initColumnIndices(String headerLine) {
        String[] headers = headerLine.split(">");
        for (int i = 0; i < headers.length; i++) {
            columnIndices.put(headers[i].trim(), i);
        }
    }

    private static boolean matchesConditions(String line, String[] conditions) {
        if (columnIndices.isEmpty()) {
            throw new IllegalStateException("[!] Column indices have not been initialized.");
        }

        String[] fields = line.split(">");
        for (String condition : conditions) {
            String[] parts = condition.split("=");
            String columnName = parts[0].trim();
            Integer index = columnIndices.get(columnName);
            if (index == null || !fields[index].equals(parts[1].trim())) {
                return false;
            }
        }
        return true;
    }
}
