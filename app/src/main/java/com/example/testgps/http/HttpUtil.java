package com.example.testgps.http;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HttpUtil {
    public static final String URL = "http://106.12.218.233:10001/jeecg-boot/";
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    public static String token;
    static Handler handler = new Handler(Looper.getMainLooper());
    static OkHttpClient okHttpClient = new OkHttpClient();

    public static void setToken(String token) {
        HttpUtil.token = token;
    }

    public static void post(String action, Object obj, final MyCallBack callBack) {
        action("post", action,obj, callBack);
    }
    public static void get(String action, Object obj, final MyCallBack callBack) {
        action("get", action,obj, callBack);
    }
    public static void action(String actionType, String action, Object obj, final MyCallBack callBack) {
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        String text = JSON.toJSONString(obj);

        final Request.Builder builder = new Request.Builder();

        if ("post".equals(actionType)){
            builder.url(URL + action).post(RequestBody.create(mediaType, text));//默认就是GET请求，可以不写

        }else if ("get".equals(actionType)){
            HttpUrl.Builder urlBuilder = HttpUrl.parse(URL + action)
                    .newBuilder();
            JSONObject jsonObject = JSONObject.parseObject(text);
            Iterator<String> iterator =jsonObject.keySet().iterator();
            while(iterator.hasNext()) {
                String key = iterator.next();
                urlBuilder.addQueryParameter(key, jsonObject.getString(key));//默认就是GET请求，可以不写
            }
            builder.url(urlBuilder.build()).get();

        }else if ("put".equals(actionType)){
            builder.url(URL + action).put(RequestBody.create(mediaType, text));//默认就是GET请求，可以不写

        }
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
                        callBack.onFailure(e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
//                Log.d("AAA", "onResponse: " + response.body().string());
                final JSONObject jsonObject = (JSONObject) JSON.parse(response.body().string());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (response.code() != 200 || jsonObject.getInteger("code") != 200) {
                            callBack.onError(jsonObject.getString("message"));
                        } else {
                            callBack.onSuccess(jsonObject.getJSONObject("result"));
                        }
                    }
                });

            }
        });
    }


    public static void uploadFile(String filePath, String fileName, final MyCallBack callBack) {
        File file = new File(filePath);
        Log.d("AAA", "filePath is " + file.exists());
//            try {
//
//                InputStream inputStream = new URL(fileName).openStream();
//                byte[] list = new byte[1024];
//                int len = inputStream.read(list);
//                Log.d("AAA", filePath +" readed");
//                inputStream.close();
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName,
                        RequestBody.create(MEDIA_TYPE_PNG, new File(filePath)))
                .build();

        Request.Builder builder = new Request.Builder()
                .url(URL+"sys/common/upload")
                .post(requestBody);
        if (!TextUtils.isEmpty(token)) {
            builder.addHeader("X-Access-Token", token);
        }
        Request request = builder.build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                callBack.onFailure(e.getMessage());
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
                            callBack.onError(jsonObject.getString("message"));
                        } else {
                            callBack.onSuccess(jsonObject);
                        }
                    }
                });
            }
        });

    }

    public static interface MyCallBack {
        public void onFailure(String message);

        public void onError(String message);

        public void onSuccess(JSONObject jsonObject);
    }
}
