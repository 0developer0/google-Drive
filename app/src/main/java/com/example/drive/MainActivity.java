package com.example.drive;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.drive.databinding.ActivityMainBinding;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;
import java.text.DateFormatSymbols;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    
    private ActivityMainBinding binding;

    private ApiService apiService;
    private Uri imageUri;
    private Bitmap imageBitmap;
    private String imageStr;
    private static final int PICK_IMAGE_INTENT = 1;

    private ImageAdapter imageAdapter;
    private List<Image> images = new ArrayList<Image>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        requestPermission();

        apiService = new ApiService(this);

        if(images != null){
            ImageAdapter imageAdapter = new ImageAdapter(images);
            binding.rvImages.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL,
                    false));
            binding.rvImages.setAdapter(imageAdapter);
        }

        controlView();
    }

    private void controlView(){
        binding.fabAddImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab_addImage:
                openGallery();
        }
    }

    private void openGallery(){
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery, "Choose Picture"), PICK_IMAGE_INTENT);
    }

    private void bindImage(String imageStr, String imageUri){
        String dateOfUploaded = getDate();

        Image image = new Image(imageStr, dateOfUploaded, imageUri);

        apiService.uploadImage(image, new ApiService.UploadImageCallBack() {
            @Override
            public void onSuccess(Image image) {
                imageAdapter.addItem(image);
                binding.pgUpload.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(VolleyError error) {
                message("check network connection", true);
                binding.pgUpload.setVisibility(View.INVISIBLE);
            }
        });
    }

    private String getImageString(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        String encodeString = Base64.getEncoder().encodeToString(baos.toByteArray());
        return encodeString;
    }

    private String getDate() {
        Calendar calendar = Calendar.getInstance();
        String dayOfMonth = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        String month = getNameMonth(calendar.get(Calendar.MONTH));
        return dayOfMonth + " " + month;
    }

    private String getNameMonth(int month){
        return new DateFormatSymbols().getMonths()[month - 1];
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_INTENT && resultCode == Activity.RESULT_OK && data != null){
            imageUri = data.getData();
            try{
                imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageStr = getImageString(imageBitmap);
                bindImage(imageStr, imageUri.toString());
                binding.pgUpload.setVisibility(View.VISIBLE);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void message(String message, boolean important){
        if(important){
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        } else{
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    private void requestPermission(){
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        message("This permission is require for run program", true);
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                    }
                })
                .withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError dexterError) {
                        message("Try Again!", false);
                    }
                })
                .check();
    }
}