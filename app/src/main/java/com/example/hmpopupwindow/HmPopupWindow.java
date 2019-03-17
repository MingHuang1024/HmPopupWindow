package com.example.hmpopupwindow;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

/**
 * 带动画及渐变模糊背景色的底部弹出框
 *
 * @author Huangming  2019/1/7
 */
public class HmPopupWindow extends PopupWindow {
    private Activity activity;
    private View contentView;
    private View backgroundView;
    private TranslateAnimation animation;

    /**
     * 构造方法
     *
     * @param activity
     */
    public HmPopupWindow(Activity activity) {
        super(activity);
        this.activity = activity;
        initView();
    }


    /**
     * 从底部弹出
     */
    public void show() {
        if (activity != null) {
            showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
            contentView.startAnimation(animation);

            // 背景色渐变
            backgroundView = new FrameLayout(activity);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            backgroundView.setLayoutParams(lp);
            ViewGroup rootView = activity.findViewById(android.R.id.content);
            rootView.addView(backgroundView);
            // 从完全透明开始到半透明的灰色
            int begin = 0x00000000;
            int end = 0x4d000000;

            ValueAnimator colorAnim = ObjectAnimator.ofInt(backgroundView, "backgroundColor",
                begin, end);
            colorAnim.setDuration(3000);
            colorAnim.setEvaluator(new ArgbEvaluator());
            Interpolator interpolator = new MyInterpolator();
            colorAnim.setInterpolator(interpolator);
            colorAnim.start();
        }
    }

    private void initView() {
        View contentView = LayoutInflater.from(activity).inflate(R.layout
            .popup_layout, null);

        this.contentView = contentView;
        setContentView(contentView);
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.MATCH_PARENT);

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                ViewGroup rootView = activity.findViewById(android.R.id.content);
                rootView.removeView(backgroundView);
            }
        });

        // 设置背景色
        setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        setFocusable(true);

        // 设置点击popupwindow外屏幕其它地方消失
        setOutsideTouchable(false);
        // 平移动画相对于手机屏幕的底部开始，X轴不变，Y轴从1变0
        animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation
            .RELATIVE_TO_PARENT, 0,
            Animation.RELATIVE_TO_PARENT, 1, Animation.RELATIVE_TO_PARENT, 0);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setDuration(3000);
        contentView.findViewById(R.id.tvOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        contentView.findViewById(R.id.tvCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    /**
     * 动画插值器
     *
     * @author Huangming 2019/3/17
     */
    private class MyInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float input) {
            // y=1-√1-x² 曲线
            float y = (float) (1.0 - Math.sqrt(1 - input * input));
            // System.out.println("x="+input+", y="+y);
            return y;
        }
    }
}
