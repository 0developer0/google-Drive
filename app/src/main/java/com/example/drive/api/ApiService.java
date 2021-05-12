package com.example.drive.api;

import com.example.drive.models.Img;
import com.example.drive.models.UploadResponse;

import java.util.ArrayList;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @POST("/upload")
    @Multipart
    Observable<UploadResponse> upload(
            @Part MultipartBody.Part files
            );

    @GET("/upload")
    Call<ArrayList<Img>> getImages();

    @GET("/upload")
    Call<Img> getImage();
}
