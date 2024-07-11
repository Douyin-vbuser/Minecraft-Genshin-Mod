package com.vbuser.browser.server;

import com.sun.net.httpserver.HttpServer;
import com.vbuser.database.operate.Convert;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.net.InetSocketAddress;

public class Server {

    public static void start() throws Exception{
        int port = 80;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/", exchange -> {
            String requestMethod = exchange.getRequestMethod();
            if (requestMethod.equalsIgnoreCase("GET")) {
                String mcDir = Minecraft.getMinecraft().mcDataDir.getAbsolutePath();
                String root = mcDir.substring(0,mcDir.length()-2)+"\\web\\";
                String path = exchange.getRequestURI().getPath();
                if (path.equals("/")) {
                    path = "index.html";
                }
                File file = new File(root + path);
                if (file.exists()) {
                    byte[] fileBytes = new byte[(int) file.length()];
                    FileInputStream fis = new FileInputStream(file);
                    fis.read(fileBytes);
                    fis.close();

                    exchange.sendResponseHeaders(200, file.length());
                    OutputStream responseBody = exchange.getResponseBody();
                    responseBody.write(fileBytes);
                    responseBody.close();
                } else {
                    String response = "File not found";
                    exchange.sendResponseHeaders(404, response.length());
                    OutputStream responseBody = exchange.getResponseBody();
                    responseBody.write(response.getBytes());
                    responseBody.close();
                }
            } else {
                exchange.sendResponseHeaders(405, 0);
            }
            exchange.close();
        });

        server.setExecutor(null);
        server.start();
        System.out.println("Server is listening on port " + port);
    }

    public static void start(File dir) throws Exception{
        int port = 11451;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/", exchange -> {
            String requestMethod = exchange.getRequestMethod();
            if (requestMethod.equalsIgnoreCase("GET")) {
                String path =exchange.getRequestURI().getPath().substring(1);
                File file = new File(dir, "\\genshin_data\\tables\\"+path);
                String htmlResponse = Convert.convertTxtToHtmlTable(file.getAbsolutePath());
                exchange.sendResponseHeaders(200, htmlResponse.length());
                OutputStream responseBody = exchange.getResponseBody();
                responseBody.write(htmlResponse.getBytes());
                responseBody.close();
            } else {
                exchange.sendResponseHeaders(405, 0);
            }
            exchange.close();
        });

        server.setExecutor(null);
        server.start();
        System.out.println("Server is listening on port " + port);
    }
}
