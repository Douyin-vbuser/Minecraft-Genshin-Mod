import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class Start {
    public static void main(String[] args) throws IOException {
        int port = 80;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/", exchange -> {
            String requestMethod = exchange.getRequestMethod();
            if (requestMethod.equalsIgnoreCase("GET")) {
                String root = "E:\\Desktop\\Refactor_version\\misc\\WebPage\\";
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
}
