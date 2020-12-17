package com.example.camtester;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import com.bumptech.glide.Glide;


public class ViewImage extends AppCompatActivity{

    ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        Intent intent = getIntent();
        String file =intent.getStringExtra("img");

        imageView= (ImageView) findViewById(R.id.imageView);
    }
}
