package com.altamirasoft.blur_imageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;

/**
 * Created by bdhwan on 2016. 10. 25..
 */

public class BlurImageTool {

    private static final float BLUR_RADIUS = 25f;





    public static Bitmap blur(Context context, Bitmap image) {
        return blur(context,image,BLUR_RADIUS);
    }



    public static Bitmap blur(Context context, Bitmap image, float radius) {
        if (null == image) return null;

        Bitmap outputBitmap = Bitmap.createBitmap(image);
        final RenderScript renderScript = RenderScript.create(context);
        Allocation tmpIn = Allocation.createFromBitmap(renderScript, image);
        Allocation tmpOut = Allocation.createFromBitmap(renderScript, outputBitmap);

        //Intrinsic Gausian blur filter
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));

        float temp = radius;
        if(temp>BLUR_RADIUS){
            temp = BLUR_RADIUS;
        }
        theIntrinsic.setRadius(temp);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);
        return outputBitmap;
    }


}
