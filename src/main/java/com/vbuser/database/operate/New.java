package com.vbuser.database.operate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class New {

    public static void createTable(String name, String[] columns, File dataBase) {
        try (FileWriter writer = new FileWriter(new File(dataBase, "\\tables\\" + name + ".txt"), true)) {
            for (int i = 0; i < columns.length; i++) {
                writer.write(columns[i]);
                if (i != columns.length - 1) {
                    writer.write(">");
                }
            }
            writer.write("\n");
        } catch (IOException e) {
            System.out.println("[!] An error occurred while creating the table.");
        }
        try (FileWriter writer = new FileWriter(new File(dataBase, "tables.txt"), true)) {
            writer.write(name + "\n");
        } catch (IOException e) {
            System.out.println("[!] An error occurred while creating the table.");
        }
        System.out.println("[] Table " + name + " created");
    }

    public static void insert(String name, String[] columns, String[] values, File dataBase) {
        Map<String, Integer> columnIndices = new HashMap<>();
        try {
            String headerLine = new String(java.nio.file.Files.readAllBytes(new File(dataBase, "\\tables\\" + name + ".txt").toPath())).split("\n")[0];
            String[] allColumns = headerLine.split(">");
            for (int i = 0; i < allColumns.length; i++) {
                columnIndices.put(allColumns[i], i);
            }
            try (FileWriter writer = new FileWriter(new File(dataBase, "\\tables\\" + name + ".txt"), true)) {
                StringBuilder line = new StringBuilder();
                for (String column : allColumns) {
                    if (columnIndices.containsKey(column)) {
                        int index = columnIndices.get(column);
                        if (index < columns.length && Arrays.asList(columns).contains(column)) {
                            int valueIndex = Arrays.asList(columns).indexOf(column);
                            line.append(values[valueIndex]);
                        }
                    }
                    line.append(">");
                }
                line.setLength(line.length() - ">".length());
                line.append(System.lineSeparator());
                writer.write(line.toString());
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
        System.out.println("[] Data inserted into table " + name);
    }
}
