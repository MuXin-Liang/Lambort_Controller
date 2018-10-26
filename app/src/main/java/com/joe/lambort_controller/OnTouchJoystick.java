package com.joe.lambort_controller;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class OnTouchJoystick extends View {

    private Joystick mJoystick;

    public OnTouchJoystick(Context context, Joystick joystick) {
        super(context);
        this.mJoystick = joystick;
        setBackgroundColor(Color.WHITE); //This is for touch area's bg color, set touchAreaAlpha not zero to see the effect
        getBackground().setAlpha(JoystickView.touchAreaAlpha);

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final float xInput = event.getX() + getX();
                final float yInput = event.getY() + getY();

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mJoystick.onJoystickPressed(xInput, yInput);
                }
                mJoystick.afterJoystickPressed(xInput,yInput);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mJoystick.onJoystickReleased();
                }

                return true;
            }
        });

    }
}
