package com.joe.lambort_controller;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class ControlActivity extends AppCompatActivity {
    public static int screenWidth, screenHeight;
    public static int device_Code;

    private float direction;
    private SendThread mSendThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        int device_Code = intent.getIntExtra("Device",0);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            screenWidth = displayMetrics.widthPixels;
            screenHeight = displayMetrics.heightPixels;

            Joystick.joyStickImageOuter = BitmapFactory.decodeResource(this.getResources(),R.mipmap.joystick);
            Joystick.joyStickImageInner = BitmapFactory.decodeResource(this.getResources(),R.mipmap.inner);

            direction = Joystick.angle; //-pi -> +pi

            setContentView(new JoystickView(this));
            print(device_Code+"");
        }
        else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 横屏
        }
        mSendThread=SendThread.getSendThread();
    }

    public SendThread getSendThread() {
        return mSendThread;
    }

    public void print(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        Log.i("MainActivity", msg);
    }
}
