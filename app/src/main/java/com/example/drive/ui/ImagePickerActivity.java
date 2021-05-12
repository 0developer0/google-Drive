package com.example.drive.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.drive.databinding.ActivityImagePickerBinding;
import com.example.drive.utils.ImgUtil;
import com.example.drive.R;
import com.example.drive.utils.SelectionListener;
import com.example.drive.ui.Adapters.ImagesAdapter;
import com.example.drive.models.Img;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class ImagePickerActivity extends AppCompatActivity implements SelectionListener {
    private ActivityImagePickerBinding binding;

    private MenuItem item_Selection;
    private MenuItem item_Ok;

    private String folderName;
    private String folderPath;

    private ImagesAdapter adapter;
    private ArrayList<Img> folderImages = new ArrayList<>();
    private ArrayList<Img> selectedImages = new ArrayList<>();

    private static final int IMAGE_PICKER_ACTIVITY_REQ_CODE = 931;

    private Toast toast_Select_Limit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImagePickerBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        getExtra();
        initView();
    }

    private void initView() {
        toolbarSetup();
        recyclerViewSetup();
        toastSetup();
    }

    private void getExtra() {
        folderName = getIntent().getStringExtra("folderName");
        folderPath = getIntent().getStringExtra("pathUri");
    }

    private void toolbarSetup() {
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setTitle("" + folderName);

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(adapter.isSelectable()){
                   cancel();
                } else {
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_picker_activity, menu);
        item_Selection = menu.findItem(R.id.item_selection);
        item_Ok = menu.findItem(R.id.tv_ok);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_selection:
                selectable();
                break;
            case R.id.tv_ok:
                displayImage();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (adapter.isSelectable()) {
            cancel();
        } else {
            finish();
        }
    }

    private void selectable() {
        adapter.setSelectable(true);
        item_Selection.setVisible(false);
        item_Ok.setVisible(true);

        binding.toolbar.setTitle("Tap photo to select");
        binding.toolbar.setTitleTextColor(Color.WHITE);
        binding.toolbar.setBackgroundColor(this.getResources().getColor(R.color.blue_500));
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
    }

    private void cancel() {
        selectedImages.clear();

        adapter.setSelectable(false);
        item_Selection.setVisible(true);
        item_Ok.setVisible(false);

        binding.toolbar.setTitle(folderName);
        binding.toolbar.setTitleTextColor(this.getResources().getColor(R.color.blue_500));
        binding.toolbar.setBackgroundColor(Color.WHITE);
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_blue);

        adapter.unselected();
    }

    private void recyclerViewSetup() {
        folderImages = getImagesFolder();
        if (!folderImages.isEmpty()) {
            adapter = new ImagesAdapter(folderImages, getApplicationContext(), this);
            GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), ImagesAdapter.SPAN_COUNT);
            binding.rvImage.setLayoutManager(layoutManager);
            binding.rvImage.setHasFixedSize(true);
            binding.rvImage.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            binding.rvImage.setDrawingCacheEnabled(true);
            binding.rvImage.setItemViewCacheSize(20);
            adapter.setHasStableIds(true);
            binding.rvImage.setAdapter(adapter);
        }
    }

    private ArrayList<Img> getImagesFolder() {
        ArrayList<Img> images = new ArrayList<>();
        Uri imagesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_MODIFIED
        };

        Cursor cursor;
        if(folderPath == null) {
            cursor = this.getContentResolver().query(
                    imagesUri,
                    projection,
                    null,
                    null,
                    projection[2]
            );
        } else {
            cursor = this.getContentResolver().query(
                    imagesUri,
                    projection,
                    projection[0] + " like ? ",
                    new String[] { "%" + folderPath + "%" },
                    projection[2]
            );
        }

        try {
            cursor.moveToFirst();
            do {
                Img image = new Img();

                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(projection[0]));
                String imageName = cursor.getString(cursor.getColumnIndexOrThrow(projection[1]));
                int dateOfModified = cursor.getColumnIndex(projection[2]);

                image.setImageName(imageName);
                image.setContentUri(imagePath);
                image.setHeaderDate(getHeaderDate(dateOfModified, cursor));

                images.add(image);
            } while (cursor.moveToNext());
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Reverse images Arraylist
        Collections.reverse(images);

        return images;
    }

    private String getHeaderDate(int dateOfModified, Cursor cursor) {
        Calendar imageDate = Calendar.getInstance();
        imageDate.setTimeInMillis(cursor.getLong(dateOfModified) * 1000);

        String headerDate = ImgUtil.getDateDifference(imageDate);

        return headerDate;
    }

    private void toastSetup() {
        toast_Select_Limit = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
    }

    @Override
    public void onImageClicked(Img image, int position) {
        if (adapter.isSelectable()) {
            //this condition -> while user first selected image amd after that clicked on image
            if(selectedImages.contains(image)) {
                unselectedImage(position, image);
            } else {
                if (selectedImages.size() >= 30 ){
                    String message = String.format("Can\'t select more than %d items.", selectedImages.size());
                    toast_Select_Limit.setText(message);
                    toast_Select_Limit.show();
//                    Utility.message(message, false, this);
                    return;
                }
                selectedImage(position, image);
            }
        } else {
            selectable();
            selectedImage(position, image);
            displayImage();
        }
    }

    @Override
    public void onImageLongClicked(Img image, int position) {
        if (!adapter.isSelectable()) {
            selectable();
            selectedImage(position, image);
        } else {
            selectedImage(position, image);
        }
    }

    private void selectedImage(int position, Img image) {
        if (!selectedImages.contains(image)) {
            image.setSelected(true);
            selectedImages.add(image);
            adapter.select(true, position);
            binding.toolbar.setTitle(String.format("%d selected", selectedImages.size()));
        }
    }

    private void unselectedImage(int position, Img image) {
        selectedImages.remove(image);
        adapter.select(false, position);

        if (selectedImages.size() > 0) {
            binding.toolbar.setTitle(String.format("%d selected", selectedImages.size()));
        } else {
            cancel();
        }
    }

    private void displayImage() {
        Intent openUploadOptions = new Intent(ImagePickerActivity.this, ImageEditorActivity.class);
        openUploadOptions.putParcelableArrayListExtra("selectedImages", selectedImages);
        startActivityForResult(openUploadOptions, IMAGE_PICKER_ACTIVITY_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_PICKER_ACTIVITY_REQ_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Intent imageFolderActivity = new Intent(ImagePickerActivity.this, ImageFolderActivity.class);
            imageFolderActivity.putParcelableArrayListExtra("SelectedImages", data.getParcelableArrayListExtra("CompressedImages"));
            setResult(Activity.RESULT_OK, imageFolderActivity);
            finish();
        }
    }

    @Override
    protected void onStop() {
        toast_Select_Limit.cancel();

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        toast_Select_Limit.cancel();

        super.onDestroy();
    }

}