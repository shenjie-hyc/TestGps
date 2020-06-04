package com.example.testgps.model;

import com.example.testgps.model.bean.UserBean;

public class UserModel {
    private static UserModel userModel;
    private UserBean userBean;

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    public static UserModel getUserModel() {
        if (userModel == null) {
            userModel = new UserModel();
        }
        return userModel;
    }
}
