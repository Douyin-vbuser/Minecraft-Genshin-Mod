package com.vbuser.ime;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

@SuppressWarnings("all")
public class IMEController {
    public static void load(String version) {
        try {
            System.loadLibrary("ime_controller");
            System.out.println("[] Plugin detected.");
        } catch (UnsatisfiedLinkError e) {
            System.out.println("[] Detected IME controller not installed.");
            String libraryName = System.mapLibraryName("ime_controller");
            String libraryPath = getLibraryPath(libraryName);

            if (libraryPath != null) {
                if (!new File(libraryPath).exists()) {
                    downloadLibrary(libraryPath,version);
                }
                try {
                    System.load(libraryPath);
                } catch (UnsatisfiedLinkError ex) {
                    ex.printStackTrace();
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            load(version);
        }
    }

    private static String getLibraryPath(String libraryName) {
        String[] paths = System.getProperty("java.library.path").split(File.pathSeparator);
        for (String path : paths) {
            File directory = new File(path);
            if (directory.canWrite()) {
                return new File(directory, libraryName).getAbsolutePath();
            }
        }
        return null;
    }

    private static void downloadLibrary(String destination,String version) {
        String baseUrl = "https://github.com/Douyin-vbuser/IME/releases/download/"+version+"/ime_controller";
        String os = System.getProperty("os.name").toLowerCase();
        System.out.printf("[] Downloading plugin for %s%n", System.getProperty("os.name"));
        String url = baseUrl;

        if (os.contains("windows")) {
            url += ".dll";
        } else if (os.contains("mac")) {
            url += ".dylib";
        } else if (os.contains("linux")) {
            url += ".so";
        }

        try {
            URL downloadUrl = new URL(url);
            try (InputStream in = downloadUrl.openStream();
                 OutputStream out = Files.newOutputStream(Paths.get(destination))) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            new File(destination).setExecutable(true);
        } catch (IOException e) {
            System.out.println("[] Download failed.Retrying.");
        }
    }

    public static native void toggleIME(boolean enable);

}