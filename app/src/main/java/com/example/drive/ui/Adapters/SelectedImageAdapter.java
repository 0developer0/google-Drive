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
import com.example.drive.utils.Utility;
import com.example.drive.models.Img;
import com.example.drive.databinding.ItemSelectedImageBinding;

import java.util.ArrayList;

public class SelectedImageAdapter extends RecyclerView.Adapter<SelectedImageAdapter.SelectedImageHolder> {
    private ArrayList<Img> images;

    private ImageEventListener listener;

    private int prevItemPosition = 0;

    private RequestOptions options;
    private Context context;

    public SelectedImageAdapter(ArrayList<Img> images, ImageEventListener listener, Context context) {
        this.images = images;
        this.listener = listener;
        this.context = context;

        options = Utility.getOptions();
    }

    @NonNull
    @Override
    public SelectedImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemSelectedImageBinding binding = ItemSelectedImageBinding.inflate(inflater, parent, false);
        return new SelectedImageHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedImageHolder holder, int position) {
        Img image = images.get(position);

        Glide.with(context)
                .load(image.getContentUri())
                .apply(options)
                .error(android.R.drawable.ic_menu_close_clear_cancel)
                .into(holder.binding.image);

        if (image.getCurrentItem()) {
            holder.binding.image.setPadding(2, 2, 2, 2);
        } else {
            holder.binding.image.setPadding(0, 0, 0, 0);
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

    public void currentItem(boolean isCurrentItem, int currentItemPosition) {
        if (prevItemPosition < images.size()) {
            images.get(prevItemPosition).setCurrentItem(false);
        }
        prevItemPosition = currentItemPosition;
        images.get(currentItemPosition).setCurrentItem(true);
        notifyDataSetChanged();
    }

    public class SelectedImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemSelectedImageBinding binding;

        public SelectedImageHolder(@NonNull ItemSelectedImageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.image.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.image:
                    int position = this.getLayoutPosition();
                    listener.onItemClicked(position);
            }
        }
    }

    public interface ImageEventListener {
        void onItemClicked(int position);
    }
}
