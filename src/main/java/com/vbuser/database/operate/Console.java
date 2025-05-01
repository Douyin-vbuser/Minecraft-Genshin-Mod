package com.vbuser.database.operate;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static String executeCommand(String command) {
        command = command.toLowerCase();
        String result = "success";

        String[] commands = command.split(" ");
        try {
            if (commands[0].equals("init")) {
                dataBase = Init.initBase(commands[1], new File(commands[2]));
            } else if (commands[0].equals("access")) {
                if ((new File(new File(commands[1]), "tables.txt")).exists()) {
                    System.out.println("[] Database " + new File(commands[1]).getName() + " is accessed");
                    dataBase = new File(commands[1]);
                } else {
                    result = "fail";
                }
            } else if (commands[0].equals("create") && commands[1].equals("table")) {
                String[] columns = command.substring(command.indexOf('(') + 1, command.indexOf(')')).split(",");
                New.createTable(commands[2], columns, dataBase);
            } else if (commands[0].equals("insert") && commands[1].equals("into")) {
                String tableName = commands[2];
                String columnsPart = commands[3].substring(1, commands[3].length() - 1);
                String valuesPart = commands[5].substring(1, commands[5].length() - 1);
                String[] columns = columnsPart.split(",\\s*");
                String[] values = valuesPart.split(",\\s*");
                New.insert(tableName, columns, values, dataBase);
            } else if (commands[0].equals("select") && commands[1].equals("*") && commands[2].equals("from")) {
                result = result + "\n" + getResult(command);
            } else if (commands[0].equals("delete") && commands[1].equals("from")) {
                String tableName = commands[2];
                Pattern wherePattern = Pattern.compile("where\\s+(.*)");
                Matcher whereMatcher = wherePattern.matcher(command);
                String[] conditions = new String[0];
                if (whereMatcher.find()) {
                    conditions = whereMatcher.group(1).split("and\\s+");
                }
                Delete.deleteFrom(dataBase, tableName, conditions);
            } else if (commands[0].equals("update")) {
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
            } else if (commands[0].equals("server")) {
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
                    thr_server.interrupt();
                } else {
                    System.out.println("[!] Invalid command \\[ o_x ]/");
                    result = "fail";
                }
            } else if (commands[0].equals("backup")) {
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

    public static File dataBase;
    static Thread thr_server;

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
