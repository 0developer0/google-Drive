package com.example.drive.utils;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;

public class Utility {

    public static int HEIGHT, WIDTH;
    public static RequestOptions options;

    public static RequestOptions getOptions() {
        options = new RequestOptions()
                .format(DecodeFormat.PREFER_RGB_565)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);

        return options;
    }

    public static void getScreenSize(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        HEIGHT = displayMetrics.heightPixels;
        WIDTH = displayMetrics.widthPixels;
    }
}
