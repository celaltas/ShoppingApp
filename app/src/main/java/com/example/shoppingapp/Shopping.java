package com.example.shoppingapp;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Shopping implements Serializable {

    private String id;
    private String name;
    private Date date;
    private String place;
    private String imageUri;
    private boolean status;

    public Shopping(String name, Date date, String place,boolean status) {
        this.name = name;
        this.date = date;
        this.place = place;
        this.status = status;
    }

    public Shopping(String id, String name, Date date, String place,String imageUri, boolean status) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.place = place;
        this.imageUri = imageUri;
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }

    public String getId() {
        return id;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String,Object>();
        map.put("name", this.name);
        map.put("date", this.date);
        map.put("place", this.place);
        map.put("status", this.status);

        return map;
    }

    /*Shopping fromMap( Map<String, Object> map){
        Shopping shopping = new Shopping( map.get("name").toString(), (Date) map.get("date"), map.get("place").toString());
        return  shopping;
    }

     */

    @Override
    public String toString() {
        return "Shopping{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", date=" + date +
                ", place='" + place + '\'' +
                '}';
    }
}
