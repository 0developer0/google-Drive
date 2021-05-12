package com.example.drive.ui.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.drive.utils.Utility;
import com.example.drive.models.Img;
import com.example.drive.databinding.ItemImageSliderBinding;

import java.util.ArrayList;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ImageSliderHolder> {
    private ArrayList<Img> images;

    private RequestOptions options;
    private Context context;

    public ImageSliderAdapter(ArrayList<Img> images, Context context) {
        this.images = images;
        this.context = context;

        options = Utility.getOptions();
    }

    @NonNull
    @Override
    public ImageSliderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemImageSliderBinding binding = ItemImageSliderBinding.inflate(inflater, parent, false);
        return new ImageSliderHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageSliderHolder holder, int position) {
        Img image = images.get(position);

        Glide.with(context)
                .load(image.getContentUri())
                .apply(options)
                .fitCenter()
                .error(android.R.drawable.ic_menu_close_clear_cancel)
                .into(holder.binding.imgSlide);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public static class ImageSliderHolder extends RecyclerView.ViewHolder {
        ItemImageSliderBinding binding;

        public ImageSliderHolder(@NonNull ItemImageSliderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}