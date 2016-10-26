package com.altamirasoft.blur_imageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by bdhwan on 2016. 10. 25..
 */

public class BlurLayout extends RelativeLayout {


    float progress = 0f;
    Bitmap bluredBitmap;
    Paint paint = new Paint();


    public BlurLayout(Context context) {
        this(context, null);
    }

    public BlurLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BlurLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }


    public Bitmap captureBlurImage(float blurRadius,float scalePercent){

        this.setDrawingCacheEnabled(true);
        this.setDrawingCacheQuality(DRAWING_CACHE_QUALITY_LOW);
        Bitmap bitmap = DrawUtil.loadBitmapFromView(this);
        Bitmap temp = DrawUtil.resize(bitmap, (int) (bitmap.getWidth()*scalePercent),false);
        this.setDrawingCacheEnabled(false);

        bluredBitmap = BlurImageTool.blur(getContext(),temp,blurRadius);
        if(bluredBitmap!=null){
            bitmap.recycle();
            temp.recycle();
        }
        invalidate();
        return bluredBitmap;
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

    @Override
    protected void dispatchDraw(Canvas canvas) {

        int count = canvas.save();
        super.dispatchDraw(canvas);
        if(bluredBitmap!=null&&progress!=0){
            paint.setAlpha((int) (progress*255));
            src.right = bluredBitmap.getWidth();
            src.bottom = bluredBitmap.getHeight();
            dst.right = getWidth();
            dst.bottom = getHeight();
            canvas.drawBitmap(bluredBitmap,src,dst,paint);
        }
        canvas.restoreToCount(count);
    }



}
