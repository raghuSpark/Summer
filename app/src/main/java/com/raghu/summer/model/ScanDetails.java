package com.raghu.summer.model;

import android.graphics.Bitmap;

public class ScanDetails {
    private int pos;
    private Bitmap image;
    private boolean checked;

    public ScanDetails(Bitmap image) {
        this.image = image;
    }

    public ScanDetails(int pos, Bitmap image) {
        this.pos = pos;
        this.image = image;
    }

    public ScanDetails(int pos, Bitmap image, boolean checked) {
        this.pos = pos;
        this.image = image;
        this.checked = checked;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
