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

import com.raghu.summer.model.ScanDocListDetails;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class PDFListEssentialsDBHandler extends SQLiteOpenHelper {

    Context context;

    private static final String DATABASE_NAME = "summerPDFListDB.db";
    private static final int DATABASE_VERSION = 5;

    private static final String createTableQuery = "create table PDFList (PDFPosition NUMBER" + ",pdfTitle STRING" + ",timeStamp STRING" + ",image BLOB)";


    public PDFListEssentialsDBHandler(@Nullable Context context) {
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

    public void storePDF(ScanDocListDetails scanDocListDetails) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Bitmap PDFToBeStored = scanDocListDetails.getPreviewImage();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PDFToBeStored.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] imageByte = byteArrayOutputStream.toByteArray();
        ContentValues contentValues = new ContentValues();
        contentValues.put("PDFPosition", scanDocListDetails.getPos());
        contentValues.put("pdfTitle", scanDocListDetails.getDocName());
        contentValues.put("timeStamp", scanDocListDetails.getDate());
        contentValues.put("image", imageByte);

        long check = sqLiteDatabase.insert("PDFList", null, contentValues);
        if (check != -1) {
            sqLiteDatabase.close();
        } else {
            Toast.makeText(context, "Error!Failed to insert image into the database", Toast.LENGTH_SHORT).show();
        }
    }

    public ArrayList<ScanDocListDetails> getAllPDFs() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        ArrayList<ScanDocListDetails> doc_list = new ArrayList<>();

        //cursor that gets the values back
        @SuppressLint("Recycle")
        Cursor cursor = sqLiteDatabase.rawQuery("select * from PDFList", null);

        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                int pdf_pos = cursor.getInt(0);
                String doc_name = cursor.getString(1);
                String date = cursor.getString(2);
                byte[] image_bytes = cursor.getBlob(3);
                Bitmap image_bitmap = BitmapFactory.decodeByteArray(image_bytes, 0, image_bytes.length);
                int height = Math.min(image_bitmap.getHeight(), 450);
                image_bitmap = Bitmap.createBitmap(image_bitmap, 0, 0, image_bitmap.getWidth(), height);
                doc_list.add(new ScanDocListDetails(image_bitmap, doc_name, date, pdf_pos));
            }
        } else {
            Toast.makeText(context, "NO Documents ARE PRESENT", Toast.LENGTH_SHORT).show();
        }
        sqLiteDatabase.close();
        return doc_list;
    }

    public boolean deleteAPDF(String doc_title) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.delete("PDFList", "pdfTitle" + "='" + doc_title + "' ;", null) > 0;
    }

    public boolean updatePDFName(String new_name, String date, Bitmap preview_image, int pos) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        ContentValues args = new ContentValues();
        args.put("PDFPosition", pos);
        args.put("pdfTitle", new_name);
        args.put("timeStamp", date);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        preview_image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        args.put("image", byteArray);
        sqLiteDatabase.update("PDFList",args,"PDFPosition" + "='" + pos + "' ;", null);
        sqLiteDatabase.close();
        return true;
    }

    public boolean alreadyExists(String new_name) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        @SuppressLint("Recycle")
        Cursor cursor = sqLiteDatabase.rawQuery("select * from PDFList", null);
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                if (new_name.equals(cursor.getString(1))) {
                    sqLiteDatabase.close();
                    return true;
                }
            }
        }
        sqLiteDatabase.close();
        return false;
    }
}
