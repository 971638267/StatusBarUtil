package com.hj.statusbardemo.base.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;


import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 文件描述
 * 创建人：ganyf
 * 创建时间：2018/10/8
 */
public class StatusBarUtil {
    private static final String PLACE_VIEW = "viewPlace";

    private static void init(@NotNull Activity activity, boolean applyNav) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (applyNav) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                window.setNavigationBarColor(Color.TRANSPARENT);
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (applyNav) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            }
        }

    }

    public static void setStatusBar(@NonNull Activity activity, @ColorInt int statusColor, @ColorInt int navColor, boolean isStatusLight, boolean isNavLight, boolean applyNav) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            init(activity, true);
            setStatusBar(activity, getLightColor(activity, statusColor, isStatusLight, true), 0, applyNav, getLightColor(activity, navColor, isNavLight, false), 0);
            getLightTheme(activity, statusColor, isStatusLight, true);
            if (applyNav) {
                getLightTheme(activity, navColor, isNavLight, false);
            }
        }
    }

    private static int getLightColor(Activity activity, int color, boolean isLight, boolean isStatusBar) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return color;
        } else {
            if (isMUI()) {
                return color;
            } else if (isMeizu()) {
                // 魅族FlymeUI
                return color;
            } else {
                return getDefaltcolor();
            }
        }
    }

    private static void getLightTheme(Activity activity, int color, boolean isLight, boolean isStatusBar) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isLight) {
                // 修改状态栏图标字体颜色
                if (isStatusBar) {
                    window.getDecorView().setSystemUiVisibility(window.getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                } else {
                    window.getDecorView().setSystemUiVisibility(window.getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
                }
            }
            return;
        } else {
            if (isMUI()) {
                try {
                    Class clazz = window.getClass();
                    Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                    Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                    int darkModeFlag = field.getInt(layoutParams);
                    Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                    if (isLight) {    //状态栏亮色且黑色字体
                        extraFlagField.invoke(window, darkModeFlag, darkModeFlag);
                    } else {       //清除黑色字体
                        extraFlagField.invoke(window, 0, darkModeFlag);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            } else if (isMeizu()) {
                // 修改状态栏图标字体颜色
                // 魅族FlymeUI
                try {
                    WindowManager.LayoutParams lp = window.getAttributes();
                    Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                    Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
                    darkFlag.setAccessible(true);
                    meizuFlags.setAccessible(true);
                    int bit = darkFlag.getInt(null);
                    int value = meizuFlags.getInt(lp);
                    if (isLight) {
                        value |= bit;
                    } else {
                        value &= ~bit;
                    }
                    meizuFlags.setInt(lp, value);
                    window.setAttributes(lp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            } else {
                return;
            }

        }
    }

    private static int getDefaltcolor() {
        return Color.BLACK;
    }

    private static boolean isMeizu() {
        return OSUtils.getRomType() == OSUtils.ROM_TYPE.FLYME;
    }

    private static boolean isMUI() {
        return OSUtils.getRomType() == OSUtils.ROM_TYPE.MIUI;
    }

    private static void setStatusBar(Activity activity, @ColorInt int statusColor, int statusDepth, boolean applyNav,
                                     @ColorInt int navColor, int navDepth) {

        int realStatusDepth = limitDepthOrAlpha(statusDepth);
        Window window = activity.getWindow();
        int finalStatusColor = realStatusDepth == 0 ? statusColor : calculateColor(statusColor, realStatusDepth);
        ViewGroup decorView = (ViewGroup) window.getDecorView();
        for (int i = 0; i < decorView.getChildCount(); i++) {
            View view = decorView.getChildAt(i);
            if (PLACE_VIEW.equals(view.getTag())) {
                decorView.removeView(view);
            }
        }
        decorView.addView(createStatusBarView(activity, finalStatusColor));

        if (applyNav && navigationBarExist(activity)) {
            int realNavDepth = limitDepthOrAlpha(navDepth);
            int finalNavColor = realNavDepth == 0 ? navColor : calculateColor(navColor, realNavDepth);
            decorView.addView(createNavBarView(activity, finalNavColor));
        }
        setRootView(activity, true);
    }

    private static void setRootView(Activity activity, boolean fit) {
        ViewGroup parent = activity.findViewById(android.R.id.content);
        for (int i = 0; i < parent.getChildCount(); i++) {
            View childView = parent.getChildAt(i);
            childView.setFitsSystemWindows(fit);
            if (childView instanceof ViewGroup) {
                ((ViewGroup) childView).setClipToPadding(fit);
            }
        }
    }

    private static View createStatusBarView(Context context, @ColorInt int color) {
        View statusBarView = new View(context);
        statusBarView.setTag(PLACE_VIEW);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams
                (FrameLayout.LayoutParams.MATCH_PARENT, getStatusHeight(context));
        params.gravity = Gravity.TOP;
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(color);
        return statusBarView;
    }

    private static View createNavBarView(Context context, @ColorInt int color) {
        View navBarView = new View(context);
        navBarView.setTag(PLACE_VIEW);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams
                (FrameLayout.LayoutParams.MATCH_PARENT, getVirtualBarHeight(context));
        params.gravity = Gravity.BOTTOM;
        navBarView.setLayoutParams(params);
        navBarView.setBackgroundColor(color);
        return navBarView;
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

    public static int getVirtualBarHeight(Context context) {
        int vh = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            @SuppressWarnings("rawtypes") Class c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked") Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            vh = dm.heightPixels - display.getHeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vh;

    }

    /**
     * 防止颜色深度超出255或者小于0
     *
     * @param depthOrAlpha
     * @return
     */
    private static int limitDepthOrAlpha(int depthOrAlpha) {
        if (depthOrAlpha < 0) {
            return 0;
        }
        if (depthOrAlpha > 255) {
            return 255;
        }
        return depthOrAlpha;
    }

    /**
     * 根据颜色深度获取真正的颜色值
     *
     * @param color
     * @param alpha
     * @return
     */
    @ColorInt
    private static int calculateColor(@ColorInt int color, int alpha) {
        float a = 1 - alpha / 255f;
        int red = color >> 16 & 0xff;
        int green = color >> 8 & 0xff;
        int blue = color & 0xff;
        red = (int) (red * a + 0.5);
        green = (int) (green * a + 0.5);
        blue = (int) (blue * a + 0.5);
        return 0xff << 24 | red << 16 | green << 8 | blue;
    }

    /**
     * 判断导航栏是否存在
     *
     * @param activity
     * @return
     */
    private static boolean navigationBarExist(Activity activity) {
        WindowManager windowManager = activity.getWindowManager();
        Display d = windowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            d.getRealMetrics(realDisplayMetrics);
        }

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }
}
