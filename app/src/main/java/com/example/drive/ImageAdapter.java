package com.example.drive;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private List<Image> images = new ArrayList<>();

    public ImageAdapter(List<Image> images) {
        this.images = images;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.bindView(images.get(position));
    }

    public void addItem(Image image){
        images.add(image);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        TextView tv_FileName, tv_Uploaded;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_FileName = itemView.findViewById(R.id.tv_name);
            tv_Uploaded = itemView.findViewById(R.id.tv_uploaded);
        }

        public void bindView(Image image){
            tv_FileName.setText(getFilename(image.getImageUri()));
            tv_Uploaded.setText(image.getDateOfUploaded());
        }

        private String getFilename(String imageUri) {
            String path[] = imageUri.split("/");
            return path[path.length - 1];
        }
    }
}
