package com.example.camtester;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.audiofx.DynamicsProcessing;
import android.net.Uri;
import android.os.Environment;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import me.pqpo.smartcropperlib.view.CropImageView;
import me.pqpo.smartcropperlib.SmartCropper;
import me.pqpo.smartcropperlib.view.CropImageView;
import com.bumptech.glide.Glide;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.annotation.SuppressLint;

import org.w3c.dom.Text;

import java.util.Calendar;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

public class MainActivity extends AppCompatActivity {


    AutoCompleteTextView textureField;
    Button btnTake,galleryView;
    Button btnSelect;
    ImageView ivShow;
    Bitmap bitmap;
    File photoFile;
    String TextureData="";
    int i=0;

    String[] texturemetadata={"Chalkboard Paint","Colour Washing","Wooden Panelling","Dry-Brushing","Crackle Effect","Ragging","Sponging","Striae","Natural Wood","Linoleum","Tiles","Carpet","Stone","Linen","Silk","Wool","Cotton","Rayon","Acetate","Acrylic","Polyester","Nylon","Carpet","Tree Bark","Bare Metal","Gore","Cashmere","Carpet"};//{"wooden","flooring tiles","Tree Bark","Bare Metal","Gore","Cashmere"};
    ArrayList<String> texturemetadata_= new ArrayList<>();
    public boolean find(String [] s1, String s2 )
    {
        int i=0,n=s1.length;
        for ( i=0;i<n;i++){
            if(s1[i].equals(s2))return true;
        }
        return false;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnTake = (Button) findViewById(R.id.btn_take);
        btnSelect = (Button) findViewById(R.id.btn_select);
        ivShow = (ImageView) findViewById(R.id.iv_show);
        galleryView=(Button) findViewById(R.id.view_gallery);
        textureField= (AutoCompleteTextView) findViewById((R.id.textureTag));


        final Date currentTime = Calendar.getInstance().getTime();
        texturemetadata_.add("Tiles");
        texturemetadata_.add("Wooden panelling");
//        String.valueOf(currentTime)
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,android.R.layout.select_dialog_item, texturemetadata_);
        textureField.setAdapter(adapter);
        textureField.setThreshold(1);


        Log.d("MyMainactivityPrinting","Hi from Mainactivity, photofile = "+photoFile+", current i = "+ i);
        btnTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextureData= textureField.getText().toString();
                Log.d("MyError","Texturedata obtained= "+TextureData+" , list contains result= "+ texturemetadata_.contains(TextureData));
                if( !TextureData.isEmpty() && !(texturemetadata_.contains(TextureData)))
                  {
                     texturemetadata_.add(TextureData);
                     Log.d("MyError","Added element "+ TextureData);
                     adapter.add(TextureData);
                     textureField.setAdapter(adapter);
                     adapter.notifyDataSetChanged();
                  }
                if(TextureData.isEmpty())
                {
                    Toast.makeText(MainActivity.this, "Please Enter Texture MetaData !", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    photoFile = new File(getExternalFilesDir("img"), i+". " + TextureData + ". Src:  CamTester!" + String.valueOf(currentTime) + ".jpg");
                    startActivityForResult(CropActivity.getJumpIntent(MainActivity.this, false, photoFile), 100);
                    textureField.setText("");
                    i = i + 1;
                }
            }
        });

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextureData= textureField.getText().toString();
                Log.d("MyError","Texturedata obtained= "+TextureData+" , list contains result= "+ texturemetadata_.contains(TextureData));
                if( !TextureData.isEmpty() && !(texturemetadata_.contains(TextureData)))
                {
                    texturemetadata_.add(TextureData);
                    Log.d("MyError","Added element "+ TextureData + ". New size = "+ texturemetadata_.size());
                    adapter.add(TextureData);
                    textureField.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                if(TextureData.isEmpty())
                {
                    Toast.makeText(MainActivity.this, "Please Enter Texture MetaData !", Toast.LENGTH_SHORT).show();
                }
                else {
                    photoFile = new File(getExternalFilesDir("img"), i+". " + TextureData + ". Src:  CamTester!" + String.valueOf(currentTime) + ".jpg");
                    startActivityForResult(CropActivity.getJumpIntent(MainActivity.this, true, photoFile), 100);
                    textureField.setText("");
                    i = i + 1;
                }
            }
        });

        galleryView.setOnClickListener(new View.OnClickListener(){
//
              @Override
              public void onClick(View v) {


                      Intent intent = new Intent(MainActivity.this, MainActivityGallery.class);
                      intent.putExtra("image",photoFile);
                      startActivity(intent);


              }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == 100 && photoFile.exists()) {
             bitmap = BitmapFactory.decodeFile(photoFile.getPath());
            ivShow.setImageBitmap(bitmap);
        }
    }


}


