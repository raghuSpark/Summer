package com.raghu.summer.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.raghu.summer.BuildConfig;
import com.raghu.summer.R;
import com.raghu.summer.database.PDFListEssentialsDBHandler;
import com.raghu.summer.model.ScanDocListDetails;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScannedDocsRecyclerViewAdapter extends RecyclerView.Adapter {

    private List<ScanDocListDetails> scanDocListDetailsList;
    private ScannedDocsRecyclerViewAdapter.OnItemClickListener mListener;
    Context context;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(ScannedDocsRecyclerViewAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public ScannedDocsRecyclerViewAdapter(List<ScanDocListDetails> scanDocListDetailsList) {
        this.scanDocListDetailsList = scanDocListDetailsList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.scan_cards, parent, false);
        return new MyViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ScannedDocsRecyclerViewAdapter.MyViewHolder myViewHolder = (MyViewHolder) holder;

        ScanDocListDetails item = scanDocListDetailsList.get(position);
        myViewHolder.previewImage.setImageBitmap(item.getPreviewImage());
        myViewHolder.docName.setText(item.getDocName());
        myViewHolder.dateAdded.setText(item.getDate());
    }

    @Override
    public int getItemCount() {
        return scanDocListDetailsList.size();
    }

    public void filteredList(ArrayList<ScanDocListDetails> mFilteredList) {
        scanDocListDetailsList = mFilteredList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView previewImage;
        TextView dateAdded, doneButton, docName;
        EditText edit_name;

        LinearLayout linearLayout1, linearLayout2;

        ImageButton openMenuButton, closeMenuButton, deleteButton, shareButton, editButton, forwardButton;

        @SuppressLint({"QueryPermissionsNeeded", "UseCompatLoadingForDrawables", "ResourceAsColor"})
        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (listener != null) {
                    if (pos != RecyclerView.NO_POSITION) {
                        listener.onItemClick(pos);
                    }
                }
            });

            linearLayout1 = itemView.findViewById(R.id.scan_card_details);
            linearLayout2 = itemView.findViewById(R.id.scan_card_extras);

            previewImage = itemView.findViewById(R.id.scan_preview);
            docName = itemView.findViewById(R.id.scan_title);
            edit_name = itemView.findViewById(R.id.edit_doc_name);
            dateAdded = itemView.findViewById(R.id.scan_timeStamp);
            doneButton = itemView.findViewById(R.id.edit_done);
            openMenuButton = itemView.findViewById(R.id.scan_menu);
            closeMenuButton = itemView.findViewById(R.id.close_menu);
            deleteButton = itemView.findViewById(R.id.delete_doc);
            shareButton = itemView.findViewById(R.id.share_doc);
            editButton = itemView.findViewById(R.id.edit_doc);
            forwardButton = itemView.findViewById(R.id.forward_doc);

            openMenuButton.setOnClickListener(v -> {
                linearLayout1.setVisibility(View.GONE);
                linearLayout2.setVisibility(View.VISIBLE);
            });

            closeMenuButton.setOnClickListener(v -> {
                linearLayout1.setVisibility(View.VISIBLE);
                linearLayout2.setVisibility(View.GONE);
            });

            editButton.setOnClickListener(v -> {
                linearLayout2.setVisibility(View.GONE);
                openMenuButton.setVisibility(View.GONE);
                dateAdded.setVisibility(View.GONE);
                docName.setVisibility(View.GONE);
                linearLayout1.setVisibility(View.VISIBLE);
                doneButton.setVisibility(View.VISIBLE);
                edit_name.setVisibility(View.VISIBLE);
                edit_name.setText(docName.getText().toString(), TextView.BufferType.EDITABLE);
            });

            doneButton.setOnClickListener(v -> {
                String new_name = edit_name.getText().toString();
                if (new_name.length() == 0) {
                    Toast.makeText(context, "Document can't be empty!", Toast.LENGTH_SHORT).show();
                } else {
                    PDFListEssentialsDBHandler pdfListEssentialsDBHandler = new PDFListEssentialsDBHandler(context);
                    int i = 1;
                    String temp = new_name;
                    while (pdfListEssentialsDBHandler.alreadyExists(new_name)) {
                        new_name = temp + "(" + i + ")";
                        i++;
                    }

                    doneButton.setVisibility(View.GONE);
                    docName.setVisibility(View.VISIBLE);
                    docName.setText(new_name);
                    edit_name.setVisibility(View.GONE);
                    openMenuButton.setVisibility(View.VISIBLE);
                    dateAdded.setVisibility(View.VISIBLE);
                    scanDocListDetailsList.get(getAdapterPosition()).setDocName(docName.getText().toString());

                    int pos = getAdapterPosition();
                    if (pdfListEssentialsDBHandler.updatePDFName(new_name, scanDocListDetailsList.get(pos).getDate(), scanDocListDetailsList.get(pos).getPreviewImage(), pos)) {
                        File oldFile = new File(context.getExternalFilesDir(null) + "/Summer/" + scanDocListDetailsList.get(pos).getDocName() + ".pdf");
                        Log.d("TAG", "MyViewHolder: "+oldFile.toString());
                        oldFile.renameTo(new File(oldFile, new_name));
                        Log.d("TAG", "MyViewHolder: "+oldFile.toString());
                        Toast.makeText(context, "Successfully Edited!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Error occurred in editing the file! Please try later", Toast.LENGTH_SHORT).show();
                    }
                    pdfListEssentialsDBHandler.close();
                }
            });

            deleteButton.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                PDFListEssentialsDBHandler pdfListEssentialsDBHandler = new PDFListEssentialsDBHandler(context);
                if (pdfListEssentialsDBHandler.deleteAPDF(scanDocListDetailsList.get(pos).getDocName())) {
                    File file = new File(context.getExternalFilesDir(null) + "/Summer/" + scanDocListDetailsList.get(pos).getDocName() + ".pdf");
                    if(file.exists()) {
                        file.delete();
                        scanDocListDetailsList.remove(pos-1);
                        notifyItemRemoved(pos);
                        notifyDataSetChanged();
                        pdfListEssentialsDBHandler.close();
                    }
                    else Toast.makeText(context, "File doesn't exist!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "Successfully Deleted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Error occurred in deleting the file! Please try later", Toast.LENGTH_SHORT).show();
                }
            });

            shareButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                File file = new File(context.getExternalFilesDir(null) + "/Summer/" + scanDocListDetailsList.get(position).getDocName() + ".pdf");
                Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID, file);
                intent.setType("application/pdf");
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                PackageManager pm = context.getPackageManager();
                if (intent.resolveActivity(pm) != null) {
                    context.startActivity(Intent.createChooser(intent, scanDocListDetailsList.get(position).getDocName() + ".pdf"));
                }
            });
        }
    }
}
