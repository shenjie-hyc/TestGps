package com.example.testgps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.testgps.model.UserModel;
import com.example.testgps.model.bean.UserBean;
import com.example.testgps.submit.SubmitActivity;

public class MainActivity extends AppCompatActivity {
    private TextView txt_id;
    private TextView txt_name;
    private TextView btn_up;
    private TextView btn_view_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }
    private void initView(){
        txt_id = (TextView)findViewById(R.id.txt_id);
        txt_name = (TextView)findViewById(R.id.txt_name);
        btn_up = (TextView)findViewById(R.id.btn_up);
        btn_view_list = (TextView)findViewById(R.id.btn_view_list);
        btn_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SubmitActivity.class));
            }
        });
        btn_view_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    private void initData(){
        UserBean userBean = UserModel.getUserModel().getUserBean();

        txt_id.setText(userBean.getId());
        txt_name.setText(userBean.getRealname());
    }
}
