package com.example.drive.ui.Adapters;

import android.app.Activity;
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
import com.example.drive.models.ImgFolder;
import com.example.drive.databinding.ItemFolderBinding;

import java.util.ArrayList;
import java.util.List;

public class ImageFolderAdapter extends RecyclerView.Adapter<ImageFolderAdapter.ImgFolderViewHolder> {
    private List<ImgFolder> folders;

    private FolderItemEventListener listener;

    private Context context;
    private RequestOptions options;

    public static final int SPAN_COUNT = 2;
    private static final int MARGIN = 4;

    public ImageFolderAdapter(ArrayList<ImgFolder> folders, Context context, FolderItemEventListener listener) {
        this.folders = folders;
        this.listener = listener;
        this.context = context;

        options = Utility.getOptions();

        Utility.getScreenSize((Activity) context);
    }

    @NonNull
    @Override
    public ImgFolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemFolderBinding binding = ItemFolderBinding.inflate(inflater, parent, false);
        return new ImgFolderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImgFolderViewHolder holder, int position) {
        ImgFolder folder = folders.get(position);

        /*
        *load coverImage with Glide
        * set folderName
        * set countOfImage in folder
         */
        Glide.with(context)
                .load(folder.getCoverImage())
                .apply(options)
                .error(android.R.drawable.ic_menu_close_clear_cancel)
                .into(holder.binding.imgFolderCover);

        holder.binding.tvFolderName.setText("" + folder.getFolderName());
        holder.binding.tvImgsCount.setText("" + folder.getCountOfImage());
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    @Override
    public long getItemId(int position) {
        return folders.get(position).getCoverImage().hashCode();
    }

    public class ImgFolderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemFolderBinding binding;

        public ImgFolderViewHolder(@NonNull ItemFolderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            int size = (Utility.WIDTH / SPAN_COUNT) - (MARGIN / 2);
            binding.imgFolderCover.getLayoutParams().width = size;
            binding.imgFolderCover.getLayoutParams().height = size;
            
            binding.imgFolderCover.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.img_folder_cover:
                    int position = this.getLayoutPosition();
                    listener.onFolderClicked(folders.get(position));
            }
        }
    }

    public interface FolderItemEventListener {
        void onFolderClicked(ImgFolder folder);
    }
}
