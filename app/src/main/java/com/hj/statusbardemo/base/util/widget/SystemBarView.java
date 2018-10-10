package com.hj.statusbardemo.base.util.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 文件描述
 * 创建人：ganyf
 * 创建时间：2018/10/8
 */
public class SystemBarView extends View {
    public SystemBarView(Context context) {
        super(context);
    }

    public SystemBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            super.onMeasure(widthMeasureSpec, getStatusHeight(getContext()));
        } else {
            setMeasuredDimension(0, 0);
            setVisibility(GONE);
        }
    }

    public static int getStatusHeight(Context context) {
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;

    }
}
