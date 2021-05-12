package com.example.drive.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Img implements Parcelable {
    private String dateOfModified;
    private String dateOfUploaded;
    private String headerDate;
    private String contentUri;
    private String imageName;
    private Boolean isSelected;
    private Boolean isCurrentItem;
    private Boolean isUploading;
    private Boolean isSuccessful;

    public Img() {
        isSelected = false;
        isCurrentItem = false;
        isUploading = false;
    }

    public Img(String imageName, String contentUri, Boolean isUploading) {
        this.imageName = imageName;
        this.contentUri = contentUri;
        this.isUploading = isUploading;
    }

    public Img(String contentUri) {
        this.contentUri = contentUri;
    }

    public String getContentUri() { return contentUri; }
    public String getDateOfModified() { return dateOfModified; }
    public String getHeaderDate() { return headerDate; }
    public String getImageName() { return imageName; }
    public Boolean getSelected() { return isSelected; }
    public Boolean getCurrentItem() { return isCurrentItem; }
    public Boolean getUploading() { return isUploading; }
    public Boolean getSuccessful() {
        return isSuccessful;
    }

    public void setContentUri(String contentUri) {
        this.contentUri = contentUri;
    }
    public void setDateOfModified(String dateOfModified) { this.dateOfModified = dateOfModified; }
    public void setHeaderDate(String headerDate) { this.headerDate = headerDate; }
    public void setImageName(String imageName) { this.imageName = imageName; }
    public void setSelected(Boolean selected) { isSelected = selected; }
    public void setCurrentItem(Boolean currentItem) { isCurrentItem = currentItem; }
    public void setUploading(Boolean uploaded) { isUploading = uploaded; }
    public void setSuccessful(Boolean successful) {
        isSuccessful = successful;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.dateOfModified);
        dest.writeString(this.headerDate);
        dest.writeString(this.contentUri);
        dest.writeString(this.imageName);
        dest.writeValue(this.isSelected);
        dest.writeValue(this.isCurrentItem);
        dest.writeValue(this.isUploading);
        dest.writeValue(this.isSuccessful);
    }

    public void readFromParcel(Parcel source) {
        this.dateOfModified = source.readString();
        this.headerDate = source.readString();
        this.contentUri = source.readString();
        this.imageName = source.readString();
        this.isSelected = (Boolean) source.readValue(Boolean.class.getClassLoader());
        this.isCurrentItem = (Boolean) source.readValue(Boolean.class.getClassLoader());
        this.isUploading = (Boolean) source.readValue(Boolean.class.getClassLoader());
        this.isSuccessful = (Boolean) source.readValue(Boolean.class.getClassLoader());
    }

    protected Img(Parcel in) {
        this.dateOfModified = in.readString();
        this.headerDate = in.readString();
        this.contentUri = in.readString();
        this.imageName = in.readString();
        this.isSelected = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isCurrentItem = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isUploading = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isSuccessful = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Parcelable.Creator<Img> CREATOR = new Parcelable.Creator<Img>() {
        @Override
        public Img createFromParcel(Parcel source) {
            return new Img(source);
        }

        @Override
        public Img[] newArray(int size) {
            return new Img[size];
        }
    };
}