package com.example.camtester;

import android.app.Application;
import me.pqpo.smartcropperlib.SmartCropper;

public class App extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        SmartCropper.buildImageDetector(this);
    }

}
