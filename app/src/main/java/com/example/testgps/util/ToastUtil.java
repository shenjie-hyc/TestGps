package com.example.testgps.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;



public class ToastUtil {
    static Handler handler = new Handler(Looper.getMainLooper());
    public static void showToast(Context context, String message){
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,message,Toast.LENGTH_LONG).show();
            }
        });

    }
}
