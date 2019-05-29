package com.srinivas.biowax;

public class Hospitals {
    String h_name,h_code,h_address,route_name,mobile,hcf_id;
    Double h_lat,h_long;

    public Hospitals(String hcf_id,String h_name, String h_code, Double h_lat, Double h_long, String h_address,String route_name,String mobile) {
        this.hcf_id=hcf_id;
        this.h_name = h_name;
        this.h_code = h_code;
        this.h_lat = h_lat;
        this.h_long = h_long;
        this.h_address = h_address;
        this.route_name = route_name;
        this.mobile = mobile;
    }

    public String getHcf_id() {
        return hcf_id;
    }

    public void setHcf_id(String hcf_id) {
        this.hcf_id = hcf_id;
    }

    public String getRoute_name() {
        return route_name;
    }

    public void setRoute_name(String route_name) {
        this.route_name = route_name;
    }

    public String getH_name() {
        return h_name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setH_name(String h_name) {
        this.h_name = h_name;
    }

    public String getH_code() {
        return h_code;
    }

    public void setH_code(String h_code) {
        this.h_code = h_code;
    }

    public Double getH_lat() {
        return h_lat;
    }

    public void setH_lat(Double h_lat) {
        this.h_lat = h_lat;
    }

    public Double getH_long() {
        return h_long;
    }

    public void setH_long(Double h_long) {
        this.h_long = h_long;
    }

    public String getH_address() {
        return h_address;
    }

    public void setH_address(String h_address) {
        this.h_address = h_address;
    }
}
