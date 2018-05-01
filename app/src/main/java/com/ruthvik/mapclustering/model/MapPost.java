package com.ruthvik.mapclustering.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MapPost implements Parcelable, Cloneable {

    @SerializedName("postId")
    @Expose
    private String postId;

    @SerializedName("publishedDate")
    @Expose
    private String publishedDate;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("sourceId")
    @Expose
    private String sourceId;

    @SerializedName("newsTypeId")
    @Expose
    private String newsTypeId;

    @SerializedName("videoTypeId")
    @Expose
    private String videoTypeId;

    @SerializedName("location")
    @Expose
    private Location location;

    @SerializedName("categoryId")
    @Expose
    private String categoryId;

    @SerializedName("activityCount")
    @Expose
    private int activityCount;

    @SerializedName("hot")
    @Expose
    private boolean hot;

    public MapPost() {
    }

    protected MapPost(Parcel in) {
        postId = in.readString();
        publishedDate = in.readString();
        title = in.readString();
        sourceId = in.readString();
        newsTypeId = in.readString();
        videoTypeId = in.readString();
        location = in.readParcelable(Location.class.getClassLoader());
        categoryId = in.readString();
        activityCount = in.readInt();
        hot = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(postId);
        dest.writeString(publishedDate);
        dest.writeString(title);
        dest.writeString(sourceId);
        dest.writeString(newsTypeId);
        dest.writeString(videoTypeId);
        dest.writeParcelable(location, flags);
        dest.writeString(categoryId);
        dest.writeInt(activityCount);
        dest.writeByte((byte) (hot ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MapPost> CREATOR = new Creator<MapPost>() {
        @Override
        public MapPost createFromParcel(Parcel in) {
            return new MapPost(in);
        }

        @Override
        public MapPost[] newArray(int size) {
            return new MapPost[size];
        }
    };

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getNewsTypeId() {
        return newsTypeId;
    }

    public void setNewsTypeId(String newsTypeId) {
        this.newsTypeId = newsTypeId;
    }

    public String getVideoTypeId() {
        return videoTypeId;
    }

    public void setVideoTypeId(String videoTypeId) {
        this.videoTypeId = videoTypeId;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public int getActivityCount() {
        return activityCount;
    }

    public void setActivityCount(int activityCount) {
        this.activityCount = activityCount;
    }

    public boolean isHot() {
        return hot;
    }

    public void setHot(boolean hot) {
        this.hot = hot;
    }

    @Override
    public String toString() {
        return "MapPost{" +
                "postId='" + postId + '\'' +
                ", publishedDate='" + publishedDate + '\'' +
                ", title='" + title + '\'' +
                ", sourceId='" + sourceId + '\'' +
                ", newsTypeId='" + newsTypeId + '\'' +
                ", videoTypeId='" + videoTypeId + '\'' +
                ", location=" + location +
                ", categoryId='" + categoryId + '\'' +
                ", activityCount=" + activityCount +
                ", hot=" + hot +
                '}';
    }
}