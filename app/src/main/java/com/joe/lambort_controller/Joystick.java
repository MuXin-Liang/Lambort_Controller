package com.joe.lambort_controller;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import static java.lang.StrictMath.abs;


public class Joystick {
    private RectF drawingField = new RectF();

    public static float angle;

    private float xSmall, ySmall;
    private float xLarge, yLarge;
    private boolean myJoystickPressed;

    private boolean smallInLarge;

    public static Bitmap joyStickImageInner;
    public static Bitmap joyStickImageOuter;


    private final int screenWidth = ControlActivity.screenWidth;
    private final int screenHeight = ControlActivity.screenHeight;

    private final float screenAreaRatio = (float) (Math.sqrt(screenWidth * screenHeight) / Math.sqrt(1920 * 1080));
    private final float rLarge =  480 * 0.5f * screenAreaRatio;
    private final float rSmall =  300 * 0.5f * screenAreaRatio;


    public void onJoystickPressed(float xInput, float yInput){
        if (xInput < rLarge)
            xLarge = rLarge;
        else
            xLarge = xInput;
        if (screenHeight - yInput < rLarge)
            yLarge = screenHeight - rLarge;
        else
            yLarge = yInput;
        myJoystickPressed = true;

    }

    public void onJoystickReleased(){
        myJoystickPressed = false;
    }

    public float getAngle(float xInput, float yInput){
        double angle;
        double k;
        if (yLarge == yInput){
            if (xLarge > xInput)
                angle = -Math.PI/2;
            else{
                angle = Math.PI/2;
            }
        }
        else{
            k = (xLarge - xInput)/(yLarge - yInput);
            if (yLarge > yInput){
                angle = Math.atan(k) + Math.PI;
            }
            else{
                angle = Math.atan(k);
            }
            if(angle>Math.PI)
                angle-=Math.PI*2;
            else if(angle<-Math.PI)
                angle+=Math.PI*2;
        }
        return (float) angle;
    }



    public boolean isSmallInLarge(float xInput, float yInput) {
        double r = Math.sqrt((xLarge - xInput) * (xLarge - xInput) + (yLarge - yInput) * (yLarge - yInput));
        if (r < rLarge*0.7f)
            return true;
        else
            return false;
    }




    public void onDraw(Canvas canvas, Paint mPaint){
        if(myJoystickPressed){
            drawingField.left = xLarge - rLarge;
            drawingField.top = yLarge - rLarge;
            drawingField.right = xLarge + rLarge;
            drawingField.bottom = yLarge + rLarge;
            canvas.drawBitmap(joyStickImageInner, null, drawingField, mPaint);
            drawingField.left = xSmall - rSmall;
            drawingField.top = ySmall - rSmall;
            drawingField.right = xSmall + rSmall;
            drawingField.bottom = ySmall + rSmall;
            canvas.drawBitmap(joyStickImageInner, null, drawingField, mPaint);
        }
    }

    public void afterJoystickPressed(float xInput, float yInput){
        angle = getAngle(xInput,yInput);
        if (!SendThread.getSendThread().isbusy()){
            if((xLarge-xInput)<-250 && (yLarge-yInput)>-60 && (yLarge-yInput)<60){
                //right
                Log.e("Now","right");
            }
            else if((xLarge-xInput)>250 && (yLarge-yInput)>-60 && (yLarge-yInput)<60){
                //left
                Log.e("Now","left");
            }
            else if((yLarge-yInput)>250 && (xLarge-xInput)>-60 && (xLarge-xInput)<60){
                //up
                Log.e("Now","up");
            }
            else if((yLarge-yInput)<-250 && (xLarge-xInput)>-60 && (xLarge-xInput)<60){
                //down`
                Log.e("Now","down");
            }
            SendThread.getSendThread().setBusy();
        }
        smallInLarge = isSmallInLarge(xInput,yInput);
        if (!smallInLarge){
            xInput = (float)(xLarge + Math.sin(angle)*rLarge*0.7f);
            yInput = (float)(yLarge + Math.cos(angle)*rLarge*0.7f);
        }
        xSmall = xInput;
        ySmall = yInput;
    }

    public Joystick(){

    }











}
