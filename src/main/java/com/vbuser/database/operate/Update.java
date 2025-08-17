package com.vbuser.database.operate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据更新操作实现<br>
 * 支持条件更新表中的数据
 */
public class Update {

    /**
     * 更新表数据
     * @param database 数据库目录
     * @param tableName 表名称
     * @param columns 要更新的列
     * @param values 新值
     * @param conditions 更新条件
     */
    public static void updateTable(File database, String tableName, String[] columns, String[] values, String[] conditions) {
        File tableFile = new File(new File(database, "tables"), tableName + ".txt");

        // 验证表文件存在
        if (!tableFile.exists() || !tableFile.isFile()) {
            System.out.println("Table not found.");
            return;
        }

        // 读取表内容
        List<String> lines = readLines(tableFile);
        Map<String, Integer> headerMap = parseHeader(lines.get(0));

        // 查找需要更新的行
        List<Integer> rowsToUpdate = findRowsToUpdate(lines, conditions, headerMap);

        // 更新数据行
        for (Integer rowIndex : rowsToUpdate) {
            String[] row = lines.get(rowIndex).split(">");
            for (int i = 0; i < columns.length; i++) {
                row[headerMap.get(columns[i])] = values[i];
            }
            lines.set(rowIndex, String.join(">", row));
        }

        // 写回更新后的内容
        writeLines(tableFile, lines);
        System.out.println("[] Table updated successfully.");
    }

    /**
     * 读取文件所有行
     * @param file 表文件
     * @return 行列表
     */
    private static List<String> readLines(File file) {
        List<String> lines;
        try {
            lines = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return lines;
    }

    /**
     * 解析表头创建列映射
     * @param headerLine 表头行
     * @return 列名到索引的映射
     */
    private static Map<String, Integer> parseHeader(String headerLine) {
        Map<String, Integer> headerMap = new HashMap<>();
        String[] headers = headerLine.split(">");
        for (int i = 0; i < headers.length; i++) {
            headerMap.put(headers[i], i);
        }
        return headerMap;
    }

    /**
     * 查找需要更新的行
     * @param lines 所有行
     * @param conditions 更新条件
     * @param headerMap 列映射
     * @return 需要更新的行索引列表
     */
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

    /**
     * 检查行是否满足所有条件
     * @param row 数据行
     * @param conditions 条件数组
     * @param headerMap 列映射
     * @return 是否满足所有条件
     */
    private static boolean matchesConditions(String[] row, String[] conditions, Map<String, Integer> headerMap) {
        for (String condition : conditions) {
            String[] parts = condition.split("=", 2);
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid condition format: " + condition);
            }

            String fieldName = parts[0].trim();
            String value = parts[1].trim();
            int columnIndex = headerMap.get(fieldName);

            // 检查值是否匹配
            if (!value.equals(row[columnIndex])) {
                return false;
            }
        }
        return true;
    }

    /**
     * 将内容写回文件
     * @param file 表文件
     * @param lines 更新后的行列表
     */
    private static void writeLines(File file, List<String> lines) {
        try {
            Files.write(file.toPath(), lines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}