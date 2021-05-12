package com.example.drive.api;

import io.reactivex.plugins.RxJavaPlugins;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "example.com";

    private static ApiService service;

    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl("https://" + BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create());

    private static Retrofit retrofit = builder.build();

    public static <S> S createServices(Class<S> serviceClass) {
        service = (ApiService) retrofit.create(serviceClass);

        return (S) service;
    }
}
