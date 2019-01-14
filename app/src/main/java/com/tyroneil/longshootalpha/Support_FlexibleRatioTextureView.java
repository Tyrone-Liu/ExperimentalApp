package com.tyroneil.longshootalpha;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

class Support_FlexibleRatioTextureView extends TextureView {

    private int ratioWidth = 0;
    private int ratioHeight = 0;

    /**
     * Three default constructor
     */
    public Support_FlexibleRatioTextureView(Context context) {
        super(context);
    }
    public Support_FlexibleRatioTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public Support_FlexibleRatioTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /**
     * Set the aspect ratio of TextureView, will take effect through the overrode {@code onMeasure()} method.
     * The scale of ratio value does not matter (4:3 is the same with 8:6).
     *
     * @param ratioWidth the width of ratio
     * @param ratioHeight the height of ratio
     */
    void setAspectRatio(int ratioWidth, int ratioHeight) {
        this.ratioWidth = ratioWidth;
        this.ratioHeight = ratioHeight;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);

        if (ratioWidth == 0 || ratioHeight == 0) {
            setMeasuredDimension(width, height);
        } else {
            if (((float) ratioWidth / ratioHeight) < ((float) width / height)) {
                // width is smaller, view's width need to be reduced
                setMeasuredDimension(height * ratioWidth / ratioHeight, height);
            } else {
                // height is smaller, view's height need to be reduced
                setMeasuredDimension(width, width * ratioHeight / ratioWidth);
            }
        }
    }
}
