package com.vbuser.database.operate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Convert {
    public static String convertTxtToHtmlTable(String fullTablePath) {
        int tablesIndex = fullTablePath.lastIndexOf("\\tables\\");
        String dbPath = fullTablePath.substring(0, tablesIndex + 1);
        String tableName = fullTablePath.substring(fullTablePath.lastIndexOf("\\") + 1, fullTablePath.lastIndexOf("."));

        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<!DOCTYPE html>\n<html>\n<head>\n");
        htmlBuilder.append("<style>\n");
        htmlBuilder.append("body { background-color: #282c34; color: #abb2bf; margin: 0; font-family: Arial, sans-serif; }\n");
        htmlBuilder.append("a { color: #61afef; text-decoration: none; }\n");
        htmlBuilder.append("a:hover { text-decoration: underline; }\n");
        htmlBuilder.append("h1, h2, h3, h4, h5, h6 { color: #e06c75; }\n");
        htmlBuilder.append("p { color: #abb2bf; }\n");
        htmlBuilder.append("table { border-collapse: collapse; width: 100%; }\n");
        htmlBuilder.append("th, td { border: 1px solid #abb2bf; padding: 0.5rem; color: #abb2bf; }\n");
        htmlBuilder.append("input[type=\"text\"] { background-color: transparent; border: none; color: #abb2bf; }\n");

        htmlBuilder.append("#sidebar { position: fixed; top: 0; left: 0; width: 200px; height: 100vh; background-color: #21252b; padding: 20px; font-size: 18px; overflow-y: auto; }\n");
        htmlBuilder.append("#sidebar h2 { font-size: 1.5rem; margin-bottom: 10px; }\n");
        htmlBuilder.append("#sidebar ul { list-style-type: none; padding: 0; }\n");
        htmlBuilder.append("#sidebar ul li { margin: 10px 0; }\n");
        htmlBuilder.append("#sidebar ul li.active::before { content: '> '; color: #e06c75; }\n");

        htmlBuilder.append("#content { margin-left: 220px; padding: 20px; }\n");

        htmlBuilder.append("</style>\n</head>\n<body>\n");

        htmlBuilder.append("<div id='sidebar'>\n<h2>Tables</h2>\n<ul>\n");
        try (BufferedReader tablesReader = new BufferedReader(new FileReader(dbPath + "tables.txt"))) {
            String tableLine;
            while ((tableLine = tablesReader.readLine()) != null) {
                String activeClass = tableLine.equals(tableName) ? " class='active'" : "";
                htmlBuilder.append("<li").append(activeClass).append("><a href='").append(tableLine).append(".html'>")
                        .append(tableLine).append("</a></li>\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading tables list", e);
        }
        htmlBuilder.append("</ul>\n</div>\n");

        htmlBuilder.append("<div id='content'>\n");
        htmlBuilder.append("<table>\n");

        String filePath = dbPath + "tables\\" + tableName + ".txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                line = line.replace("@", " ");
                htmlBuilder.append("<tr>");
                if (isHeader) {
                    for (String header : line.split(">")) {
                        htmlBuilder.append("<th>").append(header).append("</th>");
                    }
                    htmlBuilder.append("</tr>");
                    isHeader = false;
                } else {
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
