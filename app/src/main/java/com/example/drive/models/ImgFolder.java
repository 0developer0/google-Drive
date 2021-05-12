package com.example.drive.models;

public class ImgFolder {
    private String folderName;
    private String pathUri;
    private String coverImage;
    private int countOfImage;

    //Getter
    public String getFolderName() { return folderName; }
    public int getCountOfImage() { return countOfImage; }
    public String getPathUri() { return pathUri; }
    public String getCoverImage() { return coverImage; }

    //Setter
    public void setFolderName(String folderName) { this.folderName = folderName; }
    public void setCountOfImage(int countOfImage) { this.countOfImage = countOfImage; }
    public void setPathUri(String path) { this.pathUri = path; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }

    public void addNewImg() { this.countOfImage++; }
}