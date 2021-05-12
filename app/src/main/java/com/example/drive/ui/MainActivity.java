package com.example.drive.ui;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.drive.api.ApiClient;
import com.example.drive.api.ApiService;
import com.example.drive.models.UploadResponse;
import com.example.drive.ui.Components.DialogDisplayImage;
import com.example.drive.utils.ImgUtil;
import com.example.drive.utils.ItemEventListener;
import com.example.drive.R;
import com.example.drive.models.Img;
import com.example.drive.databinding.ActivityMainBinding;
import com.example.drive.ui.Adapters.UploadedImageAdapter;
import com.tbruyelle.rxpermissions3.RxPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ItemEventListener {

    private ActivityMainBinding binding;

    public static final String[] REQUIRED_PERMISSION = {
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private static final int IMAGE_PICKER_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;

    private ApiService service;

    private UploadedImageAdapter adapter;
    private ArrayList<Img> uploadedImages = new ArrayList<>();

    private DialogDisplayImage dialog;

    private Animation fab_Rotate_Clock_Wise, fab_Rotate_AntiClock_Wise;

    boolean isFabOpen = false;

    private final Handler handler = new Handler();
    private int count = 0;

    private Toast toast_Try_Again;
    private Toast toast_Error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        apiServiceSetup();
        initView();

        binding.etFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });

        binding.refreshLayout.setColorSchemeColors(getResources().getColor(R.color.blue_500));
        binding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateImagesItem();
            }
        });
    }

    private void initView() {
        shimmerLayoutSetup();
        recyclerViewSetup();
        dialogSetup();
        toastSetup();
        animationSetup();
        onClickSetup();
    }

    private void shimmerLayoutSetup() {
    }

    private void recyclerViewSetup() {
        uploadedImages.addAll(getImagesFromApi());
        if (!uploadedImages.isEmpty()) {
            adapter = new UploadedImageAdapter(uploadedImages, this);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(RecyclerView.VERTICAL);
            binding.rvUploadedImage.setLayoutManager(layoutManager);
            binding.rvUploadedImage.setHasFixedSize(true);
            adapter.setHasStableIds(true);
            binding.rvUploadedImage.setAdapter(adapter);
            binding.tvEmpty.setVisibility(View.INVISIBLE);
        } else {
            binding.tvEmpty.setVisibility(View.VISIBLE);
        }

        binding.rvUploadedImage.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && binding.fabUpload.getVisibility() == View.VISIBLE) {
                    if (!isFabOpen) {
                        binding.fabUpload.hide();
                    }
                } else if (dy < 0 && binding.fabUpload.getVisibility() != View.VISIBLE) {
                    binding.fabUpload.show();
                }
            }
        });
    }

    private void dialogSetup() {
        dialog = new DialogDisplayImage();
        dialog.setCancelable(true);
    }

    private void toastSetup() {
        Context context = getApplicationContext();
        toast_Try_Again = Toast.makeText(context, "Try Again", Toast.LENGTH_SHORT);
        toast_Error = Toast.makeText(context, " ", Toast.LENGTH_SHORT);
    }

    private void animationSetup() {
        fab_Rotate_Clock_Wise = AnimationUtils.loadAnimation(this, R.anim.fab_rotate_clock_wise);
        fab_Rotate_AntiClock_Wise = AnimationUtils.loadAnimation(this, R.anim.fab_rotate_anticlock_wise);
    }

    private void onClickSetup() {
        binding.fabUpload.setOnClickListener(this);
        binding.fabGallery.setOnClickListener(this);
        binding.fabGallery.setOnClickListener(this);
    }

    @Override
    public void onClick(@NotNull View view) {
        switch (view.getId()) {
            case R.id.fab_upload:
                if (!isFabOpen) {
                    showFabMenu();
                } else {
                    hideFabMenu();
                }
                break;

            case R.id.fab_gallery:
                if (isFabOpen) {
                    requestPermission(IMAGE_PICKER_REQUEST_CODE, REQUIRED_PERMISSION);
                    hideFabMenu();
                    break;
                }

            case R.id.fab_camera:
                if (isFabOpen) {
                    requestPermission(CAMERA_REQUEST_CODE, REQUIRED_PERMISSION);
                    hideFabMenu();
                    break;
                }
        }
    }

    private void apiServiceSetup() {
        service = ApiClient.createServices(ApiService.class);
    }

    private void showFabMenu() {
        Runnable show = new Runnable() {
            @Override
            public void run() {
                isFabOpen = true;

                binding.fabGallery.show();
                binding.fabCamera.show();

                binding.fabUpload.startAnimation(fab_Rotate_Clock_Wise);
            }
        };

        handler.postDelayed(show, 100);
    }

    private void hideFabMenu() {
        Runnable hide = new Runnable() {
            @Override
            public void run() {
                isFabOpen = false;

                binding.fabGallery.hide();
                binding.fabCamera.hide();

                binding.fabUpload.startAnimation(fab_Rotate_AntiClock_Wise);
            }
        };

        handler.postDelayed(hide, 100);
    }

    private void requestPermission(int reqCode, String... permissions) {
        RxPermissions req = new RxPermissions(this);

        req.request(permissions)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        switch (reqCode) {
                            case IMAGE_PICKER_REQUEST_CODE:
                                openImagePicker();
                                break;
                            case CAMERA_REQUEST_CODE:
                                openCamera();
                                break;
                        }
                    }
                });
    }

    private void openImagePicker() {
        Intent imagePicker = new Intent(MainActivity.this, ImageFolderActivity.class);
        startActivityForResult(imagePicker, IMAGE_PICKER_REQUEST_CODE);
    }

    private void openCamera() {
    }

//    private void openSetting() {
//        Intent setting = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//        Uri packageName = Uri.parse("package:" + getPackageName());
//        startActivity(setting);
//    }

    @Override
    public void onUploadedImageClicked(Img image) {
        Bundle bundle = new Bundle();
        bundle.putString("imageUri", image.getContentUri());

        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void onStateUploadingClicked(Img image, int position) {
        Uri uri = Uri.parse(image.getContentUri());
        File file = new File(uri.getPath());

        adapter.startUploading(position);

        uploadImage(file);
    }

    private void filter(String text) {
        if (!uploadedImages.isEmpty()) {
            List<Img> filteredImages = new ArrayList<Img>();

            for (Img image : uploadedImages) {
                if (image.getImageName().toLowerCase().contains(text.toLowerCase())) {
                    filteredImages.add(image);
                }
            }

            adapter.filteredList(filteredImages);
        }
    }

    private void updateImagesItem() {
        if (adapter != null) {
            ArrayList<Img> images = new ArrayList<>(getImagesFromApi());

            if (!images.isEmpty()) {
                adapter.uploadedImages(images);
            }
        } else {
            recyclerViewSetup();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            ArrayList<Img> uploadingImages = data.getParcelableArrayListExtra("UploadingImages");
            addItemToRecyclerview(uploadingImages);
            ArrayList<File> files = ImgUtil.getFilesFromImgObjs(uploadingImages);
            for (int i = 0; i < files.size(); i++) {
                uploadImage(files.get(i));
            }
        }
    }

   private void uploadImage(File file) {
       RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
       MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

       Observable<UploadResponse> uploadReq = service.upload(filePart);

       uploadReq.subscribeOn(Schedulers.newThread())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(new Observer<UploadResponse>() {
                   @Override
                   public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                   }

                   @Override
                   public void onNext(@io.reactivex.rxjava3.annotations.NonNull UploadResponse uploadResponse) {
                       if (!uploadResponse.getFileUri().isEmpty()) {
                           adapter.uploadingResult(uploadResponse.getFileUri(), uploadResponse.getDateOfUploaeded(), true);
                       }
                   }

                   @Override
                   public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                       adapter.uploadingResult(file.getPath(), "", false);
                       toast_Error.setText("" + e.getMessage());
                   }

                   @Override
                   public void onComplete() {

                   }
               });
   }

    private void addItemToRecyclerview(ArrayList<Img> images) {
        if (adapter == null) {
            uploadedImages.addAll(images);
            recyclerViewSetup();
        } else {
            for (int i = 0; i < images.size(); i++) {
                if (uploadedImages.contains(images.get(i))) {
                    images.remove(i);
                    toast_Error.setText("Can not upload one image two time" + images.get(i).getImageName());
                    toast_Error.show();
                }
            }

            count = 0;
            Runnable addItem = new Runnable() {
                @Override
                public void run() {
                    adapter.addItem(images.get(count), 0);

                    if (count++ < images.size() - 1) {
                        handler.postDelayed(this, 300);
                    }
                }
            };

            handler.post(addItem);
        }
    }

    private ArrayList<Img> getImagesFromApi() {
        ArrayList<Img> images = new ArrayList<>();

        //start progress
        binding.refreshLayout.setRefreshing(true);

        service.getImages().enqueue(new Callback<ArrayList<Img>>() {
            @Override
            public void onResponse(Call<ArrayList<Img>> call, Response<ArrayList<Img>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    images.addAll(response.body());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Img>> call, Throwable t) {
                toast_Try_Again.show();
            }
        });

        //finish progress
        binding.refreshLayout.setRefreshing(false);

        return images;
    }

    @Override
    protected void onStop() {
        if (toast_Try_Again != null) {
            toast_Try_Again.cancel();
        }

        toast_Error.cancel();

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (toast_Try_Again != null) {
            toast_Try_Again.cancel();
        }

        toast_Error.cancel();

        super.onDestroy();
    }

}