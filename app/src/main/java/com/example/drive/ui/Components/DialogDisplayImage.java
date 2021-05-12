package com.example.drive.ui.Components;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.drive.R;
import com.example.drive.databinding.DialogDisplayImageBinding;
import com.example.drive.utils.Utility;

import java.io.File;

public class DialogDisplayImage extends DialogFragment {

    private DialogDisplayImageBinding binding;

    private String imageUri;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //ViewBinding Setup
        binding = DialogDisplayImageBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();

        //AlertDialog Setup
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(root);

        //get Data From Bundle
        Bundle bundle = getArguments();
        imageUri = bundle.getString("imageUri");
        Uri uri = Uri.parse(imageUri);
        File file = new File(uri.getPath());

        binding.image.setImageURI(uri);

        return builder.create();
    }
}