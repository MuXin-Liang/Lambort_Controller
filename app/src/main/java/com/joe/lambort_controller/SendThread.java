package com.joe.lambort_controller;

import android.content.Context;
import android.widget.Toast;

import java.security.cert.TrustAnchor;

public class SendThread extends Thread {
    private boolean mbusy;
    private static SendThread mSendThread;
    private static int mspeed;

    public static final int low_speed=1;
    public static final int middle_speed=2;
    public static final int high_speed=3;

    private SendThread(){
        mbusy=false;
    }

    public static SendThread getSendThread(){
        if(mSendThread==null){
            mSendThread=new SendThread();
            mSendThread.start();
            return mSendThread;
        }
        else {
            return mSendThread;
        }
    }

    public boolean isbusy(){
        return mbusy;
    }
    public void setBusy(){
        mbusy=true;
    }

    public void setSpeed(int speed){
        mspeed=speed;
    }

    public static int getSpeed(){
        return mspeed;
    }

    @Override
    public void run() {
        super.run();
        while (true){
            if(mbusy){
                try {
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                mbusy=false;
            }
        }
    }
}
