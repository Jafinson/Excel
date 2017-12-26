package com.jafin.excel.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;

/**
 * Created by 何锦发 on 2017/7/21.
 * 各种方便功能
 */
public class Utils {

    /**
     * 解决
     * Java.lang.ClassCastException: Android.view.ContextThemeWrapper cannot be cast to android.app.Activity
     *
     * @param cont context
     * @return activity
     */
    public static Activity scanForActivity(Context cont) {
        if (cont == null)
            return null;
        else if (cont instanceof Activity)
            return (Activity) cont;
        else if (cont instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper) cont).getBaseContext());

        return null;
    }
}
