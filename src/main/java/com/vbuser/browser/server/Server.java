package com.vbuser.browser.server;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.vbuser.database.operate.Convert;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.vbuser.database.operate.Console.executeCommand;
import static com.vbuser.database.operate.ToTP.validateTOTP;

public class Server {

    public static void start() throws Exception {
        int port = 80;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/", exchange -> {
            String requestMethod = exchange.getRequestMethod();
            if (requestMethod.equalsIgnoreCase("GET")) {
                String mcDir = Minecraft.getMinecraft().mcDataDir.getAbsolutePath();
                String root = mcDir.substring(0, mcDir.length() - 2) + "\\web\\";
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

    private static final int PORT = 11451;
    private static String TOTP_PAGE;
    private static String TERMINAL_PAGE;
    private static final Map<String, Long> tokenStore = new HashMap<>();
    private static String key;//totp key
    private static File db;

    public static void start(File dir) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        setHTMLContent();
        db = new File(dir, "genshin_data");

        File file = new File(dir, "genshin_data/totp.txt");
        key = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(60 * 1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                tokenStore.entrySet().removeIf(entry -> System.currentTimeMillis() > entry.getValue());
            }
        }).start();

        server.createContext("/", exchange -> {
            try {
                String requestMethod = exchange.getRequestMethod();
                if (requestMethod.equalsIgnoreCase("GET")) {
                    handleGetRequest(exchange, dir);
                } else if (requestMethod.equalsIgnoreCase("POST")) {
                    handlePostRequest(exchange);
                } else {
                    exchange.sendResponseHeaders(405, 0);
                    exchange.close();
                }
            } catch (Exception e) {
                exchange.sendResponseHeaders(500, -1);
                exchange.close();
            }
        });

        server.setExecutor(null);
        server.start();
        System.out.println("Server is listening on port " + PORT);
    }

    private static void handleGetRequest(HttpExchange exchange, File dir) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if (path.equals("/")) {
            exchange.getResponseHeaders().set("Location", "/totp");
            exchange.sendResponseHeaders(302, -1);
            exchange.close();
        } else if (path.equals("/totp")) {
            sendResponse(exchange, TOTP_PAGE);
        } else if (path.startsWith("/terminal")) {
            String token = exchange.getRequestURI().getQuery().replace("token=", "");
            if (isTokenValid(token)) {
                exchange.getResponseHeaders().set("Location", "/totp");
                exchange.sendResponseHeaders(302, -1);
                exchange.close();
                return;
            }
            sendResponse(exchange, TERMINAL_PAGE);
        } else {
            path = path.substring(1);
            File file = new File(dir, "genshin_data/tables/" + path);
            String htmlResponse = Convert.convertTxtToHtmlTable(file.getAbsolutePath());
            sendResponse(exchange, htmlResponse);
        }
    }

    private static String generateToken() {
        String uuid = UUID.randomUUID().toString();
        tokenStore.put(uuid, System.currentTimeMillis() + 5 * 60 * 1000);
        return uuid;
    }

    private static void handlePostRequest(HttpExchange exchange) throws IOException {
        if (exchange.getRequestURI().getPath().equals("/totp")) {
            String totpCode = getString(exchange, "code");
            if (!validateTOTP(key, totpCode)) {
                exchange.getResponseHeaders().set("Location", "/totp");
                exchange.sendResponseHeaders(302, -1);
            } else {
                String token = generateToken();
                String redirectUrl = "/terminal?token=" + token;
                exchange.getResponseHeaders().set("Location", redirectUrl);
                exchange.sendResponseHeaders(302, -1);
            }
            exchange.close();
        } else if (exchange.getRequestURI().getPath().contains("terminal")) {
            String token = exchange.getRequestURI().getQuery().replace("token=", "");
            if (isTokenValid(token)) {
                exchange.getResponseHeaders().set("Location", "/totp");
                exchange.sendResponseHeaders(302, -1);
                exchange.close();
            } else {
                executeCommand("access " + db.getAbsolutePath());
                String command = getString(exchange, "command");
                command = command.replaceAll("%20", " ").replaceAll("%3D", "=");
                executeCommand(command);
                System.out.println("executing:" + command);
            }
        } else {
            exchange.sendResponseHeaders(404, -1);
            exchange.close();
        }
    }

    private static String getString(HttpExchange exchange, String key) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        try (InputStream inputStream = exchange.getRequestBody();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }
        String totpCode = null;
        String body = requestBody.toString();
        if (body.startsWith(key + "=")) {
            totpCode = body.substring((key + "=").length());
        }
        System.out.println(key + ": " + totpCode);
        return totpCode;
    }

    private static boolean isTokenValid(String token) {
        return !tokenStore.containsKey(token);
    }

    private static void sendResponse(HttpExchange exchange, String response) throws IOException {
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, responseBytes.length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(responseBytes);
        outputStream.close();
    }

    public static void setHTMLContent() {
        TOTP_PAGE = "<!DOCTYPE html>\n" +
                "<html lang=\"zh-CN\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>TOTP Verification</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            background-color: #282c34;\n" +
                "            color: #abb2bf;\n" +
                "            margin: 0;\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            display: flex;\n" +
                "            justify-content: center;\n" +
                "            align-items: center;\n" +
                "            height: 100vh;\n" +
                "        }\n" +
                "\n" +
                "        a {\n" +
                "            color: #61afef;\n" +
                "            text-decoration: none;\n" +
                "        }\n" +
                "\n" +
                "        a:hover {\n" +
                "            text-decoration: underline;\n" +
                "        }\n" +
                "\n" +
                "        h1, h2, h3, h4, h5, h6 {\n" +
                "            color: #e06c75;\n" +
                "        }\n" +
                "\n" +
                "        p {\n" +
                "            color: #abb2bf;\n" +
                "        }\n" +
                "\n" +
                "        input[type=\"text\"], input[type=\"submit\"] {\n" +
                "            background-color: transparent;\n" +
                "            border: 1px solid #abb2bf;\n" +
                "            color: #abb2bf;\n" +
                "            padding: 10px;\n" +
                "            font-size: 16px;\n" +
                "            border-radius: 4px;\n" +
                "            margin-bottom: 10px;\n" +
                "        }\n" +
                "\n" +
                "        input[type=\"text\"]:focus {\n" +
                "            outline: none;\n" +
                "            border-color: #61afef;\n" +
                "        }\n" +
                "\n" +
                "        #totp-box {\n" +
                "            background-color: #21252b;\n" +
                "            padding: 40px;\n" +
                "            border-radius: 8px;\n" +
                "            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.5);\n" +
                "            width: 100%;\n" +
                "            max-width: 400px;\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "\n" +
                "        #totp-box h2 {\n" +
                "            margin-bottom: 20px;\n" +
                "            font-size: 24px;\n" +
                "        }\n" +
                "\n" +
                "        input[type=\"submit\"] {\n" +
                "            background-color: #61afef;\n" +
                "            border: none;\n" +
                "            cursor: pointer;\n" +
                "        }\n" +
                "\n" +
                "        input[type=\"submit\"]:hover {\n" +
                "            background-color: #528bbf;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div id=\"totp-box\">\n" +
                "        <h2>TOTP Verification</h2>\n" +
                "        <p>Press the code generated by your plugin or 2FA app.</p>\n" +
                "        <form action=\"/totp\" method=\"POST\"> \n" +
                "            <input type=\"text\" name=\"code\" placeholder=\"XXXXXX\" maxlength=\"6\" required>\n" +
                "            <br>\n" +
                "            <input type=\"submit\" value=\"   Verify   \">\n" +
                "        </form>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
        TERMINAL_PAGE = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Console</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            background-color: #282c34;\n" +
                "            color: #abb2bf;\n" +
                "            margin: 0;\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            display: flex;\n" +
                "            justify-content: center;\n" +
                "            align-items: center;\n" +
                "            height: 100vh;\n" +
                "        }\n" +
                "\n" +
                "        #terminal {\n" +
                "            background-color: #21252b;\n" +
                "            padding: 40px;\n" +
                "            border-radius: 8px;\n" +
                "            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.5);\n" +
                "            width: 100%;\n" +
                "            max-width: 600px;\n" +
                "            text-align: left;\n" +
                "        }\n" +
                "\n" +
                "        #terminal-output {\n" +
                "            height: 200px;\n" +
                "            overflow-y: auto;\n" +
                "            margin-bottom: 20px;\n" +
                "            background-color: #1e2228;\n" +
                "            padding: 10px;\n" +
                "            border-radius: 4px;\n" +
                "            color: #98c379;\n" +
                "            font-family: monospace;\n" +
                "            font-size: 14px;\n" +
                "        }\n" +
                "\n" +
                "        #terminal-output p {\n" +
                "            margin: 0;\n" +
                "            line-height: 1.5;\n" +
                "        }\n" +
                "\n" +
                "        input[type=\"text\"] {\n" +
                "            background-color: transparent;\n" +
                "            border: 1px solid #abb2bf;\n" +
                "            color: #abb2bf;\n" +
                "            padding: 10px;\n" +
                "            font-size: 16px;\n" +
                "            border-radius: 4px;\n" +
                "            width: calc(100% - 22px);\n" +
                "        }\n" +
                "\n" +
                "        input[type=\"text\"]:focus {\n" +
                "            outline: none;\n" +
                "            border-color: #61afef;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div id=\"terminal\">\n" +
                "        <div id=\"terminal-output\">\n" +
                "            <p>MCI DataBase Console</p>\n" +
                "            <p>Type Enter to send commands.</p>\n" +
                "        </div>\n" +
                "        <form id=\"command-form\">\n" +
                "            <input type=\"text\" id=\"command-input\" placeholder=\"Type SQL command here.\">\n" +
                "        </form>\n" +
                "    </div>\n" +
                "\n" +
                "    <script>\n" +
                "        const form = document.getElementById('command-form');\n" +
                "        const input = document.getElementById('command-input');\n" +
                "        const output = document.getElementById('terminal-output');\n" +
                "\n" +
                "        const urlParams = new URLSearchParams(window.location.search);\n" +
                "        const token = urlParams.get('token');\n" +
                "\n" +
                "        form.addEventListener('submit', function(event) {\n" +
                "            event.preventDefault();\n" +
                "            const command = input.value.trim();\n" +
                "\n" +
                "            if (command) {\n" +
                "                // 显示命令到输出区域\n" +
                "                const commandOutput = document.createElement('p');\n" +
                "                commandOutput.textContent = `> ${command}`;\n" +
                "                output.appendChild(commandOutput);\n" +
                "\n" +
                "                fetch(`/terminal?token=${token}`, {\n" +
                "                    method: 'POST',\n" +
                "                    headers: {\n" +
                "                        'Content-Type': 'application/x-www-form-urlencoded',\n" +
                "                    },\n" +
                "                    body: `command=${encodeURIComponent(command)}`,\n" +
                "                })\n" +
                "                .then(response => {\n" +
                "                    if (response.redirected) {\n" +
                "                        window.location.href = response.url;\n" +
                "                    }\n" +
                "                    return response.text();\n" +
                "                })\n" +
                "                .then(data => {\n" +
                "                    const responseOutput = document.createElement('p');\n" +
                "                    responseOutput.textContent = `Server response: ${data}`;\n" +
                "                    output.appendChild(responseOutput);\n" +
                "                })\n" +
                "                .catch((error) => {\n" +
                "                    console.error('Error:', error);\n" +
                "                    const errorOutput = document.createElement('p');\n" +
                "                    errorOutput.textContent = `Error: ${error.message}`;\n" +
                "                    output.appendChild(errorOutput);\n" +
                "                });\n" +
                "\n" +
                "                output.scrollTop = output.scrollHeight;\n" +
                "            }\n" +
                "\n" +
                "            input.value = '';\n" +
                "        });\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }

    public static void main(String[] args) throws Exception {
        start(new File("E:\\Desktop\\Refactor_version\\run\\saves\\新的世界"));
    }
}
