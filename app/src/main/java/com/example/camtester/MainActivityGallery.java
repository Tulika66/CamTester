package com.example.camtester;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import androidx.core.app.ActivityCompat;
import com.bumptech.glide.Glide;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class MainActivityGallery extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    GridView gridView;
    ArrayList<File> list;

    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_gallery);

        Bitmap bmp = null;
        String filename = getIntent().getStringExtra("image");



        checkPermissions();
        try {
           list = getImageFiles(Environment.getExternalStorageDirectory());// sd card
            


        }catch(Exception E){
            Log.d("MyTagGallery","list getexternalstorage error");
        }
       
//        iv=findViewById(R.id.iv1);
//        iv.setImageBitmap(bmp);



        gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(new GridAdapter());

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(getApplicationContext(), ViewImage.class).putExtra("img", list.get(i).toString()));
            }
        });

    }

    private void checkPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                //Requesting permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    class GridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.grid_item, viewGroup, false);
            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            //imageview.setImageURI(Uri.parse(getItem(i).toString()));

            //Loading image from below url into imageView
            Glide.with(getApplicationContext())
                    .load(getItem(i).toString())
                    .into(imageView);
            return view;
        }
    }

    ArrayList<File> getImageFiles(File root) {

        ArrayList <File> list = new ArrayList();
        File[] files = root.listFiles();
        try {

            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {

                    list.addAll(getImageFiles(files[i]));
                } else {

                    if ( (files[i].getName().contains("Src:  CamTester"))  && (files[i].getName().endsWith(".jpg") || files[i].getName().endsWith(".png") )) {
                        Log.d("MyTag","directory name = "+ files[i] + "name file = "+ files[i].getName());
                        list.add(files[i]);
                    }
                }
            }

        }catch(Exception E)
        {
            Log.d("ErrorE","Error found - " + E);
            E.printStackTrace();
        }
        return list;
    }


    @Override //Override from ActivityCompat.OnRequestPermissionsResultCallback Interface
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                    Log.d("permission granted ", "on RequestPermissions granted ");
                }
                return;
            }
        }
    }

    @org.jetbrains.annotations.NotNull
    private ActivityManager.MemoryInfo getMemoryInfo() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }

}
