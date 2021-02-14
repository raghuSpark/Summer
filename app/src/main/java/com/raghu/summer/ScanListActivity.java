package com.raghu.summer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.raghu.summer.database.PDFListEssentialsDBHandler;
import com.raghu.summer.model.ScanDocListDetails;
import com.raghu.summer.view.ScannedDocsRecyclerViewAdapter;

import java.io.File;
import java.util.ArrayList;

public class ScanListActivity extends AppCompatActivity {

    private Button DOC_button;
    //    private ImageButton fabButton;
    private EditText searchBar;
    private TextView noResults;

    private RecyclerView recyclerView;
    private ScannedDocsRecyclerViewAdapter docsRecyclerViewAdapter;
    private ArrayList<ScanDocListDetails> doc_list = new ArrayList<>();

    PDFListEssentialsDBHandler pdfListEssentialsDBHandler;

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_list);

//        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//        StrictMode.setVmPolicy(builder.build());

        recyclerView = findViewById(R.id.scan_recycler_view);
        noResults = findViewById(R.id.no_results_text_view);

        pdfListEssentialsDBHandler = new PDFListEssentialsDBHandler(this);
        doc_list = pdfListEssentialsDBHandler.getAllPDFs();

        init();
        generateItem();

        if (doc_list.size() == 0) {
            noResults.setVisibility(View.VISIBLE);
            noResults.setText("No Documents Saved!");
        } else {
            noResults.setVisibility(View.INVISIBLE);
        }

        docsRecyclerViewAdapter.setOnItemClickListener(position -> {
            File file = new File(getExternalFilesDir(null) + "/Summer/" + doc_list.get(position).getDocName() + ".pdf");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = FileProvider.getUriForFile(ScanListActivity.this, BuildConfig.APPLICATION_ID + ".provider", file);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        });

        searchBar = findViewById(R.id.search_bar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        searchBar.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                searchBar.setText(null);
                searchBar.clearFocus();
            }
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

    @SuppressLint("SetTextI18n")
    private void filter(String text) {
        ArrayList<ScanDocListDetails> filteredList = new ArrayList<>();
        if (doc_list.size() == 0) return;
        for (ScanDocListDetails item : doc_list) {
            if (item.getDocName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        if (filteredList.size() == 0) {
            noResults.setVisibility(View.VISIBLE);
            noResults.setText("No results found...");
        } else {
            noResults.setVisibility(View.INVISIBLE);
        }
        docsRecyclerViewAdapter.filteredList(filteredList);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ScanListActivity.this, DashboardActivity.class));
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
