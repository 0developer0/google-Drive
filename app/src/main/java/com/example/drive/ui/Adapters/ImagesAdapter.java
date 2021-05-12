package com.example.drive.ui.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.drive.R;
import com.example.drive.utils.SelectionListener;
import com.example.drive.utils.Utility;
import com.example.drive.models.Img;
import com.example.drive.databinding.ItemImageBinding;

import java.util.ArrayList;
import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ItemHolder> {
    private List<Img> images;

    private SelectionListener listener;

    private Context context;
    private boolean isSelectable;

    private RequestOptions options;

    public static final int SPAN_COUNT = 4;
    private static final int MARGIN = 4;

    public ImagesAdapter(ArrayList<Img> images, Context context, SelectionListener listener) {
        this.images = images;
        this.context = context;
        this.listener = listener;

        options = Utility.getOptions();
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemImageBinding binding = ItemImageBinding.inflate(inflater, parent, false);
        return new ItemHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        Img image = images.get(position);

        Glide.with(context)
                .load(image.getContentUri())
                .apply(options)
                .into(holder.binding.imgPreview);

        if(image.getSelected()) {
            holder.binding.imgPreview.setPadding(16, 16, 16, 16);
            holder.binding.imgSelection.setVisibility(View.VISIBLE);
        } else {
            holder.binding.imgPreview.setPadding(0, 0, 0, 0);
            holder.binding.imgSelection.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    @Override
    public long getItemId(int position) {
        return images.get(position).getContentUri().hashCode();
    }

    public void select(boolean isSelected, int position) {
        images.get(position).setSelected(isSelected);
        notifyItemChanged(position);
    }

    public void unselected() {
        for (int i = 0; i < images.size(); i++) {
            if (images.get(i).getSelected()) {
               select(false, i);
            }
        }
    }

    public boolean isSelectable() {
        return isSelectable;
    }

    public void setSelectable(boolean isSelectable) {
        this.isSelectable = isSelectable;
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener ,View.OnLongClickListener {
        ItemImageBinding binding;

        public ItemHolder(@NonNull ItemImageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.imgPreview.setOnClickListener(this);
            binding.imgPreview.setOnLongClickListener(this);

            int size = (Utility.WIDTH / SPAN_COUNT) - (MARGIN / 2);
            binding.imgPreview.getLayoutParams().width = size;
            binding.imgPreview.getLayoutParams().height = size;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.img_preview:
                    int position = this.getLayoutPosition();
                    listener.onImageClicked(images.get(position), position);
                    break;
            }
        }

        @Override
        public boolean onLongClick(View view) {
            switch (view.getId()) {
                case R.id.img_preview:
                    int position = this.getLayoutPosition();
                    listener.onImageLongClicked(images.get(position), position);
                    break;
            }

            return true;
        }
    }
}