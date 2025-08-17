package com.vbuser.database.operate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 数据库初始化工具<br>
 * 创建数据库目录结构和必要文件
 */
@SuppressWarnings("all")
public class Init {

    /**
     * 初始化新数据库
     * @param name 数据库名称
     * @param directory 父目录
     * @return 创建的数据库目录
     */
    public static File initBase(String name, File directory) throws IOException {
        // 创建数据库目录结构
        File dbDir = new File(directory, name);
        dbDir.mkdir();
        new File(dbDir, "tables.txt").createNewFile();
        new File(dbDir, "tables").mkdir();
        new File(dbDir, "backups").mkdir();
        File totp = new File(dbDir, "totp.txt");
        totp.createNewFile();

        // 生成并写入TOTP密钥
        FileWriter fileWriter = new FileWriter(totp);
        fileWriter.write(ToTP.generateSecretKey());
        fileWriter.close();
        System.out.println("[] Database " + name + " created");

        return dbDir;
    }
}