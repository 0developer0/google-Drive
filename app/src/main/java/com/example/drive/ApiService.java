package com.example.drive;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class ApiService {

    private static RequestQueue requestQueue;
    private static final String URL_SERVER = "0.1.01.1";
    private static final String TAG = "ApiService";

    public ApiService(Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public void uploadImage(Image image, UploadImageCallBack callBack){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("image_string", image.getStrImage());
            jsonObject.put("image_uri", image.getImageUri());
        } catch (Exception e){
            e.printStackTrace();
        }

        JsonObjectRequest uploadImageRequest = new JsonObjectRequest(URL_SERVER, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e(TAG, "onResponse: " + response);
                try {
                    Image img = new Image(
                            response.getString("image_string"),
                            image.getDateOfUploaded(),
                            response.getString("image_uri")
                            );
                    callBack.onSuccess(img);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: " + error.toString());
                callBack.onError(error);
            }
        });
        requestQueue.add(uploadImageRequest);
    }

    public interface UploadImageCallBack {
        void onSuccess(Image image);

        void onError(VolleyError error);
    }
}
