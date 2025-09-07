//本文件包含AI辅助生成内容
//AI辅助编程服务提供商:DeepSeek
//AI注释服务提供商:DeepSeek

package com.vbuser.database.operate;

import com.vbuser.database.operate.huffman.HuffmanCoder;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 数据库备份管理工具<br>
 * 提供备份创建、删除、列表查看和恢复功能<br>
 * 使用哈夫曼压缩算法减少备份存储空间
 */
public class Backup {

    /**
     * 创建表备份
     * @param tableName 表名称
     * @param dataBase 数据库目录
     * @return 操作结果消息
     */
    public static String makeBackup(String tableName, File dataBase) {
        try {
            // 生成时间戳格式的备份目录
            String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            File tableBackupDir = new File(new File(dataBase, "backups"), tableName);
            File timestampDir = new File(tableBackupDir, timestamp);

            // 创建备份目录结构
            if (!timestampDir.exists()) {
                timestampDir.mkdirs();
            }

            // 获取源表文件
            File sourceFile = new File(new File(dataBase, "tables"), tableName + ".txt");

            // 使用哈夫曼压缩创建备份
            String compressedFile = new File(timestampDir, tableName + ".bin").getAbsolutePath();
            HuffmanCoder.compress(sourceFile.getAbsolutePath(), compressedFile);

            // 更新备份清单
            File manifest = new File(tableBackupDir, "manifest.txt");
            try (FileWriter writer = new FileWriter(manifest, true)) {
                writer.write(timestamp + "\n");
            }

            return "[] Backup created for table " + tableName + " at " + timestamp;
        } catch (Exception e) {
            return "[!] Error creating backup: " + e.getMessage();
        }
    }

    /**
     * 删除指定时间点的备份
     * @param tableName 表名称
     * @param timestamp 备份时间戳
     * @param dataBase 数据库目录
     * @return 操作结果消息
     */
    public static String deleteBackup(String tableName, String timestamp, File dataBase) {
        try {
            File backupDir = new File(new File(new File(dataBase, "backups"), tableName), timestamp);
            if (!backupDir.exists()) {
                return "[!] Backup not found";
            }

            // 删除备份文件
            File[] files = backupDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }

            // 删除备份目录
            backupDir.delete();

            // 更新备份清单
            File manifest = new File(new File(new File(dataBase, "backups"), tableName), "manifest.txt");
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

    /**
     * 列出所有可用备份
     * @param dataBase 数据库目录
     * @return 备份列表信息
     */
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

    /**
     * 使用备份覆盖当前表
     * @param tableName 表名称
     * @param timestamp 备份时间戳
     * @param dataBase 数据库目录
     * @return 操作结果消息
     */
    public static String overwriteWithBackup(String tableName, String timestamp, File dataBase) {
        try {
            File backupDir = new File(new File(new File(dataBase, "backups"), tableName), timestamp);
            if (!backupDir.exists()) {
                return "[!] Backup not found";
            }

            // 准备解压文件路径
            File compressedFile = new File(backupDir, tableName + ".bin");
            File treeFile = new File(compressedFile.getAbsolutePath() + ".tree");
            File outputFile = new File(new File(dataBase, "tables"), tableName + ".txt");

            // 使用哈夫曼解压恢复表
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

    /**
     * 处理备份相关命令
     * @param commands 命令数组
     * @param dataBase 数据库目录
     * @return 命令执行结果
     */
    public static String handleBackupCommand(String[] commands, File dataBase) {
        if (commands.length < 2) {
            return "[!] Invalid backup command";
        }

        // 根据子命令分发处理
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