package com.raghu.summer.model;

import android.graphics.Bitmap;

import java.io.File;

public class ScanDocListDetails {
    private Bitmap previewImage;
    private String docName;
    private String date;
    int pos;

    public ScanDocListDetails(Bitmap previewImage, String docName, String date, int pos) {
        this.previewImage = previewImage;
        this.docName = docName;
        this.date = date;
        this.pos = pos;
    }

    public Bitmap getPreviewImage() {
        return previewImage;
    }

    public void setPreviewImage(Bitmap previewImage) {
        this.previewImage = previewImage;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}
