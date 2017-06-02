package info.androidhive.materialdesign.model;

import android.util.Log;

/**
 * Created by yuqiao-liucs on 2016/3/15.
 */
public class Product {
    private String name,id,cname,time,descroption;
    private double lat,lng;

    public Product(String name, String id, String cname, String time, String descroption,double lat,double lng) {
        Log.i("tanjin",name+id+cname+time+descroption+lat+lng);
        this.name = name;
        this.id = id;
        this.cname = cname;
        this.time = time;
        this.descroption = descroption;
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getDescroption() {
        return descroption;
    }

    public void setDescroption(String descroption) {
        this.descroption = descroption;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getCname() {
        return cname;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }
}
