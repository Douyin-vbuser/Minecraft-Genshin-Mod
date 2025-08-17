package com.vbuser.database.operate;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.vbuser.database.operate.Console.executeCommand;
import static com.vbuser.database.operate.ToTP.validateTOTP;

/**
 * HTTP数据库服务器<br>
 * 提供Web界面访问数据库功能<br>
 * 支持TOTP作为操作确权
 */
public class Server {

    static int PORT = 11451;  // 服务器端口
    private static String TOTP_PAGE;   // TOTP认证页面HTML
    private static String TERMINAL_PAGE; // 终端页面HTML
    private static final Map<String, Long> tokenStore = new HashMap<>(); // 有效token存储
    private static String key;  // TOTP密钥
    public static HttpServer server;   // HTTP服务器实例

    /**
     * 启动HTTP服务器
     * @param dir 数据库目录
     */
    public static void start(File dir) throws Exception {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        setHTMLContent();  // 初始化HTML页面

        // 读取TOTP密钥
        File file = new File(dir, "totp.txt");
        key = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);

        // 定时清理过期token
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(60 * 1000); // 每分钟清理一次
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                tokenStore.entrySet().removeIf(entry -> System.currentTimeMillis() > entry.getValue());
            }
        }).start();

        // 设置请求处理器
        server.createContext("/", exchange -> {
            try {
                String requestMethod = exchange.getRequestMethod();
                if (requestMethod.equalsIgnoreCase("GET")) {
                    handleGetRequest(exchange, dir);
                } else if (requestMethod.equalsIgnoreCase("POST")) {
                    handlePostRequest(exchange, dir);
                } else {
                    exchange.sendResponseHeaders(405, 0); // 方法不允许
                    exchange.close();
                }
            } catch (Exception e) {
                exchange.sendResponseHeaders(500, -1); // 服务器错误
                exchange.close();
            }
        });

        server.setExecutor(null);
        server.start();
        System.out.println("Server is listening on port " + PORT);
    }

    /**
     * 处理GET请求
     * @param exchange HTTP交换对象
     * @param dir 数据库目录
     */
    private static void handleGetRequest(HttpExchange exchange, File dir) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if (path.equals("/")) {
            // 重定向到TOTP认证页
            exchange.getResponseHeaders().set("Location", "/totp");
            exchange.sendResponseHeaders(302, -1);
            exchange.close();
        } else if (path.equals("/totp")) {
            sendResponse(exchange, TOTP_PAGE);
        } else if (path.startsWith("/terminal")) {
            String token = exchange.getRequestURI().getQuery().replace("token=", "");
            // 验证token有效性
            if (isTokenValid(token)) {
                exchange.getResponseHeaders().set("Location", "/totp");
                exchange.sendResponseHeaders(302, -1);
                exchange.close();
                return;
            }
            sendResponse(exchange, TERMINAL_PAGE);
        } else {
            // 处理表数据请求
            path = path.substring(1);
            File file = new File(dir, "tables/" + path);
            String htmlResponse = Convert.convertTxtToHtmlTable(file.getAbsolutePath());
            sendResponse(exchange, htmlResponse);
        }
    }

    /**
     * 生成新token
     * @return 生成的token
     */
    private static String generateToken() {
        String uuid = UUID.randomUUID().toString();
        tokenStore.put(uuid, System.currentTimeMillis() + 5 * 60 * 1000); // 5分钟有效期
        return uuid;
    }

    /**
     * 处理POST请求
     * @param exchange HTTP交换对象
     * @param db 数据库目录
     */
    private static void handlePostRequest(HttpExchange exchange, File db) throws IOException {
        if (exchange.getRequestURI().getPath().equals("/totp")) {
            // 处理TOTP认证
            String totpCode = getString(exchange, "code");
            if (!validateTOTP(key, totpCode)) {
                // 认证失败，重定向回认证页
                exchange.getResponseHeaders().set("Location", "/totp");
                exchange.sendResponseHeaders(302, -1);
            } else {
                // 认证成功，生成token并重定向到终端
                String token = generateToken();
                String redirectUrl = "/terminal?token=" + token;
                exchange.getResponseHeaders().set("Location", redirectUrl);
                exchange.sendResponseHeaders(302, -1);
            }
            exchange.close();
        } else if (exchange.getRequestURI().getPath().contains("terminal")) {
            // 处理终端命令
            String token = exchange.getRequestURI().getQuery().replace("token=", "");
            if (isTokenValid(token)) {
                // token无效，重定向到认证页
                exchange.getResponseHeaders().set("Location", "/totp");
                exchange.sendResponseHeaders(302, -1);
                exchange.close();
            } else {
                // 执行命令
                executeCommand("access " + db.getAbsolutePath());
                String command = getString(exchange, "command");
                command = command.replaceAll("%20", " ").replaceAll("%3D", "=");
                String result = executeCommand(command);
                String response = result != null ? result : "success";
                sendResponse(exchange, response);
                System.out.println("executing:" + command + "\nresult: " + response);
            }
        } else {
            exchange.sendResponseHeaders(404, -1); // 未找到
            exchange.close();
        }
    }

    /**
     * 从请求体获取字符串值
     * @param exchange HTTP交换对象
     * @param key 参数键
     * @return 参数值
     */
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

    /**
     * 检查token有效性
     * @param token 待检查的token
     * @return 是否有效
     */
    private static boolean isTokenValid(String token) {
        return !tokenStore.containsKey(token);
    }

    /**
     * 发送HTTP响应
     * @param exchange HTTP交换对象
     * @param response 响应内容
     */
    private static void sendResponse(HttpExchange exchange, String response) throws IOException {
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, responseBytes.length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(responseBytes);
        outputStream.close();
    }

    /**
     * HTML页面内容
     */
    public static void setHTMLContent() {
        // TOTP认证页面
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
                "        }\n    .command {\n" +
                "        color: #61afef;         /* 用户输入命令显示为蓝色 */\n" +
                "    }\n" +
                "    .result {\n" +
                "        color: #98c379;         /* 服务器结果显示为绿色 */\n" +
                "    }" +
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
                "            if (command) {\n" +
                "                // 显示用户输入的命令（蓝色）\n" +
                "                const commandElement = document.createElement('p');\n" +
                "                //const resultElement = document.createElement('p');\n" +
                "                commandElement.textContent = `> ${command}`;\n" +
                "                commandElement.className = 'command';\n" +
                "                output.appendChild(commandElement);\n" +
                "\n" +
                "                fetch(`/terminal?token=${token}`, {\n" +
                "                    method: 'POST',\n" +
                "                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },\n" +
                "                    body: `command=${encodeURIComponent(command)}`,\n" +
                "                })\n" +
                "                .then(response => {\n" +
                "                    if (response.redirected) {\n" +
                "                        window.location.href = response.url;\n" +
                "                        return;\n" +
                "                    }\n" +
                "                    return response.text();\n" +
                "                })\n" +
                "                .then(data => {\n" +
                "                    // 显示服务器返回的结果（绿色）\n" +
                "                    const resultElement = document.createElement('p');  // 使用pre保留格式\n" +
                "                    resultElement.textContent = data;\n" +
                "                    resultElement.className = 'result';\n" +
                "                    output.appendChild(resultElement);\n" +
                "                    output.scrollTop = output.scrollHeight;\n" +
                "                })\n" +
                "                .catch(error => {\n" +
                "                    const errorElement = document.createElement('p');\n" +
                "                    errorElement.textContent = `Error: ${error.message}`;\n" +
                "                    errorElement.style.color = '#e06c75';  // 错误信息显示为红色\n" +
                "                    output.appendChild(errorElement);\n" +
                "                });\n" +
                "                input.value = '';\n" +
                "            }\n" +
                "        });\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }
}