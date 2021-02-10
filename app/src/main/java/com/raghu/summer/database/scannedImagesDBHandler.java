package com.raghu.summer.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.raghu.summer.model.ScanDetails;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class scannedImagesDBHandler extends SQLiteOpenHelper {

    Context context;

    private static final String DATABASE_NAME = "summerScanPreviewDB.db";
    private static final int DATABASE_VERSION = 1;

    private static final String createTableQuery = "create table imageList (imagePosition NUMBER" + ",image BLOB)";

    public scannedImagesDBHandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTableQuery);
        Toast.makeText(context, "Table Created Successfully in our db", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void storeImage(ScanDetails scanDetails) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Bitmap imageToBeStored = scanDetails.getImage();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageToBeStored.compress(Bitmap.CompressFormat.JPEG,100, byteArrayOutputStream);

        byte[] imageByte = byteArrayOutputStream.toByteArray();
        ContentValues contentValues = new ContentValues();
        contentValues.put("imagePosition",scanDetails.getPos());
        contentValues.put("image", imageByte);

        long check = sqLiteDatabase.insert("imageList",null,contentValues);
        if(check!=-1) {
            sqLiteDatabase.close();
        } else Toast.makeText(context, "Error!Failed to insert image into the database", Toast.LENGTH_SHORT).show();
    }

    public ArrayList<ScanDetails> getAllImages() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        ArrayList<ScanDetails> scan_list = new ArrayList<>();

        //cursor that gets the values back
        @SuppressLint("Recycle")
        Cursor cursor = sqLiteDatabase.rawQuery("select * from imageList",null);

        if (cursor.getCount()!=0) {
            while (cursor.moveToNext()) {
                int image_pos = cursor.getInt(0);
                byte[] image_bytes = cursor.getBlob(1);

                Bitmap image_bitmap = BitmapFactory.decodeByteArray(image_bytes,0,image_bytes.length);
                scan_list.add(new ScanDetails(image_pos,image_bitmap));
            }
            return scan_list;

        } else {
            Toast.makeText(context, "NO IMAGES ARE PRESENT", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public void deleteAllImages() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete("imageList",null,null);
    }
}
