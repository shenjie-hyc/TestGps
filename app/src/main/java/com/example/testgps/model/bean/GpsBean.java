package com.example.testgps.model.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GpsBean implements Serializable {
    static final long serialVersionUID = 42L;
    String createBy;
    String createTime;
    String id;
    String img1Url;
    String img2Url;
    String img3Url;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    String img4Url;
    String address;
    double lng;
    double lat;

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg1Url() {
        return img1Url;
    }

    public void setImg1Url(String img1Url) {
        this.img1Url = img1Url;
    }

    public String getImg2Url() {
        return img2Url;
    }

    public void setImg2Url(String img2Url) {
        this.img2Url = img2Url;
    }

    public String getImg3Url() {
        return img3Url;
    }

    public void setImg3Url(String img3Url) {
        this.img3Url = img3Url;
    }

    public String getImg4Url() {
        return img4Url;
    }

    public void setImg4Url(String img4Url) {
        this.img4Url = img4Url;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }
    public List<String> getImgList(){
        ArrayList list = new ArrayList();
        list.add(img1Url);
        list.add(img2Url);
        list.add(img3Url);
        list.add(img4Url);

        return list;
    }
}
