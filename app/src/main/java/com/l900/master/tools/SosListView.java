package com.l900.master.tools;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class SosListView extends ListView {
    public SosListView(Context context) {
        super(context);
    }

    public SosListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SosListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2,
                MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, heightSpec);
    }
}