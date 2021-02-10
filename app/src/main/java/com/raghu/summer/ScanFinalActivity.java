package com.raghu.summer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.raghu.summer.database.PDFListEssentialsDBHandler;
import com.raghu.summer.database.scannedImagesDBHandler;
import com.raghu.summer.model.ScanDetails;
import com.raghu.summer.model.ScanDocListDetails;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class ScanFinalActivity extends AppCompatActivity {

    private EditText prefix_edit_text;
    private EditText pdf_name_edit_text;
    private TextView quality_level_text_view,page_count_text_view;
    private SeekBar quality_seekBar;
    private Button save_pdf_btn;
    private SwitchCompat save_to_gallery_switch;
    ArrayList<ScanDetails> scan_list = new ArrayList<ScanDetails>();

    private scannedImagesDBHandler scannedImagesDBHandler;

    private PDFListEssentialsDBHandler pdfListEssentialsDBHandler;

    private static final int PERMISSION_CODE = 1;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_final);

        scannedImagesDBHandler = new scannedImagesDBHandler(this);
        pdfListEssentialsDBHandler = new PDFListEssentialsDBHandler(this);

        page_count_text_view = findViewById(R.id.page_count);
        pdf_name_edit_text = findViewById(R.id.scan_doc_name_edit_text);
        prefix_edit_text = findViewById(R.id.prefix_edit_text);
        quality_level_text_view = findViewById(R.id.quality_text_view);
        quality_seekBar = findViewById(R.id.quality_seekBar);
        save_to_gallery_switch = findViewById(R.id.save_images_switch);
        save_pdf_btn = findViewById(R.id.save_pdf_button);

        // GETTING EXTRAS FROM PREVIOUS ACTIVITY
        Bundle extras = getIntent().getExtras();
        int count = extras.getInt("no_of_pages");
        page_count_text_view.setText("Pages: " + count);

        getImageListFromDataBase();

        Log.d("TAG", "onCreate: " + scan_list.size());

        quality_seekBar.getProgressDrawable().setColorFilter(getColor(R.color.scan_red), PorterDuff.Mode.SCREEN);
        quality_seekBar.getThumb().setColorFilter(getColor(R.color.scan_red), PorterDuff.Mode.DARKEN);

        quality_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint({"SetTextI18n"})
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int current_progress = seekBar.getProgress();
                if (current_progress <= 3) {
                    String text = "Quality: LOW";
                    editSeekBar(text, R.color.scan_red, seekBar);
                } else if (current_progress > 7) {
                    String text = "Quality: HIGH";
                    editSeekBar(text, R.color.green, seekBar);
                } else {
                    String text = "Quality: MEDIUM";
                    editSeekBar(text, R.color.yellowish, seekBar);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        save_pdf_btn.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(pdf_name_edit_text.getText().toString())) {
                int progress = quality_seekBar.getProgress();

                String pdf_name = prefix_edit_text.getText().toString() + pdf_name_edit_text.getText().toString();

                CreateAndSavePDF(pdf_name, progress);

                if (save_to_gallery_switch.isChecked()) {
                    try {
                        saveToGallery(pdf_name, progress);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                scan_list.clear();
                scannedImagesDBHandler.deleteAllImages();

                Intent save_pdf_intent = new Intent(ScanFinalActivity.this, ScanListActivity.class);
                startActivity(save_pdf_intent);
                finish();
            } else {
//                    Toast.makeText(ScanFinalActivity.this, "Enter a name to the document", Toast.LENGTH_SHORT).show();
                pdf_name_edit_text.setError("Enter a name to the document");
            }
        });
    }

    private void saveToGallery(String pdf_name, int progress) throws IOException {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, PERMISSION_CODE);
            } else {
                for (int i = 0; i < scan_list.size(); i++) {
                    String imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
                    File image = new File(imagesDir, pdf_name + ".jpg");
                    OutputStream outputStream = new FileOutputStream(image);
                    scan_list.get(i).getImage().compress(Bitmap.CompressFormat.JPEG, progress * 10, outputStream);
                    Objects.requireNonNull(outputStream);
                    outputStream.close();
                }
            }
        } else {
            for (int i = 0; i < scan_list.size(); i++) {
                ContentResolver resolver = getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, pdf_name + "_" + i + 1 + ".jpeg");
                contentValues.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis());
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "Summer");

                Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                OutputStream outputStream = resolver.openOutputStream(Objects.requireNonNull(uri));

                scan_list.get(i).getImage().compress(Bitmap.CompressFormat.JPEG, progress * 10, outputStream);
                Objects.requireNonNull(outputStream);
                outputStream.close();
            }
        }
    }

    @Override
    public void onBackPressed() {
        scan_list.clear();
        scannedImagesDBHandler.deleteAllImages();
        super.onBackPressed();
    }

    private void editSeekBar(String text, int color, SeekBar seekBar) {
        Spannable spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(getColor(color)), 9, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        quality_level_text_view.setText(spannable, TextView.BufferType.SPANNABLE);
        seekBar.getProgressDrawable().setColorFilter(getColor(color), PorterDuff.Mode.SCREEN);
        seekBar.getThumb().setColorFilter(getColor(color), PorterDuff.Mode.DARKEN);
    }

    private void getImageListFromDataBase() {
        scan_list = scannedImagesDBHandler.getAllImages();
    }

    private void CreateAndSavePDF(String pdf_name, int progress) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, PERMISSION_CODE);
            } else {
                createPDF(pdf_name, progress);
            }
        } else {
            createPDF(pdf_name, progress);
        }
    }

    private void createPDF(String pdf_name, int progress) {
        Bitmap bitmap;
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();

//        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
//        Display display = windowManager.getDefaultDisplay();
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        this.getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
//        float height = displayMetrics.heightPixels;
//        float width = displayMetrics.widthPixels;
//
//        int converted_height = (int) height;
//        int converted_width = (int) width;

        for (int i = 0; i < scan_list.size(); i++) {
            bitmap = scan_list.get(i).getImage();
            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), i + 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            canvas.drawBitmap(bitmap, 0, 0, paint);
            pdfDocument.finishPage(page);
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            savePDFLessThanEqualToQ(pdfDocument, pdf_name);
        } else {
            savePDFGreaterThanQ(pdfDocument, pdf_name);
        }
    }

    private void savePDFGreaterThanQ(PdfDocument pdfDocument, String pdf_name) {
//        ContentResolver resolver = getContentResolver();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, pdf_name+".pdf");
//        contentValues.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis());
//        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "doc/pdf");
//        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + File.separator+"Summer");
//
////        File file = new File(getFilesDir(),)
////        pdfDocument.writeTo();
    }

    private void savePDFLessThanEqualToQ(PdfDocument pdfDocument, String pdf_name) {
        File root = new File(getExternalFilesDir(null) + "/Summer");
        if (!root.exists()) root.mkdir();
        File file = new File(root, pdf_name + ".pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        pdfDocument.close();

        long tsLong = System.currentTimeMillis()/1000;

        pdfListEssentialsDBHandler.storePDF(new ScanDocListDetails(scan_list.get(0).getImage(),pdf_name,getDate(tsLong),scan_list.get(0).getPos()));

        Toast.makeText(this, "PDF saved to " + root.toString(), Toast.LENGTH_SHORT).show();
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }
}