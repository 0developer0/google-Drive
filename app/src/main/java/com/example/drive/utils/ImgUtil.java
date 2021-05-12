package com.example.drive.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.drive.models.Img;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;

public class ImgUtil {

    public static String getDateDifference(Calendar calendar) {
        Date imageDate = calendar.getTime();
        Calendar lastMonth = Calendar.getInstance();
        Calendar lastWeek = Calendar.getInstance();
        Calendar recent = Calendar.getInstance();

        lastMonth.add(Calendar.DAY_OF_MONTH, -(Calendar.DAY_OF_MONTH));
        lastWeek.add(Calendar.DAY_OF_MONTH, -7);
        recent.add(Calendar.DAY_OF_MONTH, -2);

        if (calendar.before(lastMonth)) {
            return new SimpleDateFormat("MMMM", Locale.getDefault()).format(imageDate);
        } else if (calendar.after(lastMonth) && calendar.before(lastWeek)) {
            return "Last Month";
        } else if (calendar.after(lastWeek) && calendar.before(recent)) {
            return "Last Week";
        } else {
            return "Recent";
        }
    }

    public static ArrayList<File> getFilesFromImgObjs(ArrayList<Img> images) {
        ArrayList<File> mFiles = new ArrayList<>();

        for (int i = 0; i < images.size(); i++) {
            Uri uri = Uri.parse(images.get(i).getContentUri());
            File file = new File(uri.getPath());
            mFiles.add(file);
        }

        return mFiles;
    }

    public static ArrayList<Img> getImgObjsFromFiles(ArrayList<File> files) {
        ArrayList<Img> mImages = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            Img image = new Img(file.getName(), file.getPath(),true);
            mImages.add(image);
        }

        return mImages;
    }

    public static String getImageName(Uri imageUri) {
        return imageUri.getPath().substring(0, imageUri.getPath().lastIndexOf('.'));
    }
}
