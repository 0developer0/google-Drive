package com.example.drive.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.drive.databinding.ActivityImageEditorBinding;
import com.example.drive.utils.FileManager;
import com.example.drive.utils.ImgUtil;
import com.example.drive.ui.Components.ProgressButton;
import com.example.drive.R;
import com.example.drive.ui.Adapters.ImageSliderAdapter;
import com.example.drive.ui.Adapters.SelectedImageAdapter;
import com.example.drive.models.Img;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;
import me.shaohui.advancedluban.Luban;

public class ImageEditorActivity extends AppCompatActivity implements SelectedImageAdapter.ImageEventListener {

    private ActivityImageEditorBinding binding;

    private MenuItem item_CropRatio;
    private MenuItem item_Unselect;

    private ProgressButton progressButton;
    private View btn_Progress;

    private SelectedImageAdapter adapter;
    private ImageSliderAdapter sliderAdapter;
    private ArrayList<Img> selectedImages;

    private ArrayList<Img> uploadedImages = new ArrayList<>();

    private FileManager tempFileManager;

    private int croppedItemPosition = 0;

    boolean isCompressing = false;

    private Toast toast_Error_Compressing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageEditorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getExtra();
        initView();

        tempFileManager = new FileManager(this, FileManager.TEMP_DIR);
    }

    private void getExtra() {
        selectedImages = getIntent().getParcelableArrayListExtra("selectedImages");
    }

    private void initView() {
        toolbarSetup();
        recyclerViewSetup();
        viewPagerSetup();
        buttonUploadSetup();
        toastSetup();
    }

    private void toolbarSetup() {
        setSupportActionBar(binding.toolbar);

        binding.toolbar.setNavigationOnClickListener(view -> finish());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_editor_activity, menu);
        item_CropRatio = menu.findItem(R.id.item_crop);
        item_Unselect = menu.findItem(R.id.item_unselected);

        if (selectedImages.size() == 1 ) {
            item_Unselect.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int position = binding.vpImageSlider.getCurrentItem();
        switch (item.getItemId()) {
            case R.id.item_crop:
                if (!isCompressing) {
                    cropRatioImage(position);
                }
                break;
            case R.id.item_unselected:
                if (!isCompressing) {
                    if (selectedImages.size() > 1) {
                        unselectImage(position);
                    } else {
                        finish();
                    }

                    if (selectedImages.size() == 1) {
                        item_Unselect.setVisible(false);
                        binding.rvSelectedImage.setVisibility(View.GONE);
                    }
                }
                break;
            default:
                break;
        }

        return true;
    }

    private void cropRatioImage(int position) {
        croppedItemPosition = position;
        Img image = selectedImages.get(position);

        Uri imageUri = Uri.fromFile(new File(image.getContentUri()));
        File temFile = new File(tempFileManager.getFileDir(), tempFileManager.createRandomFileName("IMG_", FileManager.JPG_TYPE));

        UCrop uCrop = UCrop.of(imageUri, Uri.fromFile(temFile));

        uCrop.useSourceImageAspectRatio();
        uCrop = advanceConfig(uCrop);

        uCrop.start(ImageEditorActivity.this);
    }

    private UCrop advanceConfig(UCrop uCrop) {
        UCrop.Options options =new UCrop.Options();

        options.setFreeStyleCropEnabled(true);

        options.setStatusBarColor(getResources().getColor(R.color.blue_500));
        options.setActiveControlsWidgetColor(getResources().getColor(R.color.blue_500));
        options.setToolbarWidgetColor(getResources().getColor(R.color.blue_500));
        options.setCropFrameColor(getResources().getColor(R.color.blue_500));

        return uCrop.withOptions(options);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK && data != null) {
            Uri uri = UCrop.getOutput(data);
            selectedImages.get(croppedItemPosition).setContentUri(uri.getPath());

            sliderAdapter.notifyItemChanged(croppedItemPosition);
            adapter.notifyItemChanged(croppedItemPosition);
        }
    }

    private void unselectImage(int position) {
        selectedImages.remove(position);

        sliderAdapter.notifyItemRemoved(position);
        adapter.notifyItemRemoved(position);

        //Scroll to current Item after Item removed
        if (position >= selectedImages.size()) {
            position--;
        }
        scrollCurrentItem(position);

        progressButton.setCounter(selectedImages.size());
    }

    private void recyclerViewSetup() {
        if (!selectedImages.isEmpty()) {
            adapter = new SelectedImageAdapter(selectedImages, this, this);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(RecyclerView.HORIZONTAL);
            binding.rvSelectedImage.setLayoutManager(layoutManager);
            binding.rvSelectedImage.setHasFixedSize(true);
            binding.rvSelectedImage.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            binding.rvSelectedImage.setDrawingCacheEnabled(true);
            binding.rvSelectedImage.setItemViewCacheSize(20);
            adapter.setHasStableIds(true);
            binding.rvSelectedImage.setAdapter(adapter);
            if (selectedImages.size() == 1) {
                binding.rvSelectedImage.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onItemClicked(int position) {
        if (!isCompressing) {
            binding.vpImageSlider.setCurrentItem(position);
        }
    }

    private void viewPagerSetup() {
        if (!selectedImages.isEmpty()) {
            sliderAdapter = new ImageSliderAdapter(selectedImages, this);
            binding.vpImageSlider.setAdapter(sliderAdapter);

            binding.vpImageSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);

                    scrollCurrentItem(position);
                }
            });
        }
    }

    private void scrollCurrentItem(int position) {
        adapter.currentItem(true, position);
        binding.rvSelectedImage.scrollToPosition(position);
    }

    private void buttonUploadSetup() {
        btn_Progress = findViewById(R.id.btn_upload);

        progressButton = new ProgressButton(this, btn_Progress);
        progressButton.setListener(new ProgressButton.OnButtonClickListener() {
            @Override
            public void onClick() {
                isCompressing = true;
                disableScrolling();
                compressImages(selectedImages);
            }
        });
        progressButton.setCounter(selectedImages.size());
    }

    private void compressImages(ArrayList<Img> images) {
        ArrayList<File> mFiles = ImgUtil.getFilesFromImgObjs(images);

        Luban.compress(mFiles, getFilesDir())
                .clearCache()
                .putGear(Luban.THIRD_GEAR)
                .asListObservable()
                .subscribe(new Consumer<List<File>>() {
                    @Override
                    public void accept(List<File> files) throws Exception {
                        if (files != null) {
                            ArrayList<File> compressedFiles = new ArrayList<File>(files);

                            /*
                            for (int i = 0; i < files.size(); i++) {
                                Log.e("Compressor", '\n' + "before : " + mFiles.get(i).length() / 1024 + " kb" + '\n'
                                        + "after : " + files.get(i).length() / 1024 + " kb" + "path : " + files.get(i).getPath());
                            }
                             */

                            passImagesToMainActivity(compressedFiles);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        toast_Error_Compressing.show();
                    }
                });
    }

    private void disableScrolling() {
        binding.vpImageSlider.setUserInputEnabled(false);
    }
    
    private void passImagesToMainActivity(ArrayList<File> files) {
        if (!files.isEmpty()) {
            ArrayList<Img> uploadingImages = ImgUtil.getImgObjsFromFiles(files);

            Intent imageActivity = new Intent(ImageEditorActivity.this, MainActivity.class);
            imageActivity.putParcelableArrayListExtra("CompressedImages", uploadingImages);
            setResult(Activity.RESULT_OK, imageActivity);
            finish();
        }
    }

    private void toastSetup() {
        toast_Error_Compressing = Toast.makeText(getApplicationContext(), "Invalid Format Photo", Toast.LENGTH_SHORT);
    }

    @Override
    protected void onStop() {
        toast_Error_Compressing.cancel();

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        toast_Error_Compressing.cancel();

        super.onDestroy();
    }
}