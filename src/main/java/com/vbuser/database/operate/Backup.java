package com.vbuser.database.operate;

import com.vbuser.database.operate.huffman.HuffmanCoder;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;

public class Backup {

    public static String makeBackup(String tableName, File dataBase) {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            File tableBackupDir = new File(dataBase, "backups/" + tableName);
            File timestampDir = new File(tableBackupDir, timestamp);

            if (!timestampDir.exists()) {
                timestampDir.mkdirs();
            }

            File sourceFile = new File(dataBase, "tables/" + tableName + ".txt");

            String compressedFile = new File(timestampDir, tableName + ".bin").getAbsolutePath();
            HuffmanCoder.compress(sourceFile.getAbsolutePath(), compressedFile);

            File manifest = new File(tableBackupDir, "manifest.txt");
            try (FileWriter writer = new FileWriter(manifest, true)) {
                writer.write(timestamp + "\n");
            }

            return "[] Backup created for table " + tableName + " at " + timestamp;
        } catch (Exception e) {
            return "[!] Error creating backup: " + e.getMessage();
        }
    }

    public static String deleteBackup(String tableName, String timestamp, File dataBase) {
        try {
            File backupDir = new File(dataBase, "backups/" + tableName + "/" + timestamp);
            if (!backupDir.exists()) {
                return "[!] Backup not found";
            }

            File[] files = backupDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }

            backupDir.delete();

            File manifest = new File(dataBase, "backups/" + tableName + "/manifest.txt");
            if (manifest.exists()) {
                List<String> timestamps = Files.readAllLines(manifest.toPath());
                timestamps.remove(timestamp);
                Files.write(manifest.toPath(), timestamps);
            }

            return "[] Backup deleted for table " + tableName + " at " + timestamp;
        } catch (Exception e) {
            return "[!] Error deleting backup: " + e.getMessage();
        }
    }

    public static String listBackups(File dataBase) {
        try {
            File backupsDir = new File(dataBase, "backups");
            if (!backupsDir.exists()) {
                return "[] No backups available";
            }

            StringBuilder result = new StringBuilder("Available backups:\n");
            File[] tables = backupsDir.listFiles();
            if (tables != null) {
                for (File tableDir : tables) {
                    result.append("Table: ").append(tableDir.getName()).append("\n");
                    File manifest = new File(tableDir, "manifest.txt");
                    if (manifest.exists()) {
                        List<String> timestamps = Files.readAllLines(manifest.toPath());
                        for (String timestamp : timestamps) {
                            result.append("  - ").append(timestamp).append("\n");
                        }
                    }
                }
            }
            return result.toString();
        } catch (Exception e) {
            return "[!] Error listing backups: " + e.getMessage();
        }
    }

    public static String overwriteWithBackup(String tableName, String timestamp, File dataBase) {
        try {
            File backupDir = new File(dataBase, "backups/" + tableName + "/" + timestamp);
            if (!backupDir.exists()) {
                return "[!] Backup not found";
            }

            File compressedFile = new File(backupDir, tableName + ".bin");
            File treeFile = new File(compressedFile.getAbsolutePath() + ".tree");
            File outputFile = new File(dataBase, "tables/" + tableName + ".txt");

            HuffmanCoder.decompress(
                    compressedFile.getAbsolutePath(),
                    outputFile.getAbsolutePath(),
                    treeFile.getAbsolutePath()
            );

            return "[] Table " + tableName + " overwritten with backup from " + timestamp;
        } catch (Exception e) {
            return "[!] Error overwriting with backup: " + e.getMessage();
        }
    }

    public static String handleBackupCommand(String[] commands, File dataBase) {
        if (commands.length < 2) {
            return "[!] Invalid backup command";
        }

        switch (commands[1]) {
            case "make":
                if (commands.length < 3) return "[!] Missing table name";
                return makeBackup(commands[2], dataBase);
            case "delete":
                if (commands.length < 4) return "[!] Missing table name or timestamp";
                return deleteBackup(commands[2], commands[3], dataBase);
            case "list":
                return listBackups(dataBase);
            case "overwrite":
                if (commands.length < 4) return "[!] Missing table name or timestamp";
                return overwriteWithBackup(commands[2], commands[3], dataBase);
            default:
                return "[!] Invalid backup command";
        }
    }
}
