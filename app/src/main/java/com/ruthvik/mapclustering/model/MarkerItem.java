package com.ruthvik.mapclustering.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Vijay Ganduri on 05/10/17.
 */

public class MarkerItem implements ClusterItem {
    private final LatLng mPosition;
    private final Object object;
    private String mTitle;
    private String mSnippet;

    public MarkerItem(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
        mTitle = null;
        mSnippet = null;
        object = null;
    }

    public MarkerItem(double lat, double lng, String title, Object object) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        this.object = object;
    }

    public MarkerItem(double lat, double lng, String title, String snippet) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mSnippet = snippet;
        object = null;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }

    public Object getObject() {
        return object;
    }
}