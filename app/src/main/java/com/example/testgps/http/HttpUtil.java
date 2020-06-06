package com.example.testgps.http;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HttpUtil {
    public static final String URL = "http://106.12.218.233:10001/jeecg-boot/";
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image.png");
    public static String token;
    static Handler handler = new Handler(Looper.getMainLooper());
    static OkHttpClient okHttpClient = new OkHttpClient();


    public static void setToken(String token) {
        HttpUtil.token = token;
    }

    public static void post(String action, Object obj, final MyCallback callback) {
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        String text = JSON.toJSONString(obj);
        final Request.Builder builder = new Request.Builder()
                .url(URL + action)
                .post(RequestBody.create(mediaType, text));//默认就是GET请求，可以不写
        if (!TextUtils.isEmpty(token)) {
            builder.addHeader("X-Access-Token", token);
        }
        Call call = okHttpClient.newCall(builder.build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                Log.d("AAA", "onFailure: ");
                e.printStackTrace();
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        callback.onFailure(e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // Log.d("AAA", "onResponse: " + response.body().string());
                try {
                    final JSONObject jsonObject = (JSONObject) JSON.parse(response.body().string());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (response.code() != 200 || jsonObject.getInteger("code") != 200) {
                                callback.onError(jsonObject.getString("message"));
                            } else {
                                callback.onSuccess(jsonObject.getJSONObject("result"));
                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void uploadFile(String filePath, String fileName, final MyCallback callback) throws Exception {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName,
                        RequestBody.create(MEDIA_TYPE_PNG, new File(filePath)))
                .build();

        Request.Builder builder = new Request.Builder()
                .url(URL + "sys/common/upload")
                .post(requestBody);
        if (!TextUtils.isEmpty(token)) {
            builder.addHeader("X-Access-Token", token);
        }
        Request request = builder.build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                callback.onFailure(e.getMessage());

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String str = response.body().string();
                Log.d("AAA", "response : " + str);
                final JSONObject jsonObject = (JSONObject) JSON.parse(str);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (response.code() != 200 || !jsonObject.getBoolean("success")) {
                            callback.onError(jsonObject.getString("message"));
                        } else {
                            callback.onSuccess(jsonObject);
                        }
                    }
                });
            }
        });
    }

    public static interface MyCallback {
        public void onFailure(String message);

        public void onError(String message);

        public void onSuccess(JSONObject jsonObject);
    }
}
