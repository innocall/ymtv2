package com.lemon95.ymtv.myview;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.TextView;

import com.lemon95.ymtv.utils.StringUtils;

/**
 * Created by wuxiaotie on 2016/11/1.
 */
public class MarqueeTextView extends TextView {

    /**
     * 是否停止滚动
     */
    private boolean mStopMarquee;
    private String mText;
    private float mCoordinateX;
    private float mTextWidth;

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setText(String text) {
        this.mText = text;
        mTextWidth = getPaint().measureText(mText);
        if (mHandler.hasMessages(0))
            mHandler.removeMessages(0);
        mHandler.sendEmptyMessageDelayed(0, 2000);
    }

    @Override
    protected void onAttachedToWindow() {
        mStopMarquee = false;
        if (!StringUtils.isBlank(mText))
            mHandler.sendEmptyMessageDelayed(0, 2000);
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        mStopMarquee = true;
        if (mHandler.hasMessages(0))
            mHandler.removeMessages(0);
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!StringUtils.isBlank(mText))
            canvas.drawText(mText, mCoordinateX, 15, getPaint());
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (Math.abs(mCoordinateX) > (mTextWidth + 100)) {
                        mCoordinateX = 0;
                        invalidate();
                        if (!mStopMarquee) {
                            sendEmptyMessageDelayed(0, 2000);
                        }
                    } else {
                        mCoordinateX -= 1;
                        invalidate();
                        if (!mStopMarquee) {
                            sendEmptyMessageDelayed(0, 30);
                        }
                    }

                    break;
            }
            super.handleMessage(msg);
        }
    };

}
