package com.raghu.summer.view;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.raghu.summer.R;
import com.raghu.summer.model.ScanDetails;

import java.util.ArrayList;

public class DragAndDropRecyclerViewAdapter extends RecyclerView.Adapter {

    private final ArrayList<ScanDetails> scanDetailsList;
    private DragAndDropRecyclerViewAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(DragAndDropRecyclerViewAdapter.OnItemClickListener listener){
        mListener = listener;
    }

    public DragAndDropRecyclerViewAdapter(ArrayList<ScanDetails> scanDetailsList) {
        this.scanDetailsList = scanDetailsList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.scanned_preview_cards,parent,false);
        return new ViewHolderOne(view, mListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DragAndDropRecyclerViewAdapter.ViewHolderOne viewHolderOne = (ViewHolderOne) holder;
        ScanDetails item = scanDetailsList.get(position);
        viewHolderOne.imageView.setImageBitmap(item.getImage());
        viewHolderOne.position.setText(Integer.toString(position+1));
    }

    @Override
    public int getItemCount() {
        return scanDetailsList.size();
    }

    public static class ViewHolderOne extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView position;

        public ViewHolderOne(View view, OnItemClickListener listener) {
            super(view);
            imageView = itemView.findViewById(R.id.preview_img);
            position = itemView.findViewById(R.id.pos);

            itemView.setOnClickListener(v -> {
                if(listener != null) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION) {
                        listener.onItemClick(pos);
                    }
                }
            });
        }
    }
}
