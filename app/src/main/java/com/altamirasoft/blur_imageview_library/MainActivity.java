package com.altamirasoft.blur_imageview_library;

import android.animation.ValueAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.altamirasoft.blur_imageview.BlurBackgroundView;
import com.altamirasoft.blur_imageview.BlurImageView;
import com.altamirasoft.blur_imageview.BlurLayout;

public class MainActivity extends AppCompatActivity {


    LinearLayout layout;

    BlurBackgroundView header;

    ScrollView scrollView;

    View parentPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout = (LinearLayout)findViewById(R.id.layout);
        header = (BlurBackgroundView)findViewById(R.id.header);

        parentPanel = findViewById(R.id.parentPanel);

        header.setTargetView(parentPanel);

        scrollView= (ScrollView)findViewById(R.id.scrollView);

        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                header.captureBlurImage(25f,1f);
                header.invalidate();
            }
        });
//
        findViewById(R.id.icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float from = header.getCurrentProgress();
                float target = 1f;
                if(from==0){
                    header.captureBlurImage(25f,0.1f);
                }
                else{
                    target = 0f;
                }

                final ValueAnimator anim = ValueAnimator.ofFloat(from,target);
                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = (float)anim.getAnimatedValue();
                        header.changeProgress(value);
                    }
                });
                anim.start();
            }
        });
//
//        image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                float from = image.getCurrentProgress();
//                float target = 1f;
//                if(from==0){
//                    image.captureBlurImage(25f,1f);
//                }
//                else{
//                    target = 0f;
//                }
//
//                final ValueAnimator anim = ValueAnimator.ofFloat(from,target);
//                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                    @Override
//                    public void onAnimationUpdate(ValueAnimator animation) {
//                        float value = (float)anim.getAnimatedValue();
//                        image.changeProgress(value);
//                    }
//                });
//                anim.start();
//            }
//        });
    }
}
