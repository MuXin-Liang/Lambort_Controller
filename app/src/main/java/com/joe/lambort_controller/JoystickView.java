package com.joe.lambort_controller;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

public class JoystickView extends RelativeLayout implements Runnable {


    private Paint mPaint;
    private Joystick mJoystick = new Joystick();
    public static int touchAreaAlpha=0;
    private int backgroundColor = Color.WHITE;
    private SendThread mSendThread=SendThread.getSendThread();


    public JoystickView(final Context context) {
        super(context);
        mPaint = new Paint();
        setBackgroundColor(backgroundColor);
        OnTouchJoystick onTouchJoystick = new OnTouchJoystick(context, mJoystick);

        LayoutInflater inflater = LayoutInflater.from(context);
        View control_layout = inflater.inflate(R.layout.control_layout, null);

        Button bt_brake= (Button)control_layout.findViewById(R.id.bt_brake);
        bt_brake.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mSendThread.isbusy()){
                    ConnectLab.get().send(ControlActivity.device_Code,"00/1/0/0/");
                    mSendThread.setBusy();
                }

            }
        });
        ImageButton bt_up= (ImageButton)control_layout.findViewById(R.id.bt_up);
        bt_up.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mSendThread.isbusy()){
                    ConnectLab.get().send(ControlActivity.device_Code,"01/0/"+SendThread.getSpeed()+"/0/");
                    mSendThread.setBusy();
                }

            }
        });

        ImageButton bt_down= (ImageButton)control_layout.findViewById(R.id.bt_down);
        bt_down.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mSendThread.isbusy()){
                    ConnectLab.get().send(ControlActivity.device_Code,"02/0/"+SendThread.getSpeed()+"/0/");
                    mSendThread.setBusy();
                }

            }
        });
        ImageButton bt_left= (ImageButton)control_layout.findViewById(R.id.bt_left);
        bt_left.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mSendThread.isbusy()){
                    ConnectLab.get().send(ControlActivity.device_Code,"03/0/"+SendThread.getSpeed()+"/0/");
                    mSendThread.setBusy();
                }

            }
        });

        ImageButton bt_right= (ImageButton)control_layout.findViewById(R.id.bt_right);
        bt_right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mSendThread.isbusy()){
                    ConnectLab.get().send(ControlActivity.device_Code,"04/0/"+SendThread.getSpeed()+"/0/");
                    mSendThread.setBusy();
                }

            }
        });



        ThirdSwitchSeekBar sb_speed=(ThirdSwitchSeekBar)control_layout.findViewById(R.id.thirdSwitchSeekBar);
        ThirdSwitchSeekBar sb_motion=(ThirdSwitchSeekBar)control_layout.findViewById(R.id.thirdSwitchSeekBar2);

        sb_motion.setSeekTouchListenr(new ThirdSwitchSeekBar.SeekTouchListener() {
            @Override
            public void touchTop(SeekBar seekBar) {
                mSendThread.setSpeed(SendThread.low_speed);
            }

            @Override
            public void touchMiddle(SeekBar seekBar) {
                mSendThread.setSpeed(SendThread.middle_speed);
            }

            @Override
            public void touchEnd(SeekBar seekBar) {
                mSendThread.setSpeed(SendThread.high_speed);
            }
        });

        sb_speed.setSeekTouchListenr(new ThirdSwitchSeekBar.SeekTouchListener() {
            @Override
            public void touchTop(SeekBar seekBar) {
                if (mSendThread.isbusy()){
                    ConnectLab.get().send(ControlActivity.device_Code,"zhengxiang/");
                    mSendThread.setBusy();
                }
            }

            @Override
            public void touchMiddle(SeekBar seekBar) {
                if (!mSendThread.isbusy()){
                    ConnectLab.get().send(ControlActivity.device_Code,"shuzhang/");
                    mSendThread.setBusy();
                }
            }

            @Override
            public void touchEnd(SeekBar seekBar) {
                if (!mSendThread.isbusy()){
                    ConnectLab.get().send(ControlActivity.device_Code,"fanxiang/");
                    mSendThread.setBusy();
                }
            }
        });

        addView(control_layout,ControlActivity.screenWidth,ControlActivity.screenHeight);
        control_layout.setX(0);
        control_layout.setY(0);
//        addView(onTouchJoystick,ControlActivity.screenWidth/3,ControlActivity.screenHeight);
//        onTouchJoystick.setX(0);
//        onTouchJoystick.setY(0);

        new Thread(this).start();
    }

    @Override
    public void run() {
        while(true){
            try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
            postInvalidate();
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mJoystick.onDraw(canvas, mPaint);


    }
    public void print(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        Log.i("MainActivity", msg);
    }


}
