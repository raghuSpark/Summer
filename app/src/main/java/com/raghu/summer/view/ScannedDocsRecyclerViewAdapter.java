package com.raghu.summer.view;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.raghu.summer.R;
import com.raghu.summer.ScanListActivity;
import com.raghu.summer.database.PDFListEssentialsDBHandler;
import com.raghu.summer.model.ScanDocListDetails;

import java.util.List;

public class ScannedDocsRecyclerViewAdapter extends RecyclerView.Adapter {

    private final List<ScanDocListDetails> scanDocListDetailsList;
    private ScannedDocsRecyclerViewAdapter.OnItemClickListener mListener;
    Context context;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(ScannedDocsRecyclerViewAdapter.OnItemClickListener listener){
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
        View view = layoutInflater.inflate(R.layout.scan_cards,parent,false);
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

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView previewImage;
        TextView docName, dateAdded;

        LinearLayout linearLayout1, linearLayout2;

        ImageButton openMenuButton, closeMenuButton, deleteButton, shareButton, EditButton, forwardButton;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            itemView.setOnClickListener(v -> {
                if(listener != null) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION) {
                        listener.onItemClick(pos);
                    }
                }
            });

            linearLayout1 = itemView.findViewById(R.id.scan_card_details);
            linearLayout2 = itemView.findViewById(R.id.scan_card_extras);

            previewImage = itemView.findViewById(R.id.scan_preview);
            docName = itemView.findViewById(R.id.scan_title);
            dateAdded = itemView.findViewById(R.id.scan_timeStamp);
            openMenuButton = itemView.findViewById(R.id.scan_menu);
            closeMenuButton = itemView.findViewById(R.id.close_menu);
            deleteButton = itemView.findViewById(R.id.delete_doc);
            shareButton = itemView.findViewById(R.id.share_doc);
            EditButton = itemView.findViewById(R.id.edit_doc);
            forwardButton = itemView.findViewById(R.id.forward_doc);

            openMenuButton.setOnClickListener(v -> {
                linearLayout1.setVisibility(View.GONE);
                linearLayout2.setVisibility(View.VISIBLE);
            });

            closeMenuButton.setOnClickListener(v -> {
                linearLayout1.setVisibility(View.VISIBLE);
                linearLayout2.setVisibility(View.GONE);
            });

            deleteButton.setOnClickListener(v -> {
                Toast.makeText(context, "I can't bro!!", Toast.LENGTH_SHORT).show();
//                PDFListEssentialsDBHandler pdfListEssentialsDBHandler = new PDFListEssentialsDBHandler(context);
//                pdfListEssentialsDBHandler.deleteAPDF(scanDocListDetailsList.get(getAdapterPosition()).getDocName());
//                notifyDataSetChanged();
//                notifyItemRemoved(getAdapterPosition());
            });

            shareButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                Log.d("TAG", "MyViewHolder: "+position);
                Log.d("TAG", "MyViewHolder: "+scanDocListDetailsList.size());
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, scanDocListDetailsList.get(position).getDocName());
                intent.setType("text/plain");
                context.startActivity(Intent.createChooser(intent, "Send Info"));
            });
        }
    }
}
