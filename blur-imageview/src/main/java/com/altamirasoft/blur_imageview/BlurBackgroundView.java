package com.altamirasoft.blur_imageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by bdhwan on 2016. 10. 25..
 */

public class BlurBackgroundView extends ImageView {

    float progress = 0f;
    Bitmap bluredBitmap;
    Paint paint = new Paint();

    float scalePercent;
    float blurRadius;


    public BlurBackgroundView(Context context) {
        this(context, null);
    }

    public BlurBackgroundView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BlurBackgroundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void captureBlurImage(float blurRadius,float scalePercent){

        this.scalePercent = scalePercent;
        this.blurRadius = blurRadius;
        if(targetView!=null){

            setDrawingCacheEnabled(true);

            setDrawingCacheQuality(DRAWING_CACHE_QUALITY_LOW);
            buildDrawingCache();
            Bitmap bitmap =DrawUtil.loadBitmapFromView(targetView);
            Bitmap temp = DrawUtil.resize(bitmap, (int) (bitmap.getHeight()*scalePercent),false);
            setDrawingCacheEnabled(false);

            bluredBitmap = BlurImageTool.blur(getContext(),temp,blurRadius);
            if(bluredBitmap!=null){
                bitmap.recycle();
                temp.recycle();
            }
            invalidate();

        }

    }

    public void changeProgress(float progress){
        this.progress = progress;
        Log.d("log","progress ="+progress);
        invalidate();
    }


    public float getCurrentProgress(){

        return progress;
    }

    Rect src = new Rect();
    Rect dst = new Rect();
    int[] targetViewLocation = new int[2];
    int[] myLocation = new int[2];


    @Override
    protected void onDraw(Canvas canvas) {

        int count = canvas.save();

        if(bluredBitmap!=null&&progress!=0){
            paint.setAlpha((int) (progress*255));

            getLocationInWindow(myLocation);
            targetView.getLocationInWindow(targetViewLocation);
            int differY = myLocation[1] - targetViewLocation[1];


            int differX = myLocation[0] - targetViewLocation[0];



            int top = (int) (differY*scalePercent);
            int left = (int) (differX*scalePercent);

            src.left = left;
            src.top = top;

            if(differX<0){
                src.left = 0;
            }

            if(differY<0){
                src.top = 0;
            }

            src.right = (int) (getWidth()*scalePercent+left);
            src.bottom = (int) (getHeight()*scalePercent+top);

            dst.left = 0;
            dst.top = 0;

            if(differX<0){
                dst.left = -differX;
            }
            if(differY<0){
                dst.top = -differY;
            }
            dst.right = getWidth();
            dst.bottom = getHeight();


            canvas.drawBitmap(bluredBitmap,src,dst,paint);


        }
        else{
            super.onDraw(canvas);
        }
        canvas.restoreToCount(count);
    }

    View targetView;

    public void setTargetView(View view){
        this.targetView = view;


    }

    public void setBluredBitmap(Bitmap image){
        bluredBitmap = image;
        invalidate();
    }



}
