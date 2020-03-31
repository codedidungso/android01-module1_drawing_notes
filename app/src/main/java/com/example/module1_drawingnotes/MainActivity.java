package com.example.module1_drawingnotes;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {

    int requestCodeWriteStorage = 1001;

    private GridView gridView;
    private ImageAdapter imageAdapter;
    private LinearLayout llTextNoImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fbAdd = findViewById(R.id.fb_add);

        fbAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DrawActivity.class);
                startActivity(intent);
            }
        });

        setupPermission();

        gridView = findViewById(R.id.gv_images);
        imageAdapter = new ImageAdapter();
        gridView.setOnItemLongClickListener(this);
        gridView.setAdapter(imageAdapter);

        llTextNoImage = findViewById(R.id.ll_text_no_image);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkIfShowTextNoImageOrNot();
        imageAdapter.notifyDataSetChanged();
    }

    public void checkIfShowTextNoImageOrNot() {
        if (ImageUtils.getListImage().size() == 0) {
            llTextNoImage.setVisibility(View.VISIBLE);
        } else {
            llTextNoImage.setVisibility(View.GONE);
        }
    }

    private void setupPermission() {
        String[] permissions = new String[1];
        permissions[0] = Manifest.permission.WRITE_EXTERNAL_STORAGE;

        //new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(permissions, requestCodeWriteStorage);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == requestCodeWriteStorage) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Warning!")
                        .setMessage("Without permission you can not use this app. Do you want to grant permission?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setupPermission();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.this.finish();
                            }
                        })
                        .show();
            }
        }
    }

    @Override
    public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int position, long l) {
        new AlertDialog.Builder(this)
                .setTitle(ImageUtils.getListImage().get(position).getName())
                .setMessage("Do you want to delete this image?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (ImageUtils.deleteImage(position)) {
                            checkIfShowTextNoImageOrNot();
                            imageAdapter.notifyDataSetChanged();
                            Toast.makeText(MainActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Cannot delete this file", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()
                .show();
        return false;
    }
}
