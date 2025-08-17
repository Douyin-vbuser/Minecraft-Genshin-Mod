package com.vbuser.database.operate;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据库命令行控制台<br>
 * 提供交互式SQL命令执行环境
 */
public class Console {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Hello,SQL Boy.");
        while (true) {
            System.out.print("> ");
            String command = sc.nextLine();
            if (command.equals("exit")) {
                break;
            } else {
                System.out.println(executeCommand(command));
            }
        }
    }

    /**
     * 执行SQL命令
     * @param command SQL命令字符串
     * @return 命令执行结果
     */
    public static String executeCommand(String command) {
        command = command.toLowerCase();
        String result = "success";

        String[] commands = command.split(" ");
        try {
            // 数据库初始化命令
            if (commands[0].equals("init")) {
                dataBase = Init.initBase(commands[1], new File(commands[2]));
            }
            // 访问数据库命令
            else if (commands[0].equals("access")) {
                if ((new File(new File(commands[1]), "tables.txt")).exists()) {
                    System.out.println("[] Database " + new File(commands[1]).getName() + " is accessed");
                    dataBase = new File(commands[1]);
                } else {
                    result = "fail";
                }
            }
            // 创建表命令
            else if (commands[0].equals("create") && commands[1].equals("table")) {
                String[] columns = command.substring(command.indexOf('(') + 1, command.indexOf(')')).split(",");
                New.createTable(commands[2], columns, dataBase);
            }
            // 插入数据命令
            else if (commands[0].equals("insert") && commands[1].equals("into")) {
                String tableName = commands[2];
                String columnsPart = commands[3].substring(1, commands[3].length() - 1);
                String valuesPart = commands[5].substring(1, commands[5].length() - 1);
                String[] columns = columnsPart.split(",\\s*");
                String[] values = valuesPart.split(",\\s*");
                New.insert(tableName, columns, values, dataBase);
            }
            // 查询命令
            else if (commands[0].equals("select") && commands[1].equals("*") && commands[2].equals("from")) {
                result = result + "\n" + getResult(command);
            }
            // 删除命令
            else if (commands[0].equals("delete") && commands[1].equals("from")) {
                String tableName = commands[2];
                Pattern wherePattern = Pattern.compile("where\\s+(.*)");
                Matcher whereMatcher = wherePattern.matcher(command);
                String[] conditions = new String[0];
                if (whereMatcher.find()) {
                    conditions = whereMatcher.group(1).split("and\\s+");
                }
                Delete.deleteFrom(dataBase, tableName, conditions);
            }
            // 更新命令
            else if (commands[0].equals("update")) {
                String tableName = commands[1];
                Pattern setPattern = Pattern.compile("set\\s+(.*?)(?=\\s+where\\s+|\\Z)");
                Matcher setMatcher = setPattern.matcher(command);
                Map<String, String> columnValues = new HashMap<>();
                if (setMatcher.find()) {
                    String[] columnValue = setMatcher.group(1).split("and\\s+");
                    for (String s : columnValue) {
                        String[] values = s.split("=");
                        columnValues.put(values[0], values[1]);
                    }
                }
                Pattern wherePattern = Pattern.compile("where\\s+(.*)");
                Matcher whereMatcher = wherePattern.matcher(command);
                String[] conditions = new String[0];
                if (whereMatcher.find()) {
                    conditions = whereMatcher.group(1).split("and\\s+");
                }
                Update.updateTable(dataBase, tableName, columnValues.keySet().toArray(new String[0]), columnValues.values().toArray(new String[0]), conditions);
            }
            // 服务器管理命令
            else if (commands[0].equals("server")) {
                if (commands[1].equals("start")) {
                    thr_server = new Thread(() -> {
                        try {
                            Server.start(dataBase);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
                    thr_server.start();
                } else if (commands[1].equals("kill")) {
                    Server.server.stop(0);
                    Server.server = null;
                    thr_server.interrupt();
                } else {
                    System.out.println("[!] Invalid command \\[ o_x ]/");
                    result = "fail";
                }
            }
            // 备份管理命令
            else if (commands[0].equals("backup")) {
                try {
                    String output = Backup.handleBackupCommand(commands, dataBase);
                    System.out.println(output);
                } catch (Exception e) {
                    result = "fail";
                }
            } else {
                System.out.println("[!] Invalid command \\[ o_x ]/");
                result = "fail";
            }
        } catch (Exception e) {
            result = "fail";
        }
        return result;
    }

    // 当前访问的数据库目录
    public static File dataBase;
    // 服务器线程
    static Thread thr_server;

    /**
     * 获取查询结果
     * @param command 查询命令
     * @return 查询结果字符串
     */
    public static String getResult(String command) {
        command = command.toLowerCase();

        String[] commands = command.split(" ");
        String tableName = commands[3];
        Pattern wherePattern = Pattern.compile("where\\s+(.*)");
        Matcher whereMatcher = wherePattern.matcher(command);
        String[] conditions = new String[0];
        if (whereMatcher.find()) {
            conditions = whereMatcher.group(1).split("and\\s+");
        }
        String[] temp = Select.queryTable(dataBase, tableName, conditions).toArray(new String[0]);
        return String.join("\n", temp);
    }
}