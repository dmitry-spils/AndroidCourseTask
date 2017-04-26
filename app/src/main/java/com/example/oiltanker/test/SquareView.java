package com.example.oiltanker.test;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Oiltanker on 23-Mar-17.
 */

public class SquareView extends View {
    public SquareView(Context context) {
        super(context);
    }

    public SquareView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size = 0;
        if (Math.min(getMeasuredWidth(), getMeasuredHeight()) == 0)
            size = Math.max(getMeasuredWidth(), getMeasuredHeight());
        else size = Math.min(getMeasuredWidth(), getMeasuredHeight());
        //Log.d("Size", "size = " + size);

        setMeasuredDimension(size, size);
    }

    /** API is too high! **/
    /*public SquareView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }*/
}
