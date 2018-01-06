package com.jafin.excel.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;



/**


 * 解决HorizontalScrollView自动滑动的问题

 */

public class NoAutoScrollView extends HorizontalScrollView {



    public NoAutoScrollView(Context context) {

        super(context);

    }



    public NoAutoScrollView(Context context, AttributeSet attrs) {

        super(context, attrs);

    }



    public NoAutoScrollView(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);

    }





    @Override

    public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {

        //return super.requestChildRectangleOnScreen(child, rectangle, immediate);

        return true;

    }

}
