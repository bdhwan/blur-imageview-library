package com.altamirasoft.blur_imageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by bdhwan on 2016. 10. 25..
 */

public class BlurImageView extends ImageView {

    float progress = 0f;
    Bitmap bluredBitmap;
    Paint paint = new Paint();


    public BlurImageView(Context context) {
        this(context, null);
    }

    public BlurImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BlurImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void captureBlurImage(float blurRadius,float scalePercent){

        setDrawingCacheEnabled(true);
        setDrawingCacheQuality(DRAWING_CACHE_QUALITY_LOW);
        Bitmap bitmap = Bitmap.createBitmap(getDrawingCache());
        Bitmap temp = DrawUtil.resize(bitmap, (int) (bitmap.getWidth()*scalePercent),false);
        setDrawingCacheEnabled(false);

        bluredBitmap = BlurImageTool.blur(getContext(),temp,blurRadius);
        if(bluredBitmap!=null){
            bitmap.recycle();
            temp.recycle();
        }
        invalidate();
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
