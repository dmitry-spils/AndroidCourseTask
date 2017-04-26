package com.example.oiltanker.test;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by Oiltanker on 21-Mar-17.
 */

public class SquareImageView extends ImageView {
    public SquareImageView(Context context) {
        super(context);
    }
    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size = 0;
        //if (Math.min(getMeasuredWidth(), getMeasuredHeight()) == 0)
            size = Math.max(getMeasuredWidth(), getMeasuredHeight());
        //else size = Math.min(getMeasuredWidth(), getMeasuredHeight());
        //Log.d("Size", "size = " + size);

        setMeasuredDimension(size, size);
    }
}
