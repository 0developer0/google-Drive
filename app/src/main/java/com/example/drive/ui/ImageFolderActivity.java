package com.example.drive.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.example.drive.ui.Adapters.ImageFolderAdapter;
import com.example.drive.models.ImgFolder;
import com.example.drive.databinding.ActivityImageFolderBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ImageFolderActivity extends AppCompatActivity implements ImageFolderAdapter.FolderItemEventListener {
    ActivityImageFolderBinding binding;

    ImageFolderAdapter adapter;

    private static final int FOLDER_ACTIVITY_REQ_CODE = 690;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageFolderBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        initView();
    }

    private void initView() {
        toolbarSetup();
        recyclerViewSetup();
    }

    private void toolbarSetup() {
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void recyclerViewSetup() {
        ArrayList<ImgFolder> folders = getImgFolders();

        if(!folders.isEmpty()) {
            adapter = new ImageFolderAdapter(folders, ImageFolderActivity.this, this);
            GridLayoutManager layoutManager = new GridLayoutManager(this, ImageFolderAdapter.SPAN_COUNT);
            binding.rvImageFolder.setLayoutManager(layoutManager);
            binding.rvImageFolder.setHasFixedSize(false);
            binding.rvImageFolder.setItemViewCacheSize(20);
            binding.rvImageFolder.setDrawingCacheEnabled(true);
            binding.rvImageFolder.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            binding.rvImageFolder.setAdapter(adapter);
        }
    }

    public ArrayList<ImgFolder> getImgFolders() {
        ArrayList<ImgFolder> folders = new ArrayList<>();
        ArrayList<String> foldersPath = new ArrayList<>();
        Uri imagesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection  = {
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_MODIFIED
        };

        Cursor cursor = this.getContentResolver().query(imagesUri,
                projection,
                null,
                null ,
                projection[2]);

        try {
            ImgFolder allMedia = new ImgFolder();
            allMedia.setFolderName("All Media");
            if(cursor != null) {
                cursor.moveToFirst();
            }
            do {
                ImgFolder folder = new ImgFolder();
                String data = cursor.getString(cursor.getColumnIndexOrThrow(projection[0]));
                String folderName = cursor.getString(cursor.getColumnIndexOrThrow(projection[1]));

                String folderPath = data.substring(0 , data.lastIndexOf(folderName + "/"));
                folderPath = folderPath + folderName + "/";
                if (!foldersPath.contains(folderPath)) {
                    foldersPath.add(folderPath);

                    folder.setFolderName(folderName);
                    folder.setPathUri(folderPath);
                    folder.setCoverImage(data);
                    folder.addNewImg();
                    folders.add(folder);
                } else {
                    for(int i = 0; i < folders.size(); i++) {
                        if (folders.get(i).getPathUri().equals(folderPath)) {
                            folders.get(i).setCoverImage(data);
                            folders.get(i).addNewImg();
                        }
                    }
                }

                allMedia.setCoverImage(data);
                allMedia.addNewImg();

            } while(cursor.moveToNext());

            cursor.close();

            ImgFolder lastFolder = folders.get(0);
            folders.set(0, allMedia);
            folders.add(lastFolder);

        } catch(Exception e) {
            e.printStackTrace();
        }

        /*
        for(int i = 0; i < folders.size(); i++) {
            ImgFolder log = folders.get(i);
            Log.e(TAG, "*FolderName : " + log.getFolderName() + "*"
                    + " *PathUri : " + log.getPathUri() + "*"
                    + " *CoverOfImage : " + log.getCoverImage() + "*"
                    + " *CountOfImage : " + log.getCountOfImage() + " Media" + "*");
        }
         */

        sortArrayListByValue(folders);

        return folders;
    }

    private void sortArrayListByValue(ArrayList<ImgFolder> folders) {
        Collections.sort(folders, new Comparator<ImgFolder>() {
            @Override
            public int compare(ImgFolder folder1, ImgFolder folder2) {
                if (folder1.getCountOfImage() < folder2.getCountOfImage()) {
                    return 1;
                }
                if (folder1.getCountOfImage() > folder2.getCountOfImage()) {
                    return -1;
                }
                return 0;
            }
        });
    }

    @Override
    public void onFolderClicked(ImgFolder folder) {
        Intent openImageFolder = new Intent(ImageFolderActivity.this, ImagePickerActivity.class);
        openImageFolder.putExtra("folderName", folder.getFolderName());
        openImageFolder.putExtra("pathUri", folder.getPathUri());

        startActivityForResult(openImageFolder, FOLDER_ACTIVITY_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == FOLDER_ACTIVITY_REQ_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Intent mainActivity = new Intent(ImageFolderActivity.this, MainActivity.class);
            mainActivity.putParcelableArrayListExtra("UploadingImages", data.getParcelableArrayListExtra("SelectedImages"));
            setResult(Activity.RESULT_OK, mainActivity);
            finish();
        }
    }
}