package com.altamirasoft.blur_imageview;

import android.content.res.Resources;

/**
 * Created by bdhwan on 15. 3. 21..
 */
public class DPUtil {

    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px)
    {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }


}
