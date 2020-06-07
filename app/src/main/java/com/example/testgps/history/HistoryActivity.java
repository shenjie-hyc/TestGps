package com.example.testgps.history;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.testgps.R;
import com.example.testgps.http.HttpUtil;
import com.example.testgps.model.UserModel;
import com.example.testgps.model.bean.GpsBean;
import com.example.testgps.submit.SubmitActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.testgps.submit.SubmitActivity.GPS_KEY;

public class HistoryActivity extends AppCompatActivity {
    private EditText edit_start;
    private EditText edit_end;
    private TextView btn_show;
    private MapView mapView;
    AMap aMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        initView();
        mapView.onCreate(savedInstanceState);
    }

    private void initView() {
        edit_start = (EditText) findViewById(R.id.edit_start);
        edit_end = (EditText) findViewById(R.id.edit_end);
        btn_show = (TextView) findViewById(R.id.btn_show);
        mapView = (MapView) findViewById(R.id.map);
        btn_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        aMap = mapView.getMap();
        edit_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTime(edit_start);
            }
        });
        edit_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTime(edit_end);
            }
        });
    }

    private void submit() {
        String start = edit_start.getText().toString().trim();
        if (TextUtils.isEmpty(start)) {
            Toast.makeText(this, "开始时间不能为空！", Toast.LENGTH_LONG).show();
            return;
        }
        String end = edit_end.getText().toString().trim();
        if (TextUtils.isEmpty(end)) {
            Toast.makeText(this, "结束时间不能为空！", Toast.LENGTH_LONG).show();
            return;
        }
        String startTime = edit_start.getText().toString();
        String endTime = edit_end.getText().toString();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("createBy", UserModel.getUserModel().getUserBean().getUsername());
        jsonObject.put("startTime", startTime);
        jsonObject.put("endTime", endTime);
        jsonObject.put("pageSize", 1000);

        HttpUtil.get("gps/list", jsonObject, new HttpUtil.MyCallBack() {
            @Override
            public void onFailure(String message) {

            }

            @Override
            public void onError(String message) {

            }

            @Override
            public void onSuccess(JSONObject jsonObject) {
                Log.d("AAA", jsonObject.toJSONString());
                JSONArray jsonArray = jsonObject.getJSONArray("records");
                aMap.clear();
                LatLngBounds.Builder builder = LatLngBounds.builder();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    LatLng latLng = new LatLng(json.getDouble("lat"),json.getDouble("lng"));
                    Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title(json.getString(json.getString("createTime"))).snippet(json.getString("address")));
                    marker.setObject(json);
                    builder.include(latLng);
                }
                LatLngBounds latLngBounds = builder.build();
                aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 10, 10, 10, 10));

            }
        });
        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                JSONObject jsonObject = (JSONObject) marker.getObject();
                GpsBean gpsBean = JSON.parseObject(jsonObject.toJSONString(), GpsBean.class);
                Intent intent = new Intent(HistoryActivity.this, SubmitActivity.class);
                intent.putExtra(GPS_KEY,gpsBean);
                startActivity(intent);
                return false;
            }
        });
    }

    private void selectTime(final TextView textView) {
        //时间选择器
        TimePickerView pvTime = new TimePickerBuilder(HistoryActivity.this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                textView.setText(simpleDateFormat.format(date));
            }
        }).setType(new boolean[]{true, true, true, true, true, true})
                .build();
        pvTime.show();
    }
}
