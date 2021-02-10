package com.raghu.summer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.raghu.summer.database.PDFListEssentialsDBHandler;
import com.raghu.summer.model.ScanDocListDetails;
import com.raghu.summer.view.ScannedDocsRecyclerViewAdapter;

import java.io.File;
import java.util.ArrayList;

public class ScanListActivity extends AppCompatActivity {

    private Button DOC_button;
//    private ImageButton fabButton;
    private EditText searchBar;

    private RecyclerView recyclerView;
    private ScannedDocsRecyclerViewAdapter docsRecyclerViewAdapter;
    private ArrayList<ScanDocListDetails> doc_list = new ArrayList<>();

    PDFListEssentialsDBHandler pdfListEssentialsDBHandler;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_list);

//        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//        StrictMode.setVmPolicy(builder.build());

        recyclerView = findViewById(R.id.scan_recycler_view);
        searchBar = findViewById(R.id.search_bar);

        pdfListEssentialsDBHandler = new PDFListEssentialsDBHandler(this);
        doc_list = pdfListEssentialsDBHandler.getAllPDFs();

        init();
        generateItem();

        docsRecyclerViewAdapter.setOnItemClickListener(position -> {
            File file = new File(getExternalFilesDir(null) + "/Summer/" + doc_list.get(position).getDocName()+".pdf");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = FileProvider.getUriForFile(ScanListActivity.this,BuildConfig.APPLICATION_ID + ".provider",file);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        });



        DOC_button = findViewById(R.id.DOC_btn);
        DOC_button.setOnClickListener(v -> {
            Intent i = new Intent(ScanListActivity.this, ScanListPreviewActivity.class);
            startActivity(i);
            finish();
        });

//        fabButton = findViewById(R.id.create_new_file_btn);
//        fabButton.setOnClickListener(v -> {
//            Intent i = new Intent(ScanListActivity.this, ScanListPreviewActivity.class);
//            startActivity(i);
//            finish();
//        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ScanListActivity.this,DashboardActivity.class));
    }

    private void generateItem() {
        //Set Adapter
        docsRecyclerViewAdapter = new ScannedDocsRecyclerViewAdapter(doc_list);
        recyclerView.setAdapter(docsRecyclerViewAdapter);
        docsRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void init() {
        recyclerView = findViewById(R.id.scan_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
