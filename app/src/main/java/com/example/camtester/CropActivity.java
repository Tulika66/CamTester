package com.example.camtester;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;


import com.example.camtester.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.pqpo.smartcropperlib.SmartCropper;
import me.pqpo.smartcropperlib.view.CropImageView;
import pub.devrel.easypermissions.EasyPermissions;



public class CropActivity  extends AppCompatActivity implements  EasyPermissions.PermissionCallbacks{

    private static final String EXTRA_FROM_ALBUM = "extra_from_album";
    private static final String EXTRA_CROPPED_FILE = "extra_cropped_file";
    private static final int REQUEST_CODE_TAKE_PHOTO = 100;
    private static final int REQUEST_CODE_SELECT_ALBUM = 200;

    public ArrayList<File> listFiles = new ArrayList<File>();

    CropImageView ivCrop;
    Button btnCancel;
    Button btnOk;

    boolean mFromAlbum;
    File mCroppedFile;

    File tempFile;

    public static Intent getJumpIntent(Context context, boolean fromAlbum, File croppedFile) {
        Intent intent = new Intent(context, CropActivity.class);
        intent.putExtra(EXTRA_FROM_ALBUM, fromAlbum);
        intent.putExtra(EXTRA_CROPPED_FILE, croppedFile);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        ivCrop = (CropImageView) findViewById(R.id.iv_crop);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnOk = (Button) findViewById(R.id.btn_ok);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) { // finally cropped after this
                if (ivCrop.canRightCrop()) {
                    Bitmap crop = ivCrop.crop();
                    if (crop != null) {

                        //image saved, mcroppedfile=file uri
                        saveImage(crop, mCroppedFile);
                        setResult(RESULT_OK);
                        Toast.makeText(CropActivity.this, "Image saved successfully, with above details !", Toast.LENGTH_SHORT).show();
                        listFiles.add(mCroppedFile);
                        Log.d("MyPrinting","My file saved, current files tot = "+ listFiles.size());
                    } else {
                        setResult(RESULT_CANCELED);
                    }
                    finish();
                } else {
                    Toast.makeText(CropActivity.this, "cannot crop correctly", Toast.LENGTH_SHORT).show();
                }
            }
        });


        mFromAlbum = getIntent().getBooleanExtra(EXTRA_FROM_ALBUM, true);
        mCroppedFile = (File) getIntent().getSerializableExtra(EXTRA_CROPPED_FILE);
        if (mCroppedFile == null) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        tempFile = new File(getExternalFilesDir("img"), "temp.jpg");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            EasyPermissions.requestPermissions(
                    CropActivity.this,
                    "request for access",
                    0,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA);
        }else{
            selectPhoto();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Some permissions have been granted
        // ...
        selectPhoto();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Some permissions have been denied
        // ...
    }

    private void selectPhoto() {
        if (mFromAlbum) {
            try {
                Intent selectIntent = new Intent(Intent.ACTION_PICK);
                selectIntent.setType("image/*");
                if (selectIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(selectIntent, REQUEST_CODE_SELECT_ALBUM);
                }
            }catch(Exception e){

            }
        } else {
            try {


                Intent startCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri uri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri = FileProvider.getUriForFile(this, "com.example.camtester.fileProvider", tempFile);
                } else {
                    uri = Uri.fromFile(tempFile);
                }
                startCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                if (startCameraIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(startCameraIntent, REQUEST_CODE_TAKE_PHOTO);
                }
            }catch(Exception e){

            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }
        Bitmap selectedBitmap = null;
        if (requestCode == REQUEST_CODE_TAKE_PHOTO && tempFile.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(tempFile.getPath(), options);
            options.inJustDecodeBounds = false;
            options.inSampleSize = calculateSampleSize(options);
            selectedBitmap = BitmapFactory.decodeFile(tempFile.getPath(), options);
        } else if (requestCode == REQUEST_CODE_SELECT_ALBUM && data != null && data.getData() != null) {
            ContentResolver cr = getContentResolver();
            Uri bmpUri = data.getData();
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(cr.openInputStream(bmpUri), new Rect(), options);
                options.inJustDecodeBounds = false;
                options.inSampleSize = calculateSampleSize(options);
                selectedBitmap = BitmapFactory.decodeStream(cr.openInputStream(bmpUri), new Rect(), options);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (selectedBitmap != null) {
            ivCrop.setImageToCrop(selectedBitmap);
            // original image asked to crop
        }
    }


    private void saveImage(Bitmap bitmap, File saveFile) {
        try {
            FileOutputStream fos = new FileOutputStream(saveFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            Log.d("MYPRINTING","My file = "+saveFile);

            fos.flush();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int calculateSampleSize(BitmapFactory.Options options) {
        int outHeight = options.outHeight;
        int outWidth = options.outWidth;
        int sampleSize = 1;
        int destHeight = 1000;
        int destWidth = 1000;
        if (outHeight > destHeight || outWidth > destHeight) {
            if (outHeight > outWidth) {
                sampleSize = outHeight / destHeight;
            } else {
                sampleSize = outWidth / destWidth;
            }
        }
        if (sampleSize < 1) {
            sampleSize = 1;
        }
        return sampleSize;
    }

}
