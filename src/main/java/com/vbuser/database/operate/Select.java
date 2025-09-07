//本文件包含AI辅助生成内容
//AI辅助编程服务提供商:通义千问
//AI注释服务提供商:DeepSeek

package com.vbuser.database.operate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据查询操作实现<br>
 * 支持带条件的表数据查询
 */
public class Select {

    // 存储列名与索引的映射关系
    private static final Map<String, Integer> columnIndices = new HashMap<>();

    /**
     * 初始化列索引映射
     * @param headerLine 表头行
     */
    private static void initColumnIndices(String headerLine) {
        String[] headers = headerLine.split(">");
        for (int i = 0; i < headers.length; i++) {
            columnIndices.put(headers[i].trim(), i);
        }
    }

    /**
     * 检查行是否满足所有条件
     * @param line 数据行
     * @param conditions 条件数组
     * @return 是否满足所有条件
     */
    private static boolean matchesConditions(String line, String[] conditions) {
        if (columnIndices.isEmpty()) {
            throw new IllegalStateException("Column indices have not been initialized.");
        }

        String[] fields = line.split(">");
        for (String condition : conditions) {
            String[] parts = condition.split("=");
            String columnName = parts[0].trim();
            int index = columnIndices.get(columnName);
            // 检查值是否匹配
            if (index == -1 || !fields[index].equals(parts[1].trim())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 查询表数据
     * @param database 数据库目录
     * @param tableName 表名称
     * @param conditions 查询条件
     * @return 匹配的数据行列表
     */
    public static List<String> queryTable(File database, String tableName, String[] conditions) {
        List<String> result = new ArrayList<>();
        File tableFile = new File(new File(database, "tables"), tableName + ".txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(tableFile))) {
            // 读取表头
            String header = reader.readLine();
            result.add(header);
            initColumnIndices(header);

            // 读取并过滤数据行
            String line;
            while ((line = reader.readLine()) != null) {
                if (matchesConditions(line, conditions)) {
                    result.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("[!] Error reading file: " + e.getMessage());
        }
        System.out.println(result.size()-1 + " result(s) found from the table " + tableName);
        return result;
    }
}