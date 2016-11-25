package com.lemon95.ymtv.myview;

import android.content.Context;
import android.media.Image;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lemon95.ymtv.R;

/**
 * Created by wuxiaotie on 2016/10/28.
 */
public class MsgView extends RelativeLayout {

    private Context mContext;
    private View loadingView;

    public MsgView(Context context) {
        super(context);
        initView(context);
    }

    public MsgView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public void setMsg(String news,String next) {
        TextView lemon95_new = (TextView)loadingView.findViewById(R.id.lemon95_new);
        TextView lemon95_next = (TextView)loadingView.findViewById(R.id.lemon95_next);
        lemon95_new.setText("正在播放：" + news);
        lemon95_next.setText("即将播放：" + next);
    }

    public void isPuase(boolean isView) {
        ImageView msg_pause = (ImageView)loadingView.findViewById(R.id.msg_pause);
        if (isView) {
            msg_pause.setVisibility(View.VISIBLE);
        } else {
            msg_pause.setVisibility(View.GONE);
        }
    }

    private void initView(Context context) {
        mContext = context;
        loadingView = View.inflate(context, R.layout.live_msg, this);
    }
}
