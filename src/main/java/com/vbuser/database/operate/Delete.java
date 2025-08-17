package com.vbuser.database.operate;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据删除操作实现<br>
 * 支持条件删除表中的数据行
 */
public class Delete {

    // 存储列名与索引的映射关系
    private static final Map<String, Integer> columnIndices = new HashMap<>();

    /**
     * 从表中删除满足条件的数据行
     * @param database 数据库目录
     * @param tableName 表名称
     * @param conditions 删除条件数组
     */
    public static void deleteFrom(File database, String tableName, String[] conditions) throws IOException {
        File file = new File(new File(database, "tables"), tableName + ".txt");

        // 构建过滤后的内容
        StringBuilder filteredContent = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    // 初始化列索引映射
                    initColumnIndices(line);
                    filteredContent.append(line).append("\n");
                    isFirstLine = false;
                    continue;
                }

                // 保留不满足删除条件的行
                if (!matchesConditions(line, conditions)) {
                    filteredContent.append(line).append("\n");
                }
            }
        }

        // 将过滤后的内容写回文件
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(filteredContent.toString());
        }
        System.out.println("[] Deleted rows from table " + tableName);
    }

    /**
     * 初始化列名到索引的映射
     * @param headerLine 表头行
     */
    private static void initColumnIndices(String headerLine) {
        String[] headers = headerLine.split(">");
        for (int i = 0; i < headers.length; i++) {
            columnIndices.put(headers[i].trim(), i);
        }
    }

    /**
     * 检查数据行是否满足所有条件
     * @param line 数据行
     * @param conditions 条件数组
     * @return 是否满足所有条件
     */
    private static boolean matchesConditions(String line, String[] conditions) {
        if (columnIndices.isEmpty()) {
            throw new IllegalStateException("[!] Column indices have not been initialized.");
        }

        String[] fields = line.split(">");
        for (String condition : conditions) {
            String[] parts = condition.split("=");
            String columnName = parts[0].trim();
            Integer index = columnIndices.get(columnName);
            // 检查列是否存在且值匹配
            if (index == null || !fields[index].equals(parts[1].trim())) {
                return false;
            }
        }
        return true;
    }
}