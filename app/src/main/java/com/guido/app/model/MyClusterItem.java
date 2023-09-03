package com.guido.app.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MyClusterItem implements ClusterItem {
    private final LatLng mPosition;
    private String name;
    private String address;
    private String iconUrl;

    public MyClusterItem(double lat, double lng, String name, String address,String iconUrl) {
        this.name = name;
        this.address = address;
        this.iconUrl = iconUrl;
        mPosition = new LatLng(lat, lng);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getSnippet() {
        return address;
    }


    public String getIconUrl() {
        return iconUrl;
    }
}
