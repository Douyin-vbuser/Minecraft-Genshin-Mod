package com.vbuser.database.operate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 表结构操作类<br>
 * 支持创建表和插入数据
 */
public class New {

    /**
     * 创建新表
     * @param name 表名称
     * @param columns 列名数组
     * @param dataBase 数据库目录
     */
    public static void createTable(String name, String[] columns, File dataBase) {
        // 创建表文件并写入表头
        File tableFile = new File(new File(dataBase, "tables"), name + ".txt");
        if(tableFile.exists()){
            System.out.println("[] Skipped as the table has existed.");
            return;
        }
        try (FileWriter writer = new FileWriter(tableFile, true)) {
            for (int i = 0; i < columns.length; i++) {
                writer.write(columns[i]);
                if (i != columns.length - 1) {
                    writer.write(">"); // 列分隔符
                }
            }
            writer.write("\n");
        } catch (IOException e) {
            System.out.println("[!] An error occurred while creating the table.");
        }

        // 在tables.txt中注册新表
        try (FileWriter writer = new FileWriter(new File(dataBase, "tables.txt"), true)) {
            writer.write(name + "\n");
        } catch (IOException e) {
            System.out.println("[!] An error occurred while creating the table.");
        }
        System.out.println("[] Table " + name + " created");
    }

    /**
     * 向表中插入数据
     * @param name 表名称
     * @param columns 插入的列名
     * @param values 对应的值
     * @param dataBase 数据库目录
     */
    public static void insert(String name, String[] columns, String[] values, File dataBase) {
        Map<String, Integer> columnIndices = new HashMap<>();
        try {
            // 读取表结构
            File tableFile = new File(new File(dataBase, "tables"), name + ".txt");
            String headerLine = new String(java.nio.file.Files.readAllBytes(tableFile.toPath())).split("\n")[0];
            String[] allColumns = headerLine.split(">");

            // 建立列名到索引的映射
            for (int i = 0; i < allColumns.length; i++) {
                columnIndices.put(allColumns[i], i);
            }

            // 写入新数据行
            try (FileWriter writer = new FileWriter(tableFile, true)) {
                StringBuilder line = new StringBuilder();
                for (String column : allColumns) {
                    if (columnIndices.containsKey(column)) {
                        int index = columnIndices.get(column);
                        // 匹配插入列的值
                        if (index < columns.length && Arrays.asList(columns).contains(column)) {
                            int valueIndex = Arrays.asList(columns).indexOf(column);
                            line.append(values[valueIndex]);
                        }
                    }
                    line.append(">");
                }
                // 移除末尾多余的分隔符
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