package com.vbuser.database.operate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Convert {
    public static String convertTxtToHtmlTable(String filePath) {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<!DOCTYPE html>\n");
        htmlBuilder.append("<html>\n");
        htmlBuilder.append("<head>\n");
        htmlBuilder.append("<style>\n");
        htmlBuilder.append("body {\n");
        htmlBuilder.append("background-color: #282c34;\n");
        htmlBuilder.append("color: #abb2bf;\n");
        htmlBuilder.append("}\n");

        htmlBuilder.append("a {\n");
        htmlBuilder.append("color: #61afef;\n");
        htmlBuilder.append("}\n");

        htmlBuilder.append("h1, h2, h3, h4, h5, h6 {\n");
        htmlBuilder.append("color: #e06c75;\n");
        htmlBuilder.append("}\n");

        htmlBuilder.append("p {\n");
        htmlBuilder.append("color: #abb2bf;\n");
        htmlBuilder.append("}\n");

        htmlBuilder.append("table {\n");
        htmlBuilder.append("border-collapse: collapse;\n");
        htmlBuilder.append("}\n");

        htmlBuilder.append("th, td {\n");
        htmlBuilder.append("border: 1px solid #abb2bf;\n");
        htmlBuilder.append("padding: 0.5rem;\n");
        htmlBuilder.append("color: #abb2bf;\n");
        htmlBuilder.append("}\n");

        htmlBuilder.append("input[type=\"text\"] {\n");
        htmlBuilder.append("background-color: transparent;\n");
        htmlBuilder.append("border: none;\n");
        htmlBuilder.append("color: #abb2bf;\n");
        htmlBuilder.append("}\n");

        htmlBuilder.append("</style>\n");
        htmlBuilder.append("</head>\n");
        htmlBuilder.append("<body>\n");

        htmlBuilder.append("<table border='1'>\n");

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                line = line.replace("@"," ");
                if (isHeader) {
                    htmlBuilder.append("<tr>");
                    for (String header : line.split(">")) {
                        htmlBuilder.append("<th>").append(header).append("</th>");
                    }
                    htmlBuilder.append("</tr>");
                    isHeader = false;
                } else {
                    htmlBuilder.append("<tr>");
                    for (String data : line.split(">")) {
                        htmlBuilder.append("<td>").append(data).append("</td>");
                    }
                    htmlBuilder.append("</tr>");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        htmlBuilder.append("</table>\n</body>\n</html>");

        return htmlBuilder.toString();
    }
}
