package com.lemon95.ymtv.myview;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lemon95.ymtv.R;

/**
 *读取视频的loading界面的配置 
 * 
 */
public class LoadingView extends RelativeLayout {
	
	private Context mContext;
	
	public LoadingView(Context context) {
		super(context);
		initView(context);
	}
	
	public LoadingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}
	
	private void initView(Context context) {
		mContext = context;
		View loadingView = View.inflate(context, R.layout.live_load, this);
		//TextView sdk_ijk_progress_bar_text = (TextView)loadingView.findViewById(R.id.sdk_ijk_progress_bar_text);
	}
}
