package com.example.drive.utils;

import android.content.Context;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileFilter;
//import org.apache.commons.lang3.RandomStringUtils;

public class FileManager {

    public static final int TEMP_DIR = 1;

    private File fileDir;
    private Context mContext;

    private static final String TEMP_DIR_STR = "temp/";

    public static final String JPG_TYPE = ".jpg";

    public FileManager (Context context, int dir) {
        this.mContext = context;
        switch (dir) {
            case TEMP_DIR:
                this.fileDir = mContext.getExternalFilesDir(TEMP_DIR_STR);
                break;
        }
    }

    public File getFileDir() {
        return fileDir;
    }

    public String createRandomFileName(String prefix, String suffix) {
//        String randomString = RandomStringUtils.random(5, false, true);
        return prefix + System.currentTimeMillis() + suffix;
    }

    public void clearDir() {
        File[] fList = fileDir.listFiles();
        if (fList != null && fileDir.isDirectory()) {
            try {
                FileUtils.cleanDirectory(fileDir);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void clearCache() {
        File[] fList = getFileDir().listFiles();
        if (fList != null && getFileDir().isDirectory()) {
            try {
                FileUtils.cleanDirectory(getFileDir());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}