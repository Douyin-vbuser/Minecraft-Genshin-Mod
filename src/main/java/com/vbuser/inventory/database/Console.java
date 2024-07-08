package com.vbuser.inventory.database;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Console {
    public static void main(String[] args) throws Exception{
        Scanner sc = new Scanner(System.in);
        while(true){
            System.out.print("> ");
            String command = sc.nextLine();
            if(command.equals("exit")){
                break;
            }else {
                executeCommand(command);
            }
        }
    }

    public static void executeCommand(String command) throws IOException {
        command = command.toLowerCase();

        String[] commands = command.split(" ");
        if(commands[0].equals("init")){
            dataBase = Init.initBase(commands[1],new File(commands[2]));
        }
        else if(commands[0].equals("access")){
            dataBase = new File(commands[1]);
            if((new File(dataBase,"tables.txt")).exists()){
                System.out.println("[] Database " + dataBase.getName() + " is accessed");
            }
        }
        else if(commands[0].equals("create")&& commands[1].equals("table")){
            String[] columns = command.substring(command.indexOf('(') + 1, command.indexOf(')')).split(",");
            New.createTable(commands[2],columns,dataBase);
        }
        else if(commands[0].equals("insert")&& commands[1].equals("into")){
            String tableName = commands[2];
            String columnsPart = commands[3].substring(1, commands[3].length() - 1);
            String valuesPart = commands[5].substring(1, commands[5].length() - 1);
            String[] columns = columnsPart.split(",\\s*");
            String[] values = valuesPart.split(",\\s*");
            New.insert(tableName, columns, values,dataBase);
        }
        else if(commands[0].equals("select") && commands[1].equals("*") && commands[2].equals("from")){
            String tableName = commands[3];
            Pattern wherePattern = Pattern.compile("where\\s+(.*)");
            Matcher whereMatcher = wherePattern.matcher(command);
            String[] conditions = new String[0];
            if (whereMatcher.find()) {
                conditions = whereMatcher.group(1).split("and\\s+");
            }
            Select.queryTable(dataBase, tableName, conditions);
        }
        else if(commands[0].equals("delete") && commands[1].equals("from")){
            String tableName = commands[2];
            Pattern wherePattern = Pattern.compile("where\\s+(.*)");
            Matcher whereMatcher = wherePattern.matcher(command);
            String[] conditions = new String[0];
            if(whereMatcher.find()){
                conditions = whereMatcher.group(1).split("and\\s+");
            }
            Delete.deleteFrom(dataBase, tableName, conditions);
        }
        else if(commands[0].equals("update")){
            String tableName = commands[1];
            Pattern setPattern = Pattern.compile("set\\s+(.*?)(?=\\s+where\\s+|\\Z)");
            Matcher setMatcher = setPattern.matcher(command);
            Map<String,String> columnValues = new HashMap<>();
            if(setMatcher.find()){
                String[] columnValue = setMatcher.group(1).split("and\\s+");
                for(String s : columnValue){
                    String[] values = s.split("=");
                    columnValues.put(values[0], values[1]);
                }
            }
            Pattern wherePattern = Pattern.compile("where\\s+(.*)");
            Matcher whereMatcher = wherePattern.matcher(command);
            String[] conditions = new String[0];
            if(whereMatcher.find()){
                conditions = whereMatcher.group(1).split("and\\s+");
            }
            Update.updateTable(dataBase, tableName, columnValues.keySet().toArray(new String[0]), columnValues.values().toArray(new String[0]), conditions);
        }
        else{
            System.out.println("[!] Invalid command");
        }
    }

    private static File dataBase;
}
