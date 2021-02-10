package com.raghu.summer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.raghu.summer.database.scannedImagesDBHandler;
import com.raghu.summer.model.ScanDetails;
import com.raghu.summer.view.DragAndDropRecyclerViewAdapter;

import java.io.IOException;
import java.util.ArrayList;

public class ScanListPreviewActivity extends AppCompatActivity {

    private static final int GALLERY_CODE = 1;

    private AlertDialog alertDialog;

    private RecyclerView recyclerView;
    private Button save_button;
    private ImageButton gallery_import;
    private ImageButton scan_img;
    private TextView delete_and_done_button;
    private ImageButton discard_all_btn;

    private DragAndDropRecyclerViewAdapter adapter;

    private final ArrayList<ScanDetails> scan_list = new ArrayList<>();

    private scannedImagesDBHandler scannedImagesDBHandler;

    private boolean delete_activated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_preview_scan);

        discard_all_btn = findViewById(R.id.discard_all);
        delete_and_done_button = findViewById(R.id.delete_img_and_done);
        scan_img = findViewById(R.id.scan_btn);
        gallery_import = findViewById(R.id.gallery_import_btn);
        save_button = findViewById(R.id.save_and_make_pdf);
        recyclerView = findViewById(R.id.recycler_view);

        if(scan_list.size() == 0) delete_and_done_button.setVisibility(View.GONE);

        init();
        generateItem();

        scannedImagesDBHandler = new scannedImagesDBHandler(this);

        scan_list.clear();
        scannedImagesDBHandler.deleteAllImages();

        gallery_import.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
            startActivityForResult(galleryIntent, GALLERY_CODE);
        });

        discard_all_btn.setOnClickListener(v -> {
            if (scan_list.size()!=0) {
                discard_all_images();
            } else {
                startActivity(new Intent(ScanListPreviewActivity.this,ScanListActivity.class));
                finish();
            }
        });

        delete_and_done_button.setOnClickListener(v -> {
            deleteClicked();
        });

        adapter.setOnItemClickListener(position -> {
            if(delete_activated) {
                scan_list.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position,scan_list.size()-position);
                if(scan_list.size() == 0) delete_and_done_button.setVisibility(View.GONE);
            } else {
                popUpImage(position);
            }
        });

        save_button.setOnClickListener(v -> {
            if (delete_activated) {
                Toast.makeText(this, "Delete Function is still running!", Toast.LENGTH_SHORT).show();
            } else {
                if (scan_list.size() != 0) {
                    Intent final_intent = new Intent(ScanListPreviewActivity.this, ScanFinalActivity.class);
                    final_intent.putExtra("no_of_pages", scan_list.size());
                    saveToDataBase();
                    startActivity(final_intent);
                } else {
                    Toast.makeText(this, "No images selected!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void popUpImage(int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ScanListPreviewActivity.this);
        LayoutInflater inflater = LayoutInflater.from(ScanListPreviewActivity.this);
        View view = inflater.inflate(R.layout.pop_up_preview,null);

        ImageView scanned_img = view.findViewById(R.id.popUpPreview);
        ImageView close_popup = view.findViewById(R.id.close_popUp);

        scanned_img.setImageBitmap(scan_list.get(position).getImage());
        close_popup.setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void deleteClicked() {
        if(!delete_activated) {
            delete_and_done_button.setText("Done");
            delete_and_done_button.setBackgroundResource(R.drawable.scan_rect_2);
            delete_and_done_button.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
            delete_activated = true;
        } else {
            delete_and_done_button.setText("");
            delete_and_done_button.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_delete,0);
            delete_and_done_button.setBackgroundResource(0);
            delete_activated = false;
        }
    }

    private void saveToDataBase() {
        for (int i=0;i<scan_list.size();i++) {
            scannedImagesDBHandler.storeImage(new ScanDetails(scan_list.get(i).getPos(), scan_list.get(i).getImage()));
        }
    }

    @Override
    public void onBackPressed() {
        if (scan_list.size()!=0) {
            discard_all_images();
        } else {
            startActivity(new Intent(ScanListPreviewActivity.this,ScanListActivity.class));
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_CODE && resultCode==RESULT_OK) {
            assert data != null;
            if (data.getClipData()!=null) {
                ClipData clipData = data.getClipData();
                for(int i=0;i<clipData.getItemCount();i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    Uri imageUri = item.getUri();
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    scan_list.add(new ScanDetails(bitmap));
                }
                if(scan_list.size() != 0) delete_and_done_button.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            } else if (data.getData()!=null) {
                Uri imageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);
                    if(scan_list.size() != 0) delete_and_done_button.setVisibility(View.VISIBLE);
                    scan_list.add(new ScanDetails(bitmap));
                    adapter.notifyDataSetChanged();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void generateItem() {
//        scan_list.add(new ScanDetails(BitmapFactory.decodeResource(getResources(),R.drawable.photo)));
//        scan_list.add(new ScanDetails(BitmapFactory.decodeResource(getResources(),R.drawable.photo1)));
//        scan_list.add(new ScanDetails(BitmapFactory.decodeResource(getResources(),R.drawable.photo2)));
//        scan_list.add(new ScanDetails(BitmapFactory.decodeResource(getResources(),R.drawable.photo3)));

        //Set Adapter
        adapter=new DragAndDropRecyclerViewAdapter(scan_list);
        recyclerView.setAdapter(adapter);
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END,0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int drag_pos = viewHolder.getAdapterPosition();
                int target_pos = target.getAdapterPosition();
                ScanDetails details = scan_list.get(drag_pos);
                scan_list.remove(drag_pos);
                scan_list.add(target_pos,details);

                if(drag_pos < target_pos){
                    adapter.notifyItemRangeChanged(drag_pos,target_pos-drag_pos+1);
                }else{
                    adapter.notifyItemRangeChanged(target_pos,drag_pos-target_pos+1);
                }
                adapter.notifyItemMoved(drag_pos,target_pos);

                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });

        helper.attachToRecyclerView(recyclerView);
    }

    private void init() {
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void discard_all_images() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ScanListPreviewActivity.this);
        LayoutInflater inflater = LayoutInflater.from(ScanListPreviewActivity.this);
        View view = inflater.inflate(R.layout.discard_confirm_popup,null);

        Button noButton = view.findViewById(R.id.no_button);
        Button yesButton = view.findViewById(R.id.yes_button);

        noButton.setOnClickListener(v -> alertDialog.dismiss());

        yesButton.setOnClickListener(v -> {
            scan_list.clear();
            Toast.makeText(ScanListPreviewActivity.this, "Discarded all images", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ScanListPreviewActivity.this,ScanListActivity.class));
            finish();
            alertDialog.dismiss();
        });

        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();
    }
}