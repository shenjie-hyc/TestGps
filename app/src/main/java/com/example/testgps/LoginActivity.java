package com.example.testgps;

import android.content.Intent;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.example.testgps.http.HttpUtil;
import com.example.testgps.model.UserModel;
import com.example.testgps.model.bean.UserBean;
import com.example.testgps.util.ToastUtil;

import androidx.appcompat.app.AppCompatActivity;


import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class LoginActivity extends AppCompatActivity {
    EditText editName;
    EditText editPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editName = findViewById(R.id.edit_name);
        editPassword = findViewById(R.id.edit_password);
        btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", editName.getText().toString());
                jsonObject.put("password", editPassword.getText().toString());
                HttpUtil.post("/sys/login", jsonObject, new HttpUtil.MyCallBack() {
                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(LoginActivity.this, message);
                    }

                    @Override
                    public void onError(String message) {
                        ToastUtil.showToast(LoginActivity.this, message);
                    }

                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        ToastUtil.showToast(LoginActivity.this, "Login Successfully！");
                        JSONObject jsonUserInfo = jsonObject.getJSONObject("userInfo");
                        UserBean userBean = JSON.parseObject(jsonUserInfo.toString(),UserBean.class);
                        UserModel.getUserModel().setUserBean(userBean);
                        try {
                            HttpUtil.setToken(jsonObject.getString("token"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                    }
                });

            }
        });

    }

}
