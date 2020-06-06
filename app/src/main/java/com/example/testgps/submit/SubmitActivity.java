package com.example.testgps.submit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Engine;
import com.example.testgps.R;
import com.example.testgps.http.HttpUtil;
import com.example.testgps.model.bean.GpsBean;
import com.example.testgps.util.GlideEngine;
import com.example.testgps.util.ToastUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SubmitActivity extends AppCompatActivity implements AMapLocationListener, ActivityCompat.OnRequestPermissionsResultCallback, View.OnClickListener {
    private static final int PERMISSON_REQUESTCODE = 0;
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE

    };
    //private RecyclerView list;
    private TextView txt_gps;
    private TextView txt_address;
    private TextView btn_gps;
    private TextView btn_submit;
    double lat;
    double lon;
    String address;
    GpsBean gpsBean = new GpsBean();
    private ImageView img1;
    private ImageView img2;
    private ImageView img3;
    private ImageView img4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);
        initView();
    }

    private void initView() {
        // list = (RecyclerView) findViewById(R.id.list);
        txt_gps = (TextView) findViewById(R.id.txt_gps);
        txt_address = (TextView) findViewById(R.id.txt_address);
        btn_gps = (TextView) findViewById(R.id.btn_gps);
        btn_submit = (TextView) findViewById(R.id.btn_submit);
        //list.setAdapter(new ImageAdapter(this, gpsBean.getImgList()));
        checkPermissions(needPermissions);
        btn_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGps();
            }
        });
        img1 = (ImageView) findViewById(R.id.img1);
        img1.setOnClickListener(this);
        img2 = (ImageView) findViewById(R.id.img2);
        img2.setOnClickListener(this);
        img3 = (ImageView) findViewById(R.id.img3);
        img3.setOnClickListener(this);
        img4 = (ImageView) findViewById(R.id.img4);
        img4.setOnClickListener(this);
    }

    public void clickAddPic(final int index) {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofAll())
                .selectionMode(PictureConfig.SINGLE)
                .loadImageEngine(GlideEngine.createGlideEngine())
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(List<LocalMedia> result) {
                        upImg(result,index);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }

    AMapLocationClient mlocationClient;

    private void onGps() {
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(SubmitActivity.this);
            mLocationOption.setOnceLocation(true);
            //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
        } else {
            mlocationClient.stopLocation();
        }
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        mlocationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();
                //获取当前定位结果来源，如网络定位结果，详见定位类型表
                lat = amapLocation.getLatitude();
                lon = amapLocation.getLongitude();
                amapLocation.getAccuracy();
                address = amapLocation.getAddress();
                txt_gps.setText(String.format("当前位置：经度： %1$f 纬度： %2$f", lat, lon));
                txt_address.setText(address);

//                //获取精度信息
//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Date date = new Date(amapLocation.getTime());
//                df.format(date);
                //定位时间
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:" + amapLocation.getErrorCode() + ", errInfo:" + amapLocation.getErrorInfo());
            }
        }
    }

    private void checkPermissions(String... permissions) {
        List<String> needRequestPermissonList = findDeniedPermissions(permissions);
        if (null != needRequestPermissonList && needRequestPermissonList.size() > 0) {
            ActivityCompat.requestPermissions(this, needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]),
                    PERMISSON_REQUESTCODE);
        }
    }


    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     * @since 2.5.0
     */

    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<String>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this,
                    perm) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    this, perm)) {
                needRequestPermissonList.add(perm);
            }
        }
        return needRequestPermissonList;
    }

    @Override

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] paramArrayOfInt) {
        if (requestCode == PERMISSON_REQUESTCODE) {
            if (!verifyPermissions(paramArrayOfInt)) {
                showMissingPermissionDialog();
            }
        }
    }

    /**
     * 检测是否说有的权限都已经授权
     *
     * @param grantResults
     * @return
     * @since 2.5.0
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 显示提示信息
     *
     * @since 2.5.0
     */
    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("请授予权限");
        // 拒绝, 退出应用
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        builder.setPositiveButton("设置权限",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAppSettings();
                    }

                });
        builder.setCancelable(false);
        builder.show();
    }

    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    private void upImg(List<LocalMedia> result,int index) {
        LocalMedia localMedia = result.get(0);
            try {
                HttpUtil.uploadFile(localMedia.getPath(), localMedia.getFileName(), new HttpUtil.MyCallback() {
                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(SubmitActivity.this, message);

                    }
                    @Override
                    public void onError(String message) {
                        ToastUtil.showToast(SubmitActivity.this, message);

                    }

                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        ToastUtil.showToast(SubmitActivity.this, jsonObject.toJSONString());
                        switch (index) {
                            case 0:
                                gpsBean.setImg1Url(jsonObject.getString("message"));
                            case 1:
                                gpsBean.setImg2Url(jsonObject.getString("message"));
                            case 2:
                                gpsBean.setImg3Url(jsonObject.getString("message"));
                            case 3:
                                gpsBean.setImg4Url(jsonObject.getString("message"));
                        }
                        updateImg();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
    private void updateImg(){
//        if(TextUtils.isEmpty(gpsBean.getImg1Url())){
//            img2.setVisibility(View.INVISIBLE);
//            img1.setImageResource(R.drawable.add);
//        }else{
//            setImage(img1,gpsBean.getImg1Url());
//            img2.setVisibility(View.VISIBLE);
//        }
//        if(TextUtils.isEmpty(gpsBean.getImg2Url())){
//            img3.setVisibility(View.INVISIBLE);
//            img2.setImageResource(R.drawable.add);
//        }else{
//            setImage(img2,gpsBean.getImg2Url());
//            img3.setVisibility(View.VISIBLE);
//        }
//        if(TextUtils.isEmpty(gpsBean.getImg3Url())){
//            img4.setVisibility(View.INVISIBLE);
//            img3.setImageResource(R.drawable.add);
//        }else{
//            setImage(img3,gpsBean.getImg3Url());
//            img4.setVisibility(View.VISIBLE);
//        }
//        if(TextUtils.isEmpty(gpsBean.getImg4Url())){
//            img4.setImageResource(R.drawable.add);
//        }else{
//            setImage(img4,gpsBean.getImg4Url());
//        }
        setImage(img1,gpsBean.getImg1Url());
        setImage(img2,gpsBean.getImg2Url());
        setImage(img3,gpsBean.getImg3Url());
        setImage(img4,gpsBean.getImg4Url());
    }
    private void setImage(ImageView image, String url) {
        if(TextUtils.isEmpty(url)){
            image.setVisibility(View.INVISIBLE);
        }else{
        Glide.with(SubmitActivity.this).load(HttpUtil.URL+"sys/common/static/"+url).error(R.drawable.picture_icon_data_error).into(image);
    }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img1:
                clickAddPic(0);
                break;
            case R.id.img2:
                clickAddPic(1);
                break;
            case R.id.img3:
                clickAddPic(2);
                break;
            case R.id.img4:
                clickAddPic(3);
                break;
        }
    }
}
