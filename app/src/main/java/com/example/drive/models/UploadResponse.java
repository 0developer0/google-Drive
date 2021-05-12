package com.example.drive.models;

public class UploadResponse {
    private String message;
    private String fileUri;
    private String dateOfUploaeded;


    public String getMessage() {
        return message;
    }
    public String getFileUri() {
        return fileUri;
    }
    public String getDateOfUploaeded() {
        return dateOfUploaeded;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }
    public void setDateOfUploaeded(String dateOfUploaeded) {
        this.dateOfUploaeded = dateOfUploaeded;
    }
}
