//本文件包含AI辅助生成内容
//AI辅助编程服务提供商:通义千问
//AI注释服务提供商:DeepSeek

package com.vbuser.database.operate;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 数据格式转换工具<br>
 * 将数据库表转换为HTML表格格式
 */
public class Convert {

    /**
     * 将文本表转换为HTML表格
     * @param fullTablePath 表文件完整路径
     * @return HTML格式的表格字符串
     */
    public static String convertTxtToHtmlTable(String fullTablePath) {
        // 解析数据库路径和表名
        Path tablePath = Paths.get(fullTablePath);

        // 获取数据库目录路径 (tables的父目录)
        Path dbPath = tablePath.getParent().getParent();

        // 获取表名 (不带扩展名的文件名)
        String tableName = tablePath.getFileName().toString().replace(".txt", "");

        // 构建HTML基础结构
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<!DOCTYPE html>\n<html>\n<head>\n");
        htmlBuilder.append("<style>\n");
        // 添加CSS样式
        htmlBuilder.append("body { background-color: #282c34; color: #abb2bf; margin: 0; font-family: Arial, sans-serif; }\n");
        htmlBuilder.append("a { color: #61afef; text-decoration: none; }\n");
        htmlBuilder.append("a:hover { text-decoration: underline; }\n");
        htmlBuilder.append("h1, h2, h3, h4, h5, h6 { color: #e06c75; }\n");
        htmlBuilder.append("p { color: #abb2bf; }\n");
        htmlBuilder.append("table { border-collapse: collapse; width: 100%; }\n");
        htmlBuilder.append("th, td { border: 1px solid #abb2bf; padding: 0.5rem; color: #abb2bf; }\n");
        htmlBuilder.append("input[type=\"text\"] { background-color: transparent; border: none; color: #abb2bf; }\n");

        // 侧边栏样式
        htmlBuilder.append("#sidebar { position: fixed; top: 0; left: 0; width: 200px; height: 100vh; background-color: #21252b; padding: 20px; font-size: 18px; overflow-y: auto; }\n");
        htmlBuilder.append("#sidebar h2 { font-size: 1.5rem; margin-bottom: 10px; }\n");
        htmlBuilder.append("#sidebar ul { list-style-type: none; padding: 0; }\n");
        htmlBuilder.append("#sidebar ul li { margin: 10px 0; }\n");
        htmlBuilder.append("#sidebar ul li.active::before { content: '> '; color: #e06c75; }\n");

        // 内容区域样式
        htmlBuilder.append("#content { margin-left: 220px; padding: 20px; }\n");
        htmlBuilder.append("</style>\n</head>\n<body>\n");

        // 构建侧边栏（表导航）
        htmlBuilder.append("<div id='sidebar'>\n<h2>Tables</h2>\n<ul>\n");

        // 使用安全的路径读取 tables.txt
        Path tablesListPath = dbPath.resolve("tables.txt");
        try (BufferedReader tablesReader = Files.newBufferedReader(tablesListPath)) {
            String tableLine;
            while ((tableLine = tablesReader.readLine()) != null) {
                String activeClass = tableLine.equals(tableName) ? " class='active'" : "";
                htmlBuilder.append("<li").append(activeClass).append("><a href='")
                        .append(tableLine).append(".html'>")
                        .append(tableLine).append("</a></li>\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading tables list", e);
        }
        htmlBuilder.append("</ul>\n</div>\n");

        // 构建表格内容区域
        htmlBuilder.append("<div id='content'>\n");
        htmlBuilder.append("<table>\n");

        // 读取表数据并转换为HTML行
        try (BufferedReader reader = Files.newBufferedReader(tablePath)) {
            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                line = line.replace("@", " ");
                htmlBuilder.append("<tr>");
                if (isHeader) {
                    // 表头行
                    for (String header : line.split(">")) {
                        htmlBuilder.append("<th>").append(header).append("</th>");
                    }
                    htmlBuilder.append("</tr>");
                    isHeader = false;
                } else {
                    // 数据行
                    for (String data : line.split(">")) {
                        htmlBuilder.append("<td>").append(data).append("</td>");
                    }
                    htmlBuilder.append("</tr>");
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Error reading table file", e);
        }

        htmlBuilder.append("</table>\n</div>\n");
        htmlBuilder.append("</body>\n</html>");
        return htmlBuilder.toString();
    }
}