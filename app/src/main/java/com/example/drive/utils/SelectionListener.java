package com.example.drive.utils;

import com.example.drive.models.Img;

public interface SelectionListener {
    void onImageClicked(Img img, int position);

    void onImageLongClicked(Img img, int position);
}
