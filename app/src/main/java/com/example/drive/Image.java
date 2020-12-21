package com.example.drive;

public class Image {
    private String strImage;
    private String dateOfUploaded;
    private String imageUri;

    public Image(String strImage, String dateOfUploaded, String imageUri) {
        this.strImage = strImage;
        this.dateOfUploaded = dateOfUploaded;
        this.imageUri = imageUri;
    }

    public String getImageUri() {
        return imageUri;
    }
    public String getStrImage() {
        return strImage;
    }
    public String getDateOfUploaded() { return dateOfUploaded; }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
    public void setStrImage(String strImage) {
        this.strImage = strImage;
    }
    public void setDateOfUploaded(String dateOfUploaded) { this.dateOfUploaded = dateOfUploaded; }
}
