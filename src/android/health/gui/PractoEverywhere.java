package com.example.mohamedrafiq.fitnessapp;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.content.Intent;
import android.widget.ImageView;

import java.net.URI;


public class PractoEverywhere extends ImageView implements View.OnClickListener {

    private static final int PRESSED_COLOR_LIGHTUP = 255 / 30;
    private static final int PRESSED_RING_ALPHA = 75;
    private static final int DEFAULT_PRESSED_RING_WIDTH_DIP = 6;
    private static final int ANIMATION_TIME_ID = android.R.integer.config_shortAnimTime;

    private int centerY;
    private int centerX;
    private int outerRadius;
    private int pressedRingRadius;

    private Paint circlePaint;
    private Paint focusPaint;

    private float animationProgress;

    private int pressedRingWidth;
    private int defaultColor = Color.LTGRAY;
    private int pressedColor;
    private ObjectAnimator pressedAnimator;
    private Context mContext;
    public static SPECIALITY mSpeciality;
    public enum SPECIALITY {
        Dentist,
        Dietitian,
        Cardiologist,
        Spa,
        Psychologist,
        Gynecologist
    }

    public PractoEverywhere(Context context) {
        super(context);
        init(context, null);
    }

    public PractoEverywhere(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PractoEverywhere(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public void setSpeciality(SPECIALITY speciality){
        mSpeciality = speciality;
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);

        if (circlePaint != null) {
            circlePaint.setColor(pressed ? pressedColor : defaultColor);
        }

        if (pressed) {
            showPressedRing();
        } else {
            hidePressedRing();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(centerX, centerY, pressedRingRadius + animationProgress, focusPaint);
        canvas.drawCircle(centerX, centerY, outerRadius - pressedRingWidth, circlePaint);
        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;
        outerRadius = Math.min(w, h) / 2;
        pressedRingRadius = outerRadius - pressedRingWidth - pressedRingWidth / 2;
    }

    public float getAnimationProgress() {
        return animationProgress;
    }

    public void setAnimationProgress(float animationProgress) {
        this.animationProgress = animationProgress;
        this.invalidate();
    }

    public void setColor(int color) {
//        setVisibility(View.GONE);
        this.defaultColor = color;
        this.pressedColor = getHighlightColor(color, PRESSED_COLOR_LIGHTUP);

        circlePaint.setColor(defaultColor);
        focusPaint.setColor(defaultColor);
        focusPaint.setAlpha(PRESSED_RING_ALPHA);

        this.invalidate();
    }

    private void hidePressedRing() {
        pressedAnimator.setFloatValues(pressedRingWidth, 0f);
        pressedAnimator.start();
    }

    private void showPressedRing() {
        pressedAnimator.setFloatValues(animationProgress, pressedRingWidth);
        pressedAnimator.start();
    }

    private void init(Context context, AttributeSet attrs) {
        this.setFocusable(true);
        this.setScaleType(ScaleType.CENTER_INSIDE);
        setClickable(true);
        setOnClickListener(this);
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.FILL);

        focusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        focusPaint.setStyle(Paint.Style.STROKE);

        pressedRingWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_PRESSED_RING_WIDTH_DIP, getResources()
                .getDisplayMetrics());

//        int color = Color.LTGRAY;
        setColor(Color.parseColor("#02a6d8"));

        focusPaint.setStrokeWidth(pressedRingWidth);
        final int pressedAnimationTime = getResources().getInteger(ANIMATION_TIME_ID);
        pressedAnimator = ObjectAnimator.ofFloat(this, "animationProgress", 0f, 0f);
        pressedAnimator.setDuration(pressedAnimationTime);
        mContext = context;
    }

    private int getHighlightColor(int color, int amount) {
        return Color.argb(Math.min(255, Color.alpha(color)), Math.min(255, Color.red(color) + amount),
                Math.min(255, Color.green(color) + amount), Math.min(255, Color.blue(color) + amount));
    }

    @Override
    public void onClick(View v) {
        try {
            Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage("com.practo.fabric");
            launchIntent.setAction(Intent.ACTION_VIEW);
            if (mSpeciality == SPECIALITY.Dentist){
                launchIntent.setData(Uri.parse("https://www.practo.com/bangalore/dentist"));
            }else if (mSpeciality == SPECIALITY.Cardiologist){
                launchIntent.setData(Uri.parse("https://www.practo.com/bangalore/cardiologist"));
            }else if (mSpeciality == SPECIALITY.Gynecologist){
                launchIntent.setData(Uri.parse("https://www.practo.com/bangalore/gynecologist-obstetrician"));
            }else if (mSpeciality == SPECIALITY.Dietitian){
                launchIntent.setData(Uri.parse("https://www.practo.com/bangalore/dietitian-nutritionist"));
            }else if (mSpeciality == SPECIALITY.Psychologist){
                launchIntent.setData(Uri.parse("https://www.practo.com/bangalore/psychologist"));
            }else if (mSpeciality == SPECIALITY.Spa){
                launchIntent.setData(Uri.parse("https://www.practo.com/bangalore/dentist"));
            }else{
                launchIntent.setData(Uri.parse("https://www.practo.com/bangalore"));
            }
            mContext.startActivity(launchIntent);
        }catch (Exception e){

        }

    }


}