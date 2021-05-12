package com.example.drive.utils;

import com.example.drive.models.Img;

public interface ItemEventListener {
    void onUploadedImageClicked(Img image);

    void onStateUploadingClicked(Img image, int position);
}
