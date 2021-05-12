package com.example.drive.ui.Adapters;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drive.models.UploadResponse;
import com.example.drive.utils.ItemEventListener;
import com.example.drive.R;
import com.example.drive.models.Img;
import com.example.drive.databinding.ItemUploadedImageBinding;

import java.util.List;

public class UploadedImageAdapter extends RecyclerView.Adapter<UploadedImageAdapter.ImageViewHolder> {
    private List<Img> images;

    private ItemEventListener listener;

    public UploadedImageAdapter(List<Img> images, ItemEventListener listener) {
        this.images = images;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemUploadedImageBinding binding = ItemUploadedImageBinding.inflate(layoutInflater, parent, false);
        return new ImageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        if (images != null) {
            Img image = images.get(position);

            holder.binding.tvFilename.setText(image.getImageName());

            if (!image.getUploading()) {

                if (image.getSuccessful() != null) {
                    if (!image.getSuccessful()) {

                        //UnSuccessful Upload State
                        holder.binding.imgUploadingState.setVisibility(View.VISIBLE);
                        holder.binding.pgUploading.setVisibility(View.INVISIBLE);
                        holder.binding.imgUploadingState.setBackgroundResource(R.drawable.ic_baseline_replay_24);
                        holder.binding.imgFormat.setVisibility(View.INVISIBLE);
                        holder.binding.tvDateOfUploaded.setText(" ");

                    }
                } else {

                    //Normal State
                    holder.binding.tvDateOfUploaded.setText("Uploaded " + image.getDateOfModified());
                    holder.binding.imgFormat.setVisibility(View.VISIBLE);

                }
            } else {

                //Uploading State
                holder.binding.imgUploadingState.setVisibility(View.INVISIBLE);
                holder.binding.imgFormat.setVisibility(View.INVISIBLE);
                holder.binding.pgUploading.setVisibility(View.VISIBLE);
                holder.binding.tvDateOfUploaded.setText("Uploading...");

            }
        }
    }

    public void addItem(Img image, int position) {
        images.add(position, image);
        notifyItemInserted(position);
    }

    public void filteredList(List<Img> images) {
        this.images = images;
        notifyDataSetChanged();
    }

    public void uploadedImages(List<Img> images) {
        this.images.clear();
        this.images.addAll(images);
        notifyDataSetChanged();
    }

    public void uploadingResult(String pathUri, @Nullable String dateOfUploaded, boolean isSuccessful) {
        for (int i = 0; i < images.size(); i++) {
            Img image = images.get(i);
            if (image.getContentUri().equals(pathUri)) {
                image.setUploading(false);
                image.setSuccessful(isSuccessful);
                if (isSuccessful) {
                    image.setDateOfModified(dateOfUploaded);
                }

                notifyItemChanged(i);
                return;
            }
        }
    }

    public void startUploading(int position) {
        images.get(position).setUploading(true);
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    @Override
    public long getItemId(int position) {
        return images.get(position).getContentUri().hashCode();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemUploadedImageBinding binding;

        public ImageViewHolder(@NonNull ItemUploadedImageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.consItem.setOnClickListener(this);
            binding.imgUploadingState.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = this.getLayoutPosition();
            Img image = images.get(position);
            switch (view.getId()) {
                case R.id.cons_item:
                    if(!image.getUploading()) {
                        listener.onUploadedImageClicked(image);
                    }
                    break;
                case R.id.img_uploading_state:
                    listener.onStateUploadingClicked(image, position);
                    break;
            }
        }
    }
}