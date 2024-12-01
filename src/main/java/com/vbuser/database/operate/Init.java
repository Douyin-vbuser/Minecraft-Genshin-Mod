package com.vbuser.database.operate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@SuppressWarnings("all")
public class Init {
    public static File initBase(String name, File directory) throws IOException {
        File file = new File(directory, name);
        file.mkdir();
        new File(directory, name + "\\tables.txt").createNewFile();
        new File(directory, name + "\\tables").mkdir();
        new File(directory, name + "\\backups").mkdir();
        new File(directory, name + "\\totp.txt").createNewFile();

        FileWriter fileWriter = new FileWriter(new File(directory, name + "\\totp.txt"));
        fileWriter.write(ToTP.generateSecretKey());
        fileWriter.close();
        System.out.println("[] Database " + name + " created");

        return file;
    }
}
